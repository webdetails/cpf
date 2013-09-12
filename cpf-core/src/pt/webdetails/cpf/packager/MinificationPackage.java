package pt.webdetails.cpf.packager;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.List;
import java.util.regex.Pattern;

import org.apache.commons.io.FilenameUtils;
import org.apache.commons.io.IOUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import pt.webdetails.cpf.packager.Concatenate.IBFileContentProcessor;
import pt.webdetails.cpf.repository.api.IBasicFile;
import pt.webdetails.cpf.repository.api.IRWAccess;
import pt.webdetails.cpf.repository.util.RepositoryHelper;
import pt.webdetails.cpf.utils.CharsetHelper;

public class MinificationPackage {
  
  protected Log logger = LogFactory.getLog(MinificationPackage.class);
  
  private String latestVersionChecksum;
  private Packager.Filetype filetype;
  
  private IRWAccess writer;
  private String outLocation;
  private boolean dirty;

  private Iterable<IBasicFile> bFiles;

  private IBFileContentProcessor fileProcessor;
  
  /**
   * Minifies a group of files into a single one.
   * Input and output files must be
   * @param filetype CSS | JS
   * @param repositoryAccess for reading files and writing outputFile
   * @param outputFile path of the outputFile from the repositoryAccess
   * @param files files to be minified
   */
  public MinificationPackage(
      Packager.Filetype filetype,
      IRWAccess repositoryAccess,
      String outputFile,
      String[] files)
  {
    
  }

  /**
   * Minifies a group of files into a single one.
   * Input and output files must be from the same repository access.
   * Any paths referenced by CSS must be inside root static folders
   * @param filetype CSS | JS
   * @param repositoryAccess for reading files and writing {@code outputFile}
   * @param outputFile path of the outputFile from the repository access 
   * @param files files obtained with {@code repositoryAccess}
   */
  public MinificationPackage(
      Packager.Filetype filetype,
      IRWAccess repositoryAccess,
      String outputFile,
      List<IBasicFile> files)
  {
      outLocation = outputFile;
      writer = repositoryAccess;
      bFiles = files;
      if (filetype == Packager.Filetype.CSS) {
        this.fileProcessor = new CssUrlReplacer(FilenameUtils.getPath(outputFile));
      }
  }

  /**
   * 
   * @return
   * @throws IOException
   */
  private String minify() throws IOException {
    InputStream concatenatedStream = null;
    try {
      ByteArrayOutputStream bytesOut = new ByteArrayOutputStream();
      Reader reader;
      switch (this.filetype) {
        case JS:
          concatenatedStream = Concatenate.concatenate(bFiles, fileProcessor);// Concatenate.concatenate(bFiles, urlProcessor);
          reader = new InputStreamReader(concatenatedStream, CharsetHelper.getEncoding());
          JSMin jsmin = new JSMin(concatenatedStream, bytesOut);
          jsmin.jsmin();
          break;
        case CSS:
          concatenatedStream = Concatenate.concatenate(bFiles, fileProcessor);
          reader = new InputStreamReader(concatenatedStream, CharsetHelper.getEncoding());
          IOUtils.copy(reader, bytesOut);
          break;
      }
      // TODO: output, save file
      byte[] fileContent = bytesOut.toByteArray();
      writer.saveFile(outLocation, new ByteArrayInputStream(fileContent));

      dirty = false;
      latestVersionChecksum = digestMd5(fileContent);
      return latestVersionChecksum;
    } catch (Exception ex) {
      logger.fatal(ex);
      return null;
    }
    finally {
      IOUtils.closeQuietly(concatenatedStream);
    }
  }

  private String digestMd5(byte[] contents) {
    try {
      return byteToHex(MessageDigest.getInstance("MD5").digest(contents));
    } catch (NoSuchAlgorithmException e) {
      logger.fatal(e);
      return null;
    }
  }

  private String byteToHex(byte[] bytes) {
    StringBuffer hexString = new StringBuffer();
    for (int i = 0; i < bytes.length; i++) {
      String byteValue = Integer.toHexString(0xFF & bytes[i]);
      hexString.append(byteValue.length() == 2 ? byteValue : "0" + byteValue);
    }
    return hexString.toString();
  }

  public String update() throws IOException, NoSuchAlgorithmException {
    return update(false);
  }

  public synchronized String update(boolean force) throws IOException, NoSuchAlgorithmException {
    long lastModified = writer.getLastModified(outLocation);
     // If we're not otherwise sure we must update, we actively check if the
     //minified file is older than any file in the set.
    if (!dirty && !force) {
      for (IBasicFile file : bFiles) {
        if (lastModified == 0L || writer.getLastModified(file.getPath()) > lastModified) {
          this.dirty = true;
          break;
        }
      }
    }
    return (dirty || force) ? this.minify() : this.latestVersionChecksum;
  }

  public static class CssUrlReplacer implements IBFileContentProcessor {

    private String rootPath;
    /** 
     * this bugger was finally deprecated in IE9;
     * IE7/8 support native png transparency w/o opacity modifiers
     * we use it for fancybox's ie variants;
     * this should probably be removed
     **/
    private static final Pattern thyOldeIEUrl =
        Pattern.compile("(progid:DXImageTransform.Microsoft.AlphaImageLoader\\(src=')");
    private static final Pattern cssUrl = Pattern.compile("(url\\(['\"]?)");
    private static final Log logger = LogFactory.getLog(CssUrlReplacer.class);
    
    public CssUrlReplacer(String rootPath) {
      if (!StringUtils.isEmpty(rootPath)) {
        this.rootPath = rootPath.replaceAll("\\\\","/").replaceAll("/+","/");
      }
      else {
        rootPath = null;
      }
    }

    private String replaceUrls(String fileContents, String newLocation) {
        String replacement = "$1" + newLocation;
        String replacedContents = cssUrl.matcher(fileContents).replaceAll(replacement);
        replacedContents = makeSillyIEReplacement(newLocation, replacedContents);
        return replacedContents;
    }

    /**
     * When this is removed there will be much rejoicing.
     */
    private String makeSillyIEReplacement(String newLocation, String replacedContents) {
      String replacement = newLocation;
      // src attribute for AlphaImageLoader is relative to the page where the css will be used
      //instead of the css file itself (go microsoft!).
      // They will be on the same level for CDE/CDF's content generators,
      //so as long as the new location starts with '../' we'll be fine;
      if (!StringUtils.startsWith(replacement, "..")) {
        String firstFolder = replacement.substring(0, replacement.indexOf("/"));
        firstFolder = RepositoryHelper.appendPath("..", firstFolder);
        replacement = RepositoryHelper.appendPath(firstFolder, replacement);
      }
      // however, the original files where this is used assume it will be referenced in the parent folder
      // hence the need for this misterious final '../'.
      // on the bright side, this will produce some humurous paths
      replacement = RepositoryHelper.appendPath(replacement, "../");
      replacedContents = thyOldeIEUrl.matcher(replacedContents).replaceAll("$1" + replacement);
      return replacedContents;
    }

    //TODO: processContents(IBasicFile) -> stream ?
    @Override
    public String processContents(final String fileContents, IBasicFile currentFile) {
        if (rootPath == null) {
          return fileContents;
        }
        try {
          String newLocation = RepositoryHelper.relativizePath(rootPath, currentFile.getPath(), true);
          // get path only
          newLocation = StringUtils.chomp(newLocation, currentFile.getName());
          return replaceUrls(fileContents, newLocation);
        }
        catch (Exception e) {
          logger.error("error changing paths for");//TODO:
          return fileContents;
        }
    }

  }
}

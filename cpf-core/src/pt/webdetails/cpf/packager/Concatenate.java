/* This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this file,
 * You can obtain one at http://mozilla.org/MPL/2.0/. */

package pt.webdetails.cpf.packager;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.io.SequenceInputStream;
import java.io.UnsupportedEncodingException;
import java.util.Enumeration;
import java.util.NoSuchElementException;

import org.apache.commons.io.FileUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import pt.webdetails.cpf.utils.CharsetHelper;

/**
 *
 * @author pdpi
 */
public class Concatenate {
  private static final Log logger = LogFactory.getLog(Concatenate.class);

  public static InputStream concat(File[] files) {
    ListOfFiles mylist = new ListOfFiles(files);

    return new SequenceInputStream(mylist);
  }

  /**
   * @deprecated
   * Use {@link#concatenate(File[] files, FileContentProcessor transformer)}
   */
  public static InputStream concat(File[] files, String rootPath) {
    return StringUtils.isEmpty(rootPath)
        ? concatenate(files, null) 
        : concatenate(files, new DashboardUrlReplacer(rootPath));
  }

  public static InputStream concatenate(File[] files, FileContentProcessor transformer) {
    if (transformer == null) {
      return concat(files);
    }
    try {
      StringBuffer buffer = new StringBuffer();
      for (File file : files) {
        try {
          // TODO: do we need to support bigger files here?
          String contents = FileUtils.readFileToString(file);
          buffer.append(transformer.processContents(contents, file));
        }
        catch (FileNotFoundException e) {
          logger.error("concat: File " + file.getAbsolutePath()
              + " doesn't exist! Skipping...");
        }
        catch (Exception e) {
          logger.error("concat: Error while attempting to concatenate file "
              + file.getAbsolutePath() + ". Attempting to continue", e);
        }
      }
      return new ByteArrayInputStream(buffer.toString().getBytes(CharsetHelper.getEncoding()));
    } catch (UnsupportedEncodingException e) {
      logger.error(e);
      return null;
    }
  }

  // we need something like this to unify cde's and cdf's Concatenate
  public interface FileContentProcessor {
    String processContents(String fileContents, File currentFile);
  }

  /**
   * The stuff CDF does to files, whatever that is
   * TODO: move back to CDF
   */
    //TODO make a CDE one
  public static class DashboardUrlReplacer implements FileContentProcessor {
    private String rootPath;
    
    public DashboardUrlReplacer(String rootPath) {
      if (!StringUtils.isEmpty(rootPath)) {
        this.rootPath = rootPath.replaceAll("\\\\","/").replaceAll("/+","/");
      }
      else {
        rootPath = null;
      }
    }

    @Override
    public String processContents(String contents, File file) {
      if (rootPath == null) {
        return contents;
      }
      String fileLocation = file.getPath().replaceAll("\\\\","/").replaceAll(file.getName(), "").replaceAll(rootPath, "..");
      return contents
          .replaceAll("(url\\(['\"]?)", "$1" + fileLocation) // Standard URLs
          //TODO: does anyone ever use this?
          .replaceAll("(progid:DXImageTransform.Microsoft.AlphaImageLoader\\(src=')", "$1" + fileLocation + "../"); // these are IE-Only
    }

  }

  private static class ListOfFiles implements Enumeration<FileInputStream> {

    private File[] listOfFiles;
    private int current = 0;

    public ListOfFiles(File[] listOfFiles) {
      this.listOfFiles = listOfFiles;
    }

    public boolean hasMoreElements() {
      return current < listOfFiles.length;
    }

    public FileInputStream nextElement() {
      FileInputStream in = null;

      if (!hasMoreElements()) {
        throw new NoSuchElementException("No more files.");
      } else {
        File nextElement = listOfFiles[current];
        current++;
        try {
          in = new FileInputStream(nextElement);
        } catch (FileNotFoundException e) {
          logger.error("ListOfFiles: Can't open " + nextElement);
        }
      }
      return in;
    }
  }
}

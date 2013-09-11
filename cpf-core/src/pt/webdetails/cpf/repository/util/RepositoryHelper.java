package pt.webdetails.cpf.repository.util;

import java.io.File;
import java.io.FilenameFilter;
import java.io.IOException;
import java.net.URL;
import java.util.Enumeration;
import java.util.regex.Pattern;

import org.apache.commons.io.FilenameUtils;
import org.apache.commons.lang.StringUtils;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import pt.webdetails.cpf.impl.DefaultRepositoryFile;
import pt.webdetails.cpf.repository.IRepositoryFile;
import pt.webdetails.cpf.repository.IRepositoryFileFilter;
import pt.webdetails.cpf.repository.api.IBasicFile;

public class RepositoryHelper {

  private RepositoryHelper() {}
  private static final char SEPARATOR = '/';

  /**
   * differs from {@link FilenameUtils#concat(String, String)} in that the second
   * path will never be treated as absolute;
   * @param first can be null
   * @param second should be something!
   * @return normalized concatenation of both paths
   */
  public static String appendPath(String first, String second) {
    if (StringUtils.isEmpty(first)) {
      return second;
    }
    int sepCount = 0;
    if (first.charAt(first.length() - 1) == SEPARATOR) {
      sepCount++;
    }
    if(second.charAt(0) == SEPARATOR) {
      sepCount++;
    }
    String result;
    switch (sepCount) {
      case 0:
        result = first + SEPARATOR + second;
        break;
      case 2:
        result = first + second.substring(1);
        break;
      default:
        result = first + second;
        break;
    }
    //get rid of ..|.
    return FilenameUtils.normalize(result);
  }

  public static StringBuilder appendPath(StringBuilder builder, String path) {
    int sepCount = 0;
    if (builder.length() > 0 && builder.charAt(builder.length() - 1) ==  SEPARATOR) {
      sepCount++;
    }
    if (path.charAt(0) == SEPARATOR) {
      sepCount++;
    }
    switch (sepCount) {
      case 2:
        return builder.append(path.substring(1));
      case 0:
        builder.append(SEPARATOR);//fall
      default:
        return builder.append(path);
    }
  }

  public static String joinPaths(String...paths) {
    StringBuilder builder = new StringBuilder();
    for (String path : paths) {
      if(!StringUtils.isEmpty(path)) {
        appendPath(builder, path);
      }
    }
    return builder.toString();
  }

  /**
   * Similar to {@link ClassLoader#getResource(String)}, except the lookup
   *  is in reverse order.<br>
   *  i.e. returns the resource from the supplied classLoader or the
   *  one closest to it in the hierarchy, instead of the closest to the root
   *  class loader
   * @param classLoader The class loader to fetch from
   * @param path The resource path relative to url base
   * @return A URL object for reading the resource, or null if the resource
   * could not be found or the invoker doesn't have adequate privileges to get
   * the resource.
   * @see ClassLoader#getResource(String)
   * @see ClassLoader#getResources(String)
   */

  public static URL getClosestResource(ClassLoader classLoader, String path) {
    URL resource = null;
    try {
        // The last resource will be from the nearest ClassLoader.
        Enumeration<URL> resourceCandidates =
            classLoader.getResources(path);
        while (resourceCandidates.hasMoreElements()) {
            resource = resourceCandidates.nextElement();
        }
    } catch (IOException ioe) {
        // ignore exception - it's OK if file is not found
    }
    return resource;
  }

  //TODO: delete?
  public IRepositoryFile getAsRepositoryFile(File file) {
    return new DefaultRepositoryFile(file);
  }
  
  public FilenameFilter getAsFilenameFilter(final IRepositoryFileFilter filter) {
    return new FilenameFilter() {
      @Override
      public boolean accept(File dir, String name) {
        return filter.accept(new DefaultRepositoryFile(new File(dir, name)));
      }
    };
  }
  
  /**
   * Make one path relative to the other.
   * @param basePath base directory from which 
   * @param fullPath targetPath is calculated to this file
   * @return fullPath as a relative path to basePath
   * 
   * TODO: simplified version of what's in cde, still untested
   */
  public static String relativizePath(String basePath, String fullPath) {

      final String pathSeparator = "/";
      // Normalize the paths
      String normalizedTargetPath = FilenameUtils.normalizeNoEndSeparator(fullPath);
      String normalizedBasePath = FilenameUtils.normalizeNoEndSeparator(basePath);

      String[] base = normalizedBasePath.split(Pattern.quote(pathSeparator));
      String[] target = normalizedTargetPath.split(Pattern.quote(pathSeparator));

      // First get all the common elements. Store them as a string,
      // and also count how many of them there are.
      StringBuilder common = new StringBuilder();

      int commonIndex = 0;
      while (commonIndex < target.length && commonIndex < base.length
              && target[commonIndex].equals(base[commonIndex])) {
          common.append(target[commonIndex]);
          common.append(pathSeparator);
          commonIndex++;
      }

      if (commonIndex == 0) {
          // No single common path element.
          // These paths cannot be relativized.
          throw new IllegalArgumentException(
              "No common path element found for '" + normalizedTargetPath
              + "' and '" + normalizedBasePath + "'");
      }

      StringBuilder relative = new StringBuilder();
      if (base.length != commonIndex) {
          int numDirsUp = base.length - commonIndex;

          for (int i = 0; i < numDirsUp; i++) {
              relative.append(".." + pathSeparator);
          }
      }
      relative.append(normalizedTargetPath.substring(common.length()));
      return relative.toString();
  }

  public static String toJQueryFileTree(String baseDir, IBasicFile[] files) {
    StringBuilder out = new StringBuilder();
    out.append("<ul class=\"jqueryFileTree\" style=\"display: none;\">");

    for (IBasicFile file : files) {
      if (file.isDirectory()) {
        out.append("<li class=\"directory collapsed\"><a href=\"#\" rel=\"" + baseDir + file.getName() + "/\">" + file.getName() + "</a></li>");
      }
    }

    for (IBasicFile file : files) {
      if (!file.isDirectory()) {
        int dotIndex = file.getName().lastIndexOf('.');
        String ext = dotIndex > 0 ? file.getName().substring(dotIndex + 1) : "";
        out.append("<li class=\"file ext_" + ext + "\"><a href=\"#\" rel=\"" + baseDir + file.getName() + "\">" + file.getName() + "</a></li>");
      }
    }
    out.append("</ul>");
    return out.toString();
  }

  public static String toJSON(String baseDir, IBasicFile[] files) throws JSONException {

    JSONArray arr = new JSONArray();

    for (IBasicFile file : files) {
      JSONObject json = new JSONObject();
      json.put("path", baseDir);
      json.put("name", file.getName());
      json.put("label", file.getName());

      if (file.isDirectory()) {
        json.put("type", "dir");
      } else {
        int dotIndex = file.getName().lastIndexOf('.');
        String ext = dotIndex > 0 ? file.getName().substring(dotIndex + 1) : "";
        json.put("ext", ext);
        json.put("type", "file");
      }
      arr.put(json);
    }

    return arr.toString();
  }
}

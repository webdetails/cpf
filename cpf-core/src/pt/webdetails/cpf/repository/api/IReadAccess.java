package pt.webdetails.cpf.repository.api;

import java.io.IOException;
import java.io.InputStream;
import java.util.List;

/**
 * Access a file repository for read-only purposes.<br>
 * All paths should use '/' as separator.
 */
public interface IReadAccess {

  public static final int DEPTH_ZERO = 0;
  public static final int DEPTH_ALL = -1;

  /**
   * @param path to file relative path from base dir
   * @return {@link InputStream} to the file
   */
  InputStream getFileInputStream(String path) throws IOException;

  /**
   * @param path to file relative path from base dir
   * @return whether file exists
   */
  boolean fileExists(String path);

  /**
   * @param path to file relative path from base dir 
   * @return Date of last modification
   * @see {@link java.io.File#lastModified}
   */
  long getLastModified(String path);

  /**
   * (optional)
   * @param path
   * @param filter (optional)
   * @param maxDepth -1 for ANY, [1..N] to limit depth
   * @return Files under path matching filter
   */
  List<IBasicFile> listFiles(String path, IBasicFileFilter filter, int maxDepth);

  /**
   * (optional)
   * @param path
   * @param filter (optional)
   * @return Files under path matching filter
   */
  List<IBasicFile> listFiles(String path, IBasicFileFilter filter);

  /**
   * @param path to file relative path from base dir 
   * @return
   */
  IBasicFile fetchFile(String path);
}

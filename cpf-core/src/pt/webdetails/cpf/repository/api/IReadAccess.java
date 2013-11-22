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
   * (optional)<br>
   * @param path
   * @param filter (optional)
   * @param maxDepth -1 for ANY, [1..N] to limit depth
   * @param includeDirs if directories should be tested and included as well.
   * @param showHiddenFilesAndFolders true if list of IBasicFile returned should also include files/folders marked as hidden, false otherwise
   * @return Files under path matching filter
   */
  List<IBasicFile> listFiles(String path, IBasicFileFilter filter, int maxDepth, boolean includeDirs, boolean showHiddenFilesAndFolders);
  
  /**
   * (optional)<br>
   * @param path
   * @param filter (optional)
   * @param maxDepth -1 for ANY, [1..N] to limit depth
   * @param includeDirs if directories should be tested and included as well.
   * @return Files under path matching filter
   */
  List<IBasicFile> listFiles(String path, IBasicFileFilter filter, int maxDepth, boolean includeDirs);

  /**
   * @deprecated use {@link #listFiles(String, IBasicFileFilter, int, boolean, boolean)}, we're not going to have every combination
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

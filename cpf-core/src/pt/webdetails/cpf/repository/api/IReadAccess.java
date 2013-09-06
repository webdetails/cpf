package pt.webdetails.cpf.repository.api;

import java.io.IOException;
import java.io.InputStream;
import java.util.List;

public interface IReadAccess {
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

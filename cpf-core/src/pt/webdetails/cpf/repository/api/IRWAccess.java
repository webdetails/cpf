package pt.webdetails.cpf.repository.api;

import java.io.InputStream;

public interface IRWAccess extends IReadAccess {

  /**
   * Save a file.
   * @param path file path, intermediate folders will be created if not present.
   * @param contents
   * @return if file was saved
   */
  boolean saveFile(String path, InputStream contents);

  /**
   * Behaves as {@link #getFileInputStream(String)} from origin and
   * {@link #saveFile(String, InputStream)} to destination.
   * 
   * @param pathFrom
   *          path to file to copy
   * @param pathTo
   *          path to destination file (must include file name)
   * @return if copied ok
   */
  boolean copyFile(String pathFrom, String pathTo);

  /**
   * @param path file to delete
   * @return if file was there and was deleted
   */
  boolean deleteFile(String path);

  /**
   * Creates a folder. Will recursively create intermediate folders that don't exist.
   * @param path directory path
   * @return if was created ok
   */
  boolean createFolder(String path);

}

/*! ******************************************************************************
 *
 * Pentaho
 *
 * Copyright (C) 2024 by Hitachi Vantara, LLC : http://www.pentaho.com
 *
 * Use of this software is governed by the Business Source License included
 * in the LICENSE.TXT file.
 *
 * Change Date: 2029-07-20
 ******************************************************************************/

package pt.webdetails.cpf.repository.api;

import java.io.InputStream;

public interface IRWAccess extends IReadAccess {

  /**
   * Save a file.
   *
   * @param path     file path, intermediate folders will be created if not present.
   * @param contents
   * @return if file was saved
   */
  boolean saveFile( String path, InputStream contents );

  /**
   * Behaves as {@link #getFileInputStream(String)} from origin and {@link #saveFile(String, InputStream)} to
   * destination.
   *
   * @param pathFrom path to file to copy
   * @param pathTo   path to destination file (must include file name)
   * @return if copied ok
   */
  boolean copyFile( String pathFrom, String pathTo );

  /**
   * @param path file to delete
   * @return if file was there and was deleted
   */
  boolean deleteFile( String path );

  /**
   * Creates a folder. Will recursively create intermediate folders that don't exist.
   *
   * @param path directory path
   * @return if was created ok
   */
  boolean createFolder( String path );

  /**
   * Creates a folder. Will recursively create intermediate folders that don't exist
   * and hide them if the folder being created is hidden.
   *
   * @param path     directory path
   * @param isHidden if folder is hidden
   * @return if was created ok
   */
  boolean createFolder( String path, boolean isHidden );

}

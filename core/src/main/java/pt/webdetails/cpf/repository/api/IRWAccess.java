/*!
* Copyright 2002 - 2017 Webdetails, a Hitachi Vantara company. All rights reserved.
*
* This software was developed by Webdetails and is provided under the terms
* of the Mozilla Public License, Version 2.0, or any later version. You may not use
* this file except in compliance with the license. If you need a copy of the license,
* please go to http://mozilla.org/MPL/2.0/. The Initial Developer is Webdetails.
*
* Software distributed under the Mozilla Public License is distributed on an "AS IS"
* basis, WITHOUT WARRANTY OF ANY KIND, either express or implied. Please refer to
* the license for the specific language governing your rights and limitations.
*/
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

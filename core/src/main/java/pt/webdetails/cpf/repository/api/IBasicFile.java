/*! ******************************************************************************
 *
 * Pentaho
 *
 * Copyright (C) 2024 by Hitachi Vantara, LLC : http://www.pentaho.com
 *
 * Use of this software is governed by the Business Source License included
 * in the LICENSE.TXT file.
 *
 * Change Date: 2028-08-13
 ******************************************************************************/
package pt.webdetails.cpf.repository.api;

import java.io.IOException;
import java.io.InputStream;

/**
 * Basic file info and its contents.
 */
public interface IBasicFile {

  /**
   * Opens a stream, don't forget to close it!
   *
   * @return stream to file contents
   */
  InputStream getContents() throws IOException;

  /**
   * @return just the name of the file, with extension
   */
  String getName();

  /**
   * Full path for repository type used.<br> For display purposes only.
   *
   * @return path and filename
   */
  String getFullPath();

  /**
   * Path for the RepositoryAccess that supplied it.
   *
   * @return path and filename
   */
  String getPath();

  /**
   * @return the extension, lower case, no dot, or empty if not there. the extension is whatever comes after the last
   * dot of the file name, if the dot isn't the first char
   */
  String getExtension();

  /**
   * @return true is this is this path relates to a directory, false otherwise (i.e. relates to a file)
   */
  boolean isDirectory();
}

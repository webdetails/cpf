/*!
 * Copyright 2002 - 2017 Webdetails, a Hitachi Vantara company.  All rights reserved.
 *
 * This software was developed by Webdetails and is provided under the terms
 * of the Mozilla Public License, Version 2.0, or any later version. You may not use
 * this file except in compliance with the license. If you need a copy of the license,
 * please go to  http://mozilla.org/MPL/2.0/. The Initial Developer is Webdetails.
 *
 * Software distributed under the Mozilla Public License is distributed on an "AS IS"
 * basis, WITHOUT WARRANTY OF ANY KIND, either express or  implied. Please refer to
 * the license for the specific language governing your rights and limitations.
 */
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

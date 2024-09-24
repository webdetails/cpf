/*!
 * Copyright 2002 - 2018 Webdetails, a Hitachi Vantara company.  All rights reserved.
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
package pt.webdetails.cpf.api;

import pt.webdetails.cpf.repository.api.IUserContentAccess;

/**
 * For user interaction with the repository. Should always check permissions.<br>
 * 
 * @see {@link pt.webdetails.cpf.repository.api.IReadAccess} {@link pt.webdetails.cpf.repository.api.IRWAccess} {@link pt.webdetails.cpf.repository.api.IACAccess}
 */
public interface IUserContentAccessExtended extends IUserContentAccess {

  /**
   * Saves a file.
   * 
   * @param IFileContent contents of file to be saved
   * @return true if file was saved, false otherwise
   */
  boolean saveFile( IFileContent file );

}

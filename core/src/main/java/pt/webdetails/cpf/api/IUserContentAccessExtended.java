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

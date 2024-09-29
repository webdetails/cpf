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

package pt.webdetails.cpf.api;

import pt.webdetails.cpf.repository.api.IBasicFile;

public interface IFileContent extends IBasicFile {

  /**
   * @return the title of the file
   */
  String getTitle();

  /**
   * @return the description of the file
   */
  String getDescription();

  /**
   * @return flag hidden
   */
  boolean isHidden();

}

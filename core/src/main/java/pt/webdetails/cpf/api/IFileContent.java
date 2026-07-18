/*! ******************************************************************************
 *
 * Pentaho
 *
 * Copyright (C) 2024 - 2026 by Pentaho Canada Inc. : http://www.pentaho.com
 *
 * Use of this software is governed by the Business Source License included
 * in the LICENSE.TXT file.
 *
 * Change Date: 2030-06-15
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

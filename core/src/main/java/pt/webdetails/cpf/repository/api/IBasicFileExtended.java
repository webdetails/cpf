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


package pt.webdetails.cpf.repository.api;

/**
 * Extended Basic file info and its contents.
 */
public interface IBasicFileExtended extends IBasicFile {

  /**
   * @return the title of the file
   */
  String getTitle();

  /**
   * @return the create date of the file
   */
  String getCreatedDate();

  /**
   * @return the modified date of the file
   */
  String getLastModifiedDate();

  /**
   * @return the size of the file in bytes
   */
  long getFileSize();

  /**
   * @return the owner of the file
   */
  String getOwner();

  /**
   * @return the description of the file
   */
  String getDescription();
}

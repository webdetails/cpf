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

public interface IACAccess {

  /**
   * Access control check
   *
   * @param filePath path to file within this content access
   * @param access   access type
   * @return if current user has access
   */
  boolean hasAccess( String filePath, FileAccess access );

}

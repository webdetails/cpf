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

import org.apache.commons.lang.StringUtils;


public enum FileAccess {

  //TODO: last checked in RepositoryFilePermissions:
  //      READ, WRITE, DELETE,  ACL_MANAGEMENT,ALL
  READ,
  WRITE,
  EXECUTE,
  DELETE;
  //    @Deprecated
  //    CREATE,
  //    NONE;

  public static FileAccess parse( String fileAccess ) {
    try {
      return FileAccess.valueOf( StringUtils.upperCase( fileAccess ) );
    } catch ( Exception e ) {
      return null;
    }
  }
}


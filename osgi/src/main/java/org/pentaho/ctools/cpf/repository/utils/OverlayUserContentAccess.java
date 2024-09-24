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
package org.pentaho.ctools.cpf.repository.utils;

import pt.webdetails.cpf.api.IFileContent;
import pt.webdetails.cpf.api.IUserContentAccessExtended;
import pt.webdetails.cpf.repository.api.FileAccess;
import pt.webdetails.cpf.repository.api.IReadAccess;

import java.util.List;

public class OverlayUserContentAccess extends OverlayAccess<IUserContentAccessExtended> implements IUserContentAccessExtended {

  public OverlayUserContentAccess( String basePath, IUserContentAccessExtended writeAccess, List<IReadAccess> readAccessList ) {
    super( basePath, writeAccess, readAccessList );
  }

  @Override
  public boolean saveFile( IFileContent file ) {
    return this.writeAccess.saveFile( file );
  }

  @Override
  public boolean hasAccess( String filePath, FileAccess access ) {
    if ( this.writeAccess.hasAccess( filePath, access ) ) {
      return true;
    } else {
      for ( IReadAccess readAccess : readAccessList ) {
        if ( readAccess.fileExists( filePath ) ) {
          return true;
        }
      }
    }
    return false;
  }
}

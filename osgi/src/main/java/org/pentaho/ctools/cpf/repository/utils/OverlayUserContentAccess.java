/*!
 * Copyright 2018 Webdetails, a Hitachi Vantara company. All rights reserved.
 *
 * This software was developed by Webdetails and is provided under the terms
 * of the Mozilla Public License, Version 2.0, or any later version. You may not use
 * this file except in compliance with the license. If you need a copy of the license,
 * please go to http://mozilla.org/MPL/2.0/. The Initial Developer is Webdetails.
 *
 * Software distributed under the Mozilla Public License is distributed on an "AS IS"
 * basis, WITHOUT WARRANTY OF ANY KIND, either express or implied. Please refer to
 * the license for the specific language governing your rights and limitations.
 */
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

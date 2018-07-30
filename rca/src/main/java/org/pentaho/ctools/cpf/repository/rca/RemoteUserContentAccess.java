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
package org.pentaho.ctools.cpf.repository.rca;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.pentaho.ctools.cpf.repository.rca.dto.StringKeyStringValueDto;
import pt.webdetails.cpf.api.IFileContent;
import pt.webdetails.cpf.api.IUserContentAccessExtended;
import pt.webdetails.cpf.repository.api.FileAccess;

import javax.ws.rs.client.Entity;
import javax.ws.rs.core.GenericEntity;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class RemoteUserContentAccess extends RemoteReadWriteAccess implements IUserContentAccessExtended {

  private static final Log logger = LogFactory.getLog( RemoteReadWriteAccess.class );

  public RemoteUserContentAccess( String reposURL, String username, String password ) {
    super( reposURL, username, password );
  }

  @Override
  public boolean hasAccess( String filePath, FileAccess access ) {
    //TODO: dummy implementation
    return fileExists( filePath );
  }

  @Override
  public boolean saveFile( IFileContent file ) {
    try {
      if ( saveFile( file.getPath(), file.getContents() ) ) {
        if ( file.isHidden() ) {
          return makeHidden( file.getPath() ) && updateProperties( file );
        } else {
          return updateProperties( file );
        }
      }
    } catch ( IOException ex ) {
      logger.error( ex );
    }
    return false;
  }

  private boolean updateProperties( IFileContent file ) {
    final String defaultLocale = "default"; // use default locale
    List<StringKeyStringValueDto> properties = new ArrayList<>();
    properties.add( new StringKeyStringValueDto( "file.title", file.getTitle() ) );
    properties.add( new StringKeyStringValueDto( "file.description", file.getDescription() ) );

    GenericEntity<List<StringKeyStringValueDto>> entity = new GenericEntity<List<StringKeyStringValueDto>>( properties )
    {
    };

    String requestURL = createRequestURL( "/api/repo/files/", file.getPath(), "localeProperties" );
    Response response = client.target( requestURL )
      .queryParam( "locale", defaultLocale )
      .request( MediaType.APPLICATION_XML )
      .put( Entity.xml( entity ) );

    //TODO: handle non-OK status codes? log? exception?
    return response.getStatus() == Response.Status.OK.getStatusCode();
  }
}

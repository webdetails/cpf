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
import org.pentaho.platform.api.repository2.unified.RepositoryFilePermission;
import org.pentaho.platform.api.repository2.unified.webservices.StringKeyStringValueDto;
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

/**
 * Class {@code RemoteUserContentAccess} provides an implementation of {@code IUserContentAccessExtended} via REST calls to the Pentaho Server.
 *
 * @see IUserContentAccessExtended
 */
public class RemoteUserContentAccess extends RemoteReadWriteAccess implements IUserContentAccessExtended {

  private static final Log logger = LogFactory.getLog( RemoteReadWriteAccess.class );

  public RemoteUserContentAccess( String reposURL, String username, String password ) {
    super( reposURL, username, password );
  }

  public RemoteUserContentAccess( String basePath, String reposURL, String username, String password ) {
    super( basePath, reposURL, username, password );
  }

  @Override
  public boolean hasAccess( String filePath, FileAccess access ) {
    String requestURL = createRequestURL( filePath, "canAccess" );

    String response = null;
    try {
      response = client
        .target( requestURL )
        .queryParam( "permissions", encodeFileAccess( access ) )
        .request( MediaType.TEXT_PLAIN ).get( String.class );
    } catch ( Exception ex ) {
      logger.error( ex );
      return false;
    }

    return response != null && Boolean.parseBoolean( response );
  }

  private int encodeFileAccess( FileAccess access ) {
    int permission = RepositoryFilePermission.ALL.ordinal();

    switch ( access ) {
      case EXECUTE:
      case READ: {
        permission = RepositoryFilePermission.READ.ordinal();
        break;
      }
      case WRITE: {
        permission = RepositoryFilePermission.WRITE.ordinal();
        break;
      }
      case DELETE: {
        permission = RepositoryFilePermission.DELETE.ordinal();
        break;
      }
    }

    return permission;
  }

  @Override
  public boolean saveFile( IFileContent file ) {
    try {
      if ( saveFile( file.getPath(), file.getContents() ) ) {
        if ( file.isHidden() ) {
          String path = file.getPath();
          String fullPath = buildPath( path );
          return makeHidden( fullPath ) && updateProperties( file );
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
    String path = file.getPath();
    String fullPath = buildPath( path );
    String requestURL = createRequestURL( "/api/repo/files/", fullPath, "localeProperties" );
    Response response = client.target( requestURL )
      .queryParam( "locale", defaultLocale )
      .request( MediaType.APPLICATION_XML )
      .put( Entity.xml( entity ) );

    //TODO: handle non-OK status codes? log? exception?
    return response.getStatus() == Response.Status.OK.getStatusCode();
  }
}

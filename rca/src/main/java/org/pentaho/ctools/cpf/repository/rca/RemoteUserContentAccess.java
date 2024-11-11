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

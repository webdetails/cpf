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

package pt.webdetails.cpf.repository.rca;

import com.sun.jersey.api.client.ClientResponse;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.pentaho.platform.api.repository2.unified.RepositoryFilePermission;
import org.pentaho.platform.api.repository2.unified.webservices.StringKeyStringValueDto;
import pt.webdetails.cpf.api.IFileContent;
import pt.webdetails.cpf.api.IUserContentAccessExtended;
import pt.webdetails.cpf.repository.api.FileAccess;

import javax.ws.rs.core.MediaType;
import javax.xml.bind.JAXBElement;
import javax.xml.namespace.QName;
import java.io.IOException;
import java.util.ArrayList;

public class RemoteUserContentAccess extends RemoteReadWriteAccess implements IUserContentAccessExtended {
  private static final Log logger = LogFactory.getLog( RemoteReadWriteAccess.class );

  public RemoteUserContentAccess( String reposURL, String username, String password ) {
    super( reposURL, username, password );
  }

  public RemoteUserContentAccess( String basePath, String reposURL, String username, String password ) {
    super( basePath, reposURL, username, password );
  }

  @Override
  public boolean saveFile( IFileContent file ) {
    try {
      if ( saveFile( file.getPath(), file.getContents() ) ) {
        if ( file.isHidden() ) {
          String path = file.getPath();
          String fullPath = buildPath( path );
          return putMetadataProperty( fullPath, METADATA_PERM_HIDDEN, "true" ) && updateProperties( file );
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
    ArrayList<StringKeyStringValueDto> properties = new ArrayList<>();
    properties.add( new StringKeyStringValueDto( "file.title", file.getTitle() ) );
    properties.add( new StringKeyStringValueDto( "file.description", file.getDescription() ) );

    String path = file.getPath();
    String fullPath = buildPath( path );
    String requestURL = createRequestURL( "/api/repo/files/", fullPath, "localeProperties" );

    ClientResponse response = client.resource( requestURL )
      .queryParam( "locale", defaultLocale )
      .type( MediaType.APPLICATION_XML )
      .put( ClientResponse.class, new JAXBElement<>( new QName( "stringKeyStringValueDtoes" ), ArrayList.class, properties  ) );

    //TODO: handle non-OK status codes? log? exception?
    return response.getStatus() == ClientResponse.Status.OK.getStatusCode();
  }

  @Override
  public boolean hasAccess( String filePath, FileAccess access ) {
    String requestURL = createRequestURL( filePath, "canAccess" );

    String response = null;
    try {
      response = client
        .resource( requestURL )
        .queryParam( "permissions", Integer.toString( encodeFileAccess( access ) ) )
        .type( MediaType.TEXT_PLAIN )
        .get( String.class );
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
}

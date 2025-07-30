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

package pt.webdetails.cpf.repository.rca;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.glassfish.jersey.client.ClientConfig;
import jakarta.ws.rs.core.Response;
import org.glassfish.jersey.client.authentication.HttpAuthenticationFeature;
import org.glassfish.jersey.media.multipart.MultiPartFeature;
import org.pentaho.platform.api.repository2.unified.RepositoryFile;
import org.pentaho.platform.api.repository2.unified.webservices.RepositoryFileDto;
import org.pentaho.platform.api.repository2.unified.webservices.RepositoryFileTreeDto;
import pt.webdetails.cpf.repository.api.IBasicFile;
import pt.webdetails.cpf.repository.api.IBasicFileFilter;
import pt.webdetails.cpf.repository.api.IReadAccess;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

import jakarta.ws.rs.client.Client;
import jakarta.ws.rs.client.ClientBuilder;
import jakarta.ws.rs.client.WebTarget;
import jakarta.ws.rs.core.MediaType;

/**
 * Class {@code RemoteReadAccess} provides an implementation of {@code IReadAccess} via REST calls to the Pentaho Server.
 *
 * @see IReadAccess
 */
public class RemoteReadAccess implements IReadAccess {
  public static final String URI_PATH_SEPARATOR = "/";
  protected Client client;
  private static final Log logger = LogFactory.getLog( RemoteReadAccess.class );
  String reposURL;

  protected String basePath = RepositoryFile.SEPARATOR;

  public RemoteReadAccess( String reposURL, String username, String password ) {
    this.reposURL = reposURL;
    ClientConfig clientConfig = new ClientConfig();
    clientConfig.register( MultiPartFeature.class );
    client = ClientBuilder.newClient( clientConfig );
    client.register( HttpAuthenticationFeature.basic( username, password ) );
  }

  public RemoteReadAccess( String basePath, String reposURL, String username, String password ) {
    this( reposURL, username, password );
    this.basePath = ( basePath == null || basePath.isEmpty() ) ? RepositoryFile.SEPARATOR : ( basePath.endsWith( RepositoryFile.SEPARATOR ) ? basePath : basePath + RepositoryFile.SEPARATOR );
  }

  @Override
  public InputStream getFileInputStream( String path ) throws IOException {
    // download method used because it does the correct conversions for ktr/kjb files (see CDA-93)
    String fullPath = buildPath( path );
    String requestURL = createRequestURL( fullPath, "download" );

    Response clientResponse = client.target( requestURL )
      .queryParam( "withManifest", "false" )
      .request( MediaType.APPLICATION_OCTET_STREAM_TYPE )
      .get( Response.class );

    if ( clientResponse != null && clientResponse.getStatus() == Response.Status.OK.getStatusCode() ) {
      InputStream inputStream = clientResponse.readEntity( InputStream.class );
      return inputStream;
    }
    return null;
  }

  @Override
  public boolean fileExists( String path ) {
    String fullPath = buildPath( path );
    String requestURL = createRequestURL( fullPath, "properties" );

    Response clientResponse = client.target( requestURL )
      .request( MediaType.APPLICATION_XML )
      .get( Response.class );

    return ( clientResponse != null && clientResponse.getStatus() == Response.Status.OK.getStatusCode() );
  }

  @Override
  public long getLastModified( String path ) {
    String fullPath = buildPath( path );
    String requestURL = createRequestURL( fullPath, "properties" );
    RepositoryFileDto response = client.target( requestURL )
      .request( MediaType.APPLICATION_XML )
      .get( RepositoryFileDto.class );

    if ( response == null ) {
      return 0L;
    }
    return Long.parseLong( response.getLastModifiedDate() );
  }

  @Override
  public List<IBasicFile> listFiles( String path, IBasicFileFilter filter, int maxDepth, boolean includeDirs, boolean showHiddenFilesAndFolders ) {
    String fullPath = buildPath( path );
    String requestURL = createRequestURL( fullPath, "tree" );

    WebTarget resource = client.target( requestURL );

    // apply query params
    resource = resource.queryParam( "showHidden", Boolean.toString( showHiddenFilesAndFolders ) );

    if ( maxDepth >= 0 ) {
      resource = resource.queryParam( "depth", Integer.toString( maxDepth ) );
    }
    // NOTE: legacy filters on the "tree" endpoint do not appear to work as expected
    //       for now, we're going to do the filtering locally.
    /*
    if ( !includeDirs ) {
      target = target.queryParam( "filter", "*|FILES" );
    }
    */

    // GET
    RepositoryFileTreeDto response = null;
    try {
      response = resource.request( MediaType.APPLICATION_XML ).get( RepositoryFileTreeDto.class );
    } catch ( Exception ex ) {
      logger.error( ex );
      return null;
    }
    if ( response == null ) {
      return null;
    }

    return treeFlatten( response, includeDirs, filter, fullPath );
  }

  @Override
  public List<IBasicFile> listFiles( String path, IBasicFileFilter filter, int maxDepth, boolean includeDirs ) {
    return listFiles( path, filter, maxDepth, includeDirs, false );
  }

  @Override
  public List<IBasicFile> listFiles( String path, IBasicFileFilter filter, int maxDepth ) {
    return listFiles( path, filter, maxDepth, false );
  }

  @Override
  public List<IBasicFile> listFiles( String path, IBasicFileFilter filter ) {
    return listFiles( path, filter, -1 );
  }

  @Override
  public IBasicFile fetchFile( String path ) {
    String fullPath = buildPath( path );
    String requestURL = createRequestURL( fullPath, "properties" );
    RepositoryFileDto response = client.target( requestURL )
      .request( MediaType.APPLICATION_XML )
      .get( RepositoryFileDto.class );
    return new RemoteBasicFile( basePath, this, response );
  }

  static String encodePath( String path ) {
    return path.replaceAll( RepositoryFile.SEPARATOR, ":" );
  }

  String createRequestURL( String path, String method ) {
    return createRequestURL( "/api/repo/files/", path, method );
  }

  String createRequestURL( String endpoint, String path, String method ) {
    if ( method != null ) {
      return reposURL + endpoint + encodePath( path ) + URI_PATH_SEPARATOR + method;
    }
    return reposURL + endpoint + encodePath( path );
  }

  private boolean pathEquals( String path1, String path2 ) {
    if ( !path1.endsWith( RepositoryFile.SEPARATOR ) ) {
      path1 = path1 + RepositoryFile.SEPARATOR;
    }
    if ( !path2.endsWith( RepositoryFile.SEPARATOR ) ) {
      path2 = path2 + RepositoryFile.SEPARATOR;
    }
    return path1.equals( path2 );
  }

  private void treeFlattenRecursive( RepositoryFileTreeDto node, boolean includeDirs, IBasicFileFilter filter, List<IBasicFile> result, String queryPath ) {
    IBasicFile file = new RemoteBasicFile( basePath, this, node.getFile() );

    if ( ( includeDirs || !file.isDirectory() ) && !pathEquals( file.getFullPath(), queryPath ) && ( filter == null || filter.accept( file ) ) ) {
      result.add( file );
    }

    for ( RepositoryFileTreeDto child : node.getChildren() ) {
      treeFlattenRecursive( child, includeDirs, filter, result, queryPath );
    }
  }

  List<IBasicFile> treeFlatten( RepositoryFileTreeDto tree, boolean includeDirs, IBasicFileFilter filter, String queryPath ) {
    List<IBasicFile> flatList = new ArrayList<>();
    treeFlattenRecursive( tree, includeDirs, filter, flatList, queryPath );
    return flatList;
  }

  protected String buildPath( String path ) {
    if ( path == null ) {
      return this.basePath;
    }

    String fullPath = this.basePath;
    if ( path.startsWith( RepositoryFile.SEPARATOR ) ) {
      fullPath += path.substring( 1 );
    } else {
      fullPath += path;
    }

    return fullPath; //TODO: normalize and guard against accessing above basePath
  }
}

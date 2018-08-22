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
package pt.webdetails.cpf.repository.rca;

import com.sun.jersey.api.json.JSONConfiguration;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
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

import com.sun.jersey.api.client.Client;
import com.sun.jersey.api.client.ClientResponse;
import com.sun.jersey.api.client.WebResource;
import com.sun.jersey.api.client.config.ClientConfig;
import com.sun.jersey.api.client.config.DefaultClientConfig;
import com.sun.jersey.api.client.filter.HTTPBasicAuthFilter;

import javax.ws.rs.core.MediaType;

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
    ClientConfig clientConfig = new DefaultClientConfig();
    clientConfig.getFeatures().put( JSONConfiguration.FEATURE_POJO_MAPPING, Boolean.TRUE );
    client = Client.create( clientConfig );
    client.addFilter( new HTTPBasicAuthFilter( username, password ) );
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

    ClientResponse clientResponse = client.resource( requestURL )
      .queryParam( "withManifest", "false" )
      .type( MediaType.APPLICATION_OCTET_STREAM_TYPE )
      .get( ClientResponse.class );

    if ( clientResponse != null && clientResponse.getStatus() == ClientResponse.Status.OK.getStatusCode() ) {
      return clientResponse.getEntityInputStream();
    }
    return null;
  }

  @Override
  public boolean fileExists( String path ) {
    String fullPath = buildPath( path );
    String requestURL = createRequestURL( fullPath, "properties" );

    ClientResponse clientResponse = client.resource( requestURL )
      .type( MediaType.APPLICATION_XML )
      .get( ClientResponse.class );

    return ( clientResponse != null && clientResponse.getStatus() == ClientResponse.Status.OK.getStatusCode() );
  }

  @Override
  public long getLastModified( String path ) {
    String fullPath = buildPath( path );
    String requestURL = createRequestURL( fullPath, "properties" );
    RepositoryFileDto response = client.resource( requestURL )
      .type( MediaType.APPLICATION_XML )
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

    WebResource resource = client.resource( requestURL );

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
      response = resource.type( MediaType.APPLICATION_XML ).get( RepositoryFileTreeDto.class );
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
    RepositoryFileDto response = client.resource( requestURL )
      .type( MediaType.APPLICATION_XML )
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

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
import org.pentaho.platform.api.repository2.unified.webservices.RepositoryFileDto;
import org.pentaho.platform.api.repository2.unified.webservices.RepositoryFileTreeDto;
import pt.webdetails.cpf.repository.api.IBasicFile;
import pt.webdetails.cpf.repository.api.IBasicFileFilter;
import pt.webdetails.cpf.repository.api.IReadAccess;

import javax.ws.rs.client.Client;
import javax.ws.rs.client.ClientBuilder;
import javax.ws.rs.client.ClientRequestFilter;
import javax.ws.rs.client.WebTarget;
import javax.ws.rs.core.HttpHeaders;
import javax.ws.rs.core.MediaType;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.Base64;
import java.util.List;

/**
 * Class {@code RemoteReadAccess} provides an implementation of {@code IReadAccess} via REST calls to the Pentaho Server.
 *
 * @see IReadAccess
 */
public class RemoteReadAccess implements IReadAccess {
  Client client;
  private static final Log logger = LogFactory.getLog( RemoteReadAccess.class );
  String reposURL;
  private static final String DEFAULT_PATH_SEPARATOR = "/";

  protected String basePath = "/";

  public RemoteReadAccess( String reposURL, String username, String password ) {
    this.reposURL = reposURL;
    client = ClientBuilder.newClient()
        // Register Authentication provider
        .register( (ClientRequestFilter) requestContext -> {
          byte[] passwordBytes = password.getBytes();

          Charset CHARACTER_SET = Charset.forName( "iso-8859-1" );
          final byte[] prefix = ( username + ":" ).getBytes( CHARACTER_SET );
          final byte[] usernamePassword = new byte[ prefix.length + passwordBytes.length ];

          System.arraycopy( prefix, 0, usernamePassword, 0, prefix.length );
          System.arraycopy( passwordBytes, 0, usernamePassword, prefix.length, passwordBytes.length );

          String authentication = "Basic " + new String( Base64.getEncoder().encode( usernamePassword ), "ASCII" );
          requestContext.getHeaders().add( HttpHeaders.AUTHORIZATION, authentication );
        } )
        // Register ImportMessage MessageBodyWriter
        .register( org.pentaho.ctools.cpf.repository.rca.ImportMessageBodyWriter.class );
  }

  public RemoteReadAccess( String basePath, String reposURL, String username, String password ) {
    this( reposURL, username, password );
    this.basePath = ( basePath == null || basePath.isEmpty() ) ? DEFAULT_PATH_SEPARATOR : ( basePath.endsWith( DEFAULT_PATH_SEPARATOR ) ? basePath : basePath + DEFAULT_PATH_SEPARATOR );
  }

  @Override
  public InputStream getFileInputStream( String path ) throws IOException {
    // download method used because it does the correct conversions for ktr/kjb files (see CDA-93)
    String fullPath = buildPath( path );
    String requestURL = createRequestURL( fullPath, "download" );
    InputStream responseData = client.target( requestURL )
        .queryParam( "withManifest", "false" )
        .request( MediaType.APPLICATION_OCTET_STREAM_TYPE )
        .get( InputStream.class );

    return responseData;
  }

  @Override
  public boolean fileExists( String path ) {
    String fullPath = buildPath( path );
    String requestURL = createRequestURL( fullPath, "properties" );
    RepositoryFileDto response = client.target( requestURL )
        .request( MediaType.APPLICATION_XML )
        .get( RepositoryFileDto.class );
    return response != null;
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

    WebTarget target = client.target( requestURL );

    // apply query params
    target = target.queryParam( "showHidden", showHiddenFilesAndFolders );

    if ( maxDepth >= 0 ) {
      target = target.queryParam( "depth", maxDepth );
    }
    // TODO: legacy filters on the "tree" endpoint do not appear to work as expected
    //       for now, we're going to do the filtering locally.
    /*
    if ( !includeDirs ) {
      target = target.queryParam( "filter", "*|FILES" );
    }
    */

    // GET
    RepositoryFileTreeDto response = null;
    try {
      response = target.request( MediaType.APPLICATION_XML ).get( RepositoryFileTreeDto.class );
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
    return path.replaceAll( "/", ":" );
  }

  String createRequestURL( String path, String method ) {
    return createRequestURL( "/api/repo/files/", path, method );
  }

  String createRequestURL( String endpoint, String path, String method ) {
    if ( method != null ) {
      return reposURL + endpoint + encodePath( path ) + DEFAULT_PATH_SEPARATOR + method;
    }
    return reposURL + endpoint + encodePath( path );
  }

  private boolean pathEquals( String path1, String path2 ) {
    if ( !path1.endsWith( DEFAULT_PATH_SEPARATOR ) ) {
      path1 = path1 + DEFAULT_PATH_SEPARATOR;
    }
    if ( !path2.endsWith( DEFAULT_PATH_SEPARATOR ) ) {
      path2 = path2 + DEFAULT_PATH_SEPARATOR;
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
    if ( path.startsWith( DEFAULT_PATH_SEPARATOR ) ) {
      fullPath += path.substring( 1 );
    } else {
      fullPath += path;
    }

    return fullPath; //TODO: normalize and guard against accessing above basePath
  }
}

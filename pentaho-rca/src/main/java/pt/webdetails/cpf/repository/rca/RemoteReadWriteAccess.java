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

import org.glassfish.jersey.media.multipart.FormDataMultiPart;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.pentaho.platform.api.repository2.unified.webservices.RepositoryFileDto;
import org.pentaho.platform.api.repository2.unified.webservices.StringKeyStringValueDto;
import pt.webdetails.cpf.repository.api.IRWAccess;

import javax.ws.rs.client.Entity;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.xml.bind.JAXBElement;
import javax.xml.namespace.QName;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;

/**
 * Class {@code RemoteReadWriteAccess} provides an implementation of {@code IRWAccess} via REST calls to the Pentaho Server.
 *
 * @see IRWAccess
 */
public class RemoteReadWriteAccess extends RemoteReadAccess implements IRWAccess {
  private static final Log logger = LogFactory.getLog( RemoteReadWriteAccess.class );
  protected static final String METADATA_PERM_HIDDEN = "_PERM_HIDDEN";

  public RemoteReadWriteAccess( String reposURL, String username, String password ) {
    super( reposURL, username, password );
  }

  public RemoteReadWriteAccess( String basePath, String reposURL, String username, String password ) {
    super( basePath, reposURL, username, password );
  }

  @Override
  public boolean saveFile( String path, InputStream contents ) {
    String fullPath = buildPath( path );
    // split into folder and filename
    int splitIndex = fullPath.lastIndexOf( '/' );
    String folder = splitIndex > 0 ? fullPath.substring( 0, splitIndex ) : "/";
    String filename = splitIndex > 0 ? fullPath.substring( splitIndex + 1 ) : null;

    if ( filename == null || filename.isEmpty() ) {
      throw new IllegalArgumentException( "Invalid path: " + fullPath );
    }

    // this endpoint requires a different encoding for paths
    String requestURL = createRequestURL( "/api/repo/files/import", "", null );

    FormDataMultiPart parts = new FormDataMultiPart();
    parts.field( "importDir", folder );
    parts.field( "fileUpload", contents, MediaType.MULTIPART_FORM_DATA_TYPE );
    parts.field( "overwriteFile", "true" );
    parts.field( "fileNameOverride", filename );

    Response response = client.target( requestURL )
      .request( MediaType.MULTIPART_FORM_DATA )
      .post( Entity.entity( parts, MediaType.MULTIPART_FORM_DATA_TYPE ), Response.class );

    if ( response.getStatus() != Response.Status.OK.getStatusCode() ) {
      //TODO: handle non-OK status codes? log? exception?
      return false;
    }

    // make file not hidden
    return putMetadataProperty( fullPath, METADATA_PERM_HIDDEN, "false" );
  }

  @Override
  public boolean copyFile( String pathFrom, String pathTo ) {
    try {
      InputStream contents = getFileInputStream( pathFrom );
      if ( contents == null ) {
        return false;
      }

      return saveFile( pathTo, contents );
    } catch ( IOException ex ) {
      logger.error( ex.getMessage() );
    }
    return false;
  }

  @Override
  public boolean deleteFile( String path ) {
    String fullPath = buildPath( path );
    String fileId = remoteFileId( fullPath );

    if ( fileId == null ) {
      logger.error( "Attempt to delete non-existing file: " + fullPath );
      return false;
    }

    String requestURL = createRequestURL( "", "delete" ); // TODO: delete or deletepermanent
    Response response = client.target( requestURL )
      .request( MediaType.APPLICATION_XML )
      .put(Entity.entity(fileId, MediaType.APPLICATION_XML ), Response.class );

    //TODO: handle non-OK status codes? log? exception?
    return response.getStatus() == Response.Status.OK.getStatusCode();
  }

  private String remoteFileId( String path ) {
    String requestURL = createRequestURL( path, "properties" );
    RepositoryFileDto properties = client.target( requestURL )
      .request( MediaType.APPLICATION_XML )
      .get( RepositoryFileDto.class );

    if ( properties == null ) {
      return null;
    }
    return properties.getId();
  }

  @Override
  public boolean createFolder( String path ) {
    String fullPath = buildPath( path );
    String requestURL = createRequestURL( "/api/repo/dirs/", fullPath, null );
    Response response = client.target( requestURL )
      .request( MediaType.APPLICATION_XML )
      .put( Entity.entity( fullPath , MediaType.APPLICATION_XML ) ,Response.class );

    //TODO: handle non-OK status codes? log? exception?
    return response.getStatus() == Response.Status.OK.getStatusCode();
  }

  @Override
  public boolean createFolder( String path, boolean isHidden ) {
    String fullPath = buildPath( path );
    if ( createFolder( fullPath ) ) {
      if ( isHidden ) {
        //NOTE: We only set the hidden flag on the last path component, while the "pentaho" implementation will
        //      set the hidden flag on all intermediate folders that were created.
        // TODO: revert directory creation on error???
        return putMetadataProperty( fullPath, METADATA_PERM_HIDDEN, "true" );
      }
      return true;
    }
    return false;
  }

  protected boolean putMetadataProperty( String fullPath, String key, String value ) {
    StringKeyStringValueDto metadataProperty = new StringKeyStringValueDto();
    metadataProperty.setKey( key );
    metadataProperty.setValue( value );

    ArrayList<StringKeyStringValueDto> metadata = new ArrayList<>();
    metadata.add( metadataProperty );

    String requestURL = createRequestURL( fullPath, "metadata" );
    Response response = client.target( requestURL )
      .request( MediaType.APPLICATION_XML )
      .put( Entity.entity( new JAXBElement<>( new QName( "stringKeyStringValueDtoes" ), ArrayList.class, metadata ), MediaType.APPLICATION_XML ), Response.class );

    // TODO: handle non-OK status codes? log? exceptions?
    if ( response.getStatus() == Response.Status.OK.getStatusCode() ) {
      return true;
    }
    logger.error( "Failed to set " + key + "=" + value + " for: " + fullPath );
    return false;
  }
}

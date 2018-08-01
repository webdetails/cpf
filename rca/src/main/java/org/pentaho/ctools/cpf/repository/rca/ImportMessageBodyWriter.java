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

import org.apache.commons.io.IOUtils;

import javax.ws.rs.Produces;
import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.HttpHeaders;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.MultivaluedMap;
import javax.ws.rs.ext.MessageBodyWriter;
import javax.ws.rs.ext.Provider;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.lang.annotation.Annotation;
import java.lang.reflect.Type;
import java.util.List;

/**
 * Class {@code ImportMessageBodyWriter} provides a {@code MessageBodyWriter<T>} implementation for the
 * {@code ImportMessage} class as a multipart/form-data POST body.
 *
 * @see ImportMessage
 * @see MessageBodyWriter
 */
@Provider
@Produces( "multipart/form-data" )
public class ImportMessageBodyWriter implements MessageBodyWriter<ImportMessage> {
  static final String CRLF = "\r\n";

  @Override
  public boolean isWriteable( Class<?> type, Type genericType, Annotation[] annotations, MediaType mediaType ) {
    return type == ImportMessage.class;
  }

  @Override
  public long getSize( ImportMessage importMessage, Class<?> type, Type genericType, Annotation[] annotations, MediaType mediaType ) {
    return -1;
  }

  private byte[] prepareField( String boundary, String field, String value ) {
    StringBuilder builder = new StringBuilder();
    builder.append( "--" );
    builder.append( boundary );
    builder.append( CRLF );
    builder.append( "Content-Disposition: form-data; name=\"" + field + "\"" );
    builder.append( CRLF );
    builder.append( CRLF );
    builder.append( value );
    builder.append( CRLF );
    return builder.toString().getBytes();
  }

  private void writeContents( String boundary, InputStream contents, OutputStream destination ) throws IOException {
    StringBuilder builder = new StringBuilder();
    builder.append( "--" );
    builder.append( boundary );
    builder.append( CRLF );
    builder.append( "Content-Disposition: form-data; name=\"fileUpload\"" );
    builder.append( CRLF );
    builder.append( CRLF );
    destination.write( builder.toString().getBytes() );
    IOUtils.copy( contents, destination );
    destination.write( CRLF.getBytes() );
  }

  private byte[] createTerminator( String boundary ) {
    return ( "--" + boundary + "--" + CRLF ).getBytes();
  }

  @Override
  public void writeTo( ImportMessage importMessage, Class<?> type, Type genericType, Annotation[] annotations, MediaType mediaType, MultivaluedMap<String, Object> httpHeaders, OutputStream entityStream ) throws IOException, WebApplicationException {
    // define boundary
    String boundary = "------------------------b9cf97a74e305cd0";
    List<Object> contentType = httpHeaders.get( HttpHeaders.CONTENT_TYPE );
    contentType.set( 0, contentType.get( 0 ) + "; boundary=" + boundary );
    // write body
    entityStream.write( prepareField( boundary, "importDir", importMessage.importDir ) );
    writeContents( boundary, importMessage.contents, entityStream );
    entityStream.write( prepareField( boundary, "overwriteFile", String.valueOf( importMessage.overwrite ) ) );
    entityStream.write( prepareField( boundary, "fileNameOverride", importMessage.filename ) );
    entityStream.write( createTerminator( boundary ) );
  }
}

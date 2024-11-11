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


package pt.webdetails.cpf;

import pt.webdetails.cpf.http.ICommonParameterProvider;

import java.io.OutputStream;


public interface RestRequestHandler {

  public enum HttpMethod {
    GET, POST, PUT, DELETE, HEAD, TRACE, OPTIONS, CONNECT, PATCH
  }

  public boolean canHandle( HttpMethod method, String path );

  public void route( HttpMethod method, String path, OutputStream out, ICommonParameterProvider pathParams,
                     ICommonParameterProvider requestParams );

  public String getResponseMimeType();

}

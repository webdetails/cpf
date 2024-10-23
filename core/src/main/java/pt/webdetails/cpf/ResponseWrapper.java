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

import jakarta.servlet.http.HttpServletResponse;

/**
 * @author pdpi
 */
public class ResponseWrapper {

  private HttpServletResponse response;

  public ResponseWrapper( HttpServletResponse response ) {
    this.response = response;
  }

  public HttpServletResponse getResponse() {
    return response;
  }

  public void setResponseHeader( final String header, String value ) {
    if ( response != null ) {
      response.setHeader( header, value );
    }
  }

  public void setOutputType( String type ) {
    setResponseHeader( "Content-Type", type );
  }
}

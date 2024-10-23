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
package pt.webdetails.cpf.messaging;

import java.io.OutputStream;

import jakarta.servlet.ServletOutputStream;

import pt.webdetails.cpf.web.CpfHttpServletResponse;
import pt.webdetails.cpf.web.DelegatingServletOutputStream;

/**
 * InterPluginCall response, only cares about OutputStream
 * 
 */
public class MockHttpServletResponse extends CpfHttpServletResponse {
  private OutputStream outputStream;
  private ServletOutputStream servletOutputStream;

  public MockHttpServletResponse(OutputStream output) {
    outputStream = output;
    servletOutputStream = new DelegatingServletOutputStream( outputStream );
  }

  @Override
  public ServletOutputStream getOutputStream() {
    return servletOutputStream;
  }

}

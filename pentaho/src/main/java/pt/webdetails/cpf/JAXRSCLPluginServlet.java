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

import java.io.IOException;

import jakarta.servlet.ServletException;
import jakarta.servlet.ServletRequest;
import jakarta.servlet.ServletResponse;
//import jakarta.servlet.http.HttpServletRequest;
//import jakarta.servlet.http.HttpServletResponse;

import org.pentaho.platform.web.servlet.JAXRSPluginServlet;

/**
 * JAXRSPluginServlet that switches to the plugin classloader.
 */
public class JAXRSCLPluginServlet extends JAXRSPluginServlet {

  private static final long serialVersionUID = 1L;

//  @Override
//  public void service( HttpServletRequest request, HttpServletResponse response ) throws ServletException, IOException {
//    ClassLoader original = Thread.currentThread().getContextClassLoader();
//    try {
//      Thread.currentThread().setContextClassLoader( getClass().getClassLoader() );
//      super.service( request, response );
//    } finally {
//      Thread.currentThread().setContextClassLoader( original );
//    }
//  }

  @Override
  public void service(ServletRequest req, ServletResponse res) throws ServletException, IOException {
    ClassLoader original = Thread.currentThread().getContextClassLoader();
    try {
      // avoid trouble if we need to load a new class during the call
      Thread.currentThread().setContextClassLoader( getClass().getClassLoader() );
      super.service( req, res );
    }
    finally {
      Thread.currentThread().setContextClassLoader( original );
    }
  }

}

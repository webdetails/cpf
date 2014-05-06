package pt.webdetails.cpf;

import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
//import javax.servlet.http.HttpServletRequest;
//import javax.servlet.http.HttpServletResponse;

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

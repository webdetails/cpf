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

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.util.Map;
import java.util.concurrent.Callable;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.pentaho.platform.api.engine.IPluginManager;
import org.pentaho.platform.engine.core.system.PentahoSystem;
import org.pentaho.platform.web.servlet.JAXRSPluginServlet;
import org.springframework.beans.factory.ListableBeanFactory;

import pt.webdetails.cpf.ClassLoaderAwareCaller;
import pt.webdetails.cpf.Util;
import pt.webdetails.cpf.plugincall.api.IPluginCall;

/**
 * PluginManager/Beans/reflection-based plugin call <-- nope, ended up being resty
 */
public class BeanyPluginCall implements IPluginCall {

  private static final Log logger = LogFactory.getLog(BeanyPluginCall.class);
  
  protected String pluginId;
  protected String servicePath;
  protected String methodPath;
  protected String method = "GET";
  protected byte[] contents;
  
  public BeanyPluginCall(String pluginId, String servicePath, String methodPath) {
    this.pluginId = pluginId;
    this.servicePath = servicePath;
    this.methodPath = methodPath;
  }


  //TODO: error handling, cache bean
  private static JAXRSPluginServlet getApiBean( String pluginId ) {
    IPluginManager pluginManager = PentahoSystem.get(IPluginManager.class);
    ListableBeanFactory beanFactory = pluginManager.getBeanFactory( pluginId );
    
    if (beanFactory == null) {
      if ( pluginManager.getClassLoader( pluginId ) == null ) {
        logger.error( "No such plugin: " + pluginId );
      }
      else {
        logger.error( "No bean factory for plugin: " + pluginId );
      }
      return null;
    }
    
    if (!beanFactory.containsBean( "api" )) {
      logger.error( "'api' bean not found in " + pluginId );
      return null;
    }

    return (JAXRSPluginServlet) beanFactory.getBean("api", JAXRSPluginServlet.class);
  }

  private IPluginManager getPluginManager() {
    return PentahoSystem.get(IPluginManager.class);
  }

  private void runThroughService( Map<String, String[]> params ) throws Exception {
    ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
    String path = Util.joinPath( "/", pluginId, "api", servicePath, methodPath );
    final JAXRSPluginServlet pluginServlet = getApiBean( pluginId );
    final MockHttpServletRequest request = new MockHttpServletRequest( path, params );
    final MockHttpServletResponse response = new MockHttpServletResponse( outputStream );
    // at this point there
    ClassLoaderAwareCaller caller = new ClassLoaderAwareCaller( getPluginManager().getClassLoader( pluginId ) );
    caller.callInClassLoader( new Callable<Object>() {
      @Override
      public Object call() throws Exception {
        pluginServlet.service( request, response );
        return null;
      }
    } );
    contents = outputStream.toByteArray();
  }

  Callable<String> asCallable(final Map<String, String[]> parameters) {
    return new Callable<String> () {
      public String call() throws Exception {
        return BeanyPluginCall.this.call(parameters);
      }
    };
  }

  Runnable asRunnable(final Map<String, String[]> parameters) {
    return new Runnable () {

      public void run() {
        try {
          BeanyPluginCall.this.run(parameters);
        } catch ( Exception e ) {
          logger.error(e);
        }
      }

    };
  }

  @Override
  public String call( Map<String, String[]> params ) throws Exception {
    runThroughService( params );
    return Util.toString( getResult() );
  }

  @Override
  public void run( Map<String, String[]> params ) throws Exception {
    runThroughService( params );
  }

  @Override
  public InputStream getResult() {
    if (contents == null) {
      return null;
    }
    return new ByteArrayInputStream(contents);
  }

  @Override
  public boolean exists() {
    return getApiBean( pluginId ) != null;
  }


}

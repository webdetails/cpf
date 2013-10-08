package pt.webdetails.cpf.messaging;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Map;
import java.util.concurrent.Callable;

import javax.servlet.ServletException;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.pentaho.platform.api.engine.IPluginManager;
import org.pentaho.platform.engine.core.system.PentahoSystem;
import org.pentaho.platform.web.servlet.JAXRSPluginServlet;
import org.springframework.beans.factory.ListableBeanFactory;

import pt.webdetails.cpf.Util;
import pt.webdetails.cpf.plugincall.api.IPluginCall;

/**
 * PluginManager/Beans/reflection-based plugin call
 */
public class BeanyPluginCall implements IPluginCall {

  private static final Log logger = LogFactory.getLog(BeanyPluginCall.class);
  
  protected String pluginId;
  protected String servicePath;
  protected String methodPath;
  protected ByteArrayOutputStream outputStream;
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

  private void runThroughApi( Map<String, String[]> params ) throws ServletException, IOException {
    String path = Util.joinPath( "/", servicePath, methodPath );
    JAXRSPluginServlet pluginServlet = getApiBean( pluginId );
    MockHttpServletRequest request = new MockHttpServletRequest( path, params );
    MockHttpServletResponse response = new MockHttpServletResponse( outputStream );
    pluginServlet.service( request, response );
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
  public String call( Map<String, String[]> params ) throws ServletException, IOException {
    runThroughApi( params );
    return Util.toString( getResult() );
  }

  @Override
  public void run( Map<String, String[]> params ) throws ServletException, IOException {
    runThroughApi( params );
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

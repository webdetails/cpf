/*!
* Copyright 2002 - 2019 Webdetails, a Hitachi Vantara company. All rights reserved.
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
package pt.webdetails.cpf;

import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.lang.annotation.Annotation;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.Callable;

import java.lang.reflect.Method;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.ws.rs.DefaultValue;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.Response;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.pentaho.platform.api.engine.IParameterProvider;
import org.pentaho.platform.api.engine.IPentahoSession;
import org.pentaho.platform.api.engine.IPluginManager;
import org.pentaho.platform.engine.core.solution.SimpleParameterProvider;
import org.pentaho.platform.engine.core.system.PentahoSessionHolder;
import org.pentaho.platform.engine.core.system.PentahoSystem;
import org.pentaho.platform.web.http.request.HttpRequestParameterProvider;
import org.springframework.beans.factory.ListableBeanFactory;
import pt.webdetails.cpf.plugincall.api.IPluginCall;
import pt.webdetails.cpf.web.CpfHttpServletRequest;
import pt.webdetails.cpf.web.CpfHttpServletResponse;


/**
 * Call to another pentaho plugin getting the bean from the plugin bean factory.
 * Not thread safe. - really ? Why not ?
 */
public class InterPluginCall implements Runnable, Callable<String>, IPluginCall {

  public static final Plugin CDA = new Plugin( "cda" );
  public static final Plugin CDB = new Plugin( "cdb" );
  public static final Plugin CDC = new Plugin( "cdc" );
  public static final Plugin CDE = new Plugin( "pentaho-cdf-dd" );
  public static final Plugin CDF = new Plugin( "pentaho-cdf" );
  public static final Plugin CDV = new Plugin( "cdv" );
  private Object objectResponse;


  @Override
  public String call( Map<String, String[]> params ) {
    for ( Map.Entry<String, String[]> entry : params.entrySet() ) {
      requestParameters.put( entry.getKey(), entry.getValue() );
    }
    return call();
  }

  @Override
  public void run( Map<String, String[]> params ) {
    for ( Map.Entry<String, String[]> entry : params.entrySet() ) {
      requestParameters.put( entry.getKey(), entry.getValue() );
    }
    run();
  }

  @Override
  public InputStream getResult() {
    return null; // REVIEW
  }

  /**
   * @deprecated (
   * Deprecated<br /> 
   * This simply calls new method 'exists()';<br />
   * This was the method name used in cpf-pentaho 4.x;<br />
   * Useful for blocks of code in core or pentaho-base, where method name must remain the same.)
   */
  @Deprecated
  public boolean pluginExists() {
    return exists();
  }

  @Override
  public boolean exists() {
    Object bean = getBeanObject();

    return getBeanMethod( bean ) != null;
  }

  public static class Plugin {

    private String name;
    private String title;

    public String getName() {
      return name;
    }

    public String getTitle() {
      return title;
    }

    public Plugin( String name, String title ) {
      this.name = name;
      this.title = title;
    }

    public Plugin( String id ) {
      this.name = id;
      this.title = id;
    }

  }

  private static final Log logger = LogFactory.getLog( InterPluginCall.class );

  private Plugin plugin;
  private String method;
  private String service;

  private Map<String, Object> requestParameters;
  private HttpServletResponse response;
  private HttpServletRequest request;

  private IPentahoSession session;
  private IPluginManager pluginManager;


  public InterPluginCall( Plugin plugin, String method ) {
    this( plugin, plugin.getName() + ".api", method );
  }

  public InterPluginCall( Plugin plugin, String method, Map<String, Object> params ) {
    this( plugin, plugin.getName() + ".api", method, params );
  }

  public InterPluginCall( Plugin plugin, String service, String method ) {

    if ( plugin == null ) {
      throw new IllegalArgumentException( "Plugin must be specified" );
    }

    this.plugin = plugin;
    this.method = method;
    this.service = service != null ? service : plugin.getName() + ".api";
    this.requestParameters = new HashMap<>();
  }

  public InterPluginCall( Plugin plugin, String service, String method, Map<String, Object> params ) {
    this( plugin, service, method );

    this.requestParameters.putAll( params != null ? params : new HashMap<>() );
  }

  protected HttpServletRequest getRequest() {
    if ( request == null ) {
      request = new pt.webdetails.cpf.web.CpfHttpServletRequest();
    }

    return request;
  }

  public InterPluginCall putParameter( String name, Object value ) {
    requestParameters.put( name, value );

    return this;
  }

  public String getService() {
    return this.service;
  }

  private Object getBeanObject() {
    final String pluginName = plugin.getName();

    ListableBeanFactory beanFactory = getPluginManager().getBeanFactory( pluginName );

    if ( beanFactory == null ) {
      if ( pluginManager.getClassLoader( pluginName ) == null ) {
        logger.debug( "No such plugin: " + pluginName );
      } else {
        logger.debug( "No bean factory for plugin: " + pluginName );
      }

      return null;
    }

    if ( !beanFactory.containsBean( service ) ) {
      logger.debug( "'" + service + "' bean not found in " + pluginName );

      return null;
    }

    return beanFactory.getBean( service );
  }

  private Method getBeanMethod( Object bean ) {
    if ( bean != null ) {
      Method[] methods = bean.getClass().getMethods();
      for ( Method m : methods ) {
        if ( m.getName().equals( this.method ) ) {
          return m;
        }
      }
    }

    return null;
  }

  // There are some issues to be resolved in InterPluginCall
  //
  // ONE: that is very bug prone is the reflection analysis to get de method to invoke, if there are two methods
  // with same name but invocation signatures different the first one is chosen
  //
  // TWO: we are only reading annotated params so if we try to call a method with no annotated params but with params
  // those params are not passed to the invoked method.
  public void run() {
    Object bean = getBeanObject();
    Method operation = getBeanMethod( bean );

    if ( operation != null ) {

      Annotation[][] params = operation.getParameterAnnotations();
      Class<?>[] paramTypes = operation.getParameterTypes();

      List<Object> parameters = new ArrayList<>();

      for ( int i = 0; i < params.length; i++ ) {
        String paramName = "";
        String paramDefaultValue = "";

        for ( Annotation annotation : params[i] ) {
          String annotationClass = annotation.annotationType().getName();

          if ( "javax.ws.rs.QueryParam".equals( annotationClass ) ) {
            QueryParam param = (QueryParam) annotation;
            paramName = param.value();
          } else if ( "javax.ws.rs.DefaultValue".equals( annotationClass ) ) {
            DefaultValue param = (DefaultValue) annotation;
            paramDefaultValue = param.value();
          } else if ( "javax.ws.rs.core.Context".equals( annotationClass ) ) {
            if ( paramTypes[i] == HttpServletRequest.class ) {

              CpfHttpServletRequest cpfRequest = (CpfHttpServletRequest) getRequest();
              for ( Map.Entry<String, Object> entry : requestParameters.entrySet() ) {
                String key = entry.getKey();

                Object paramValue = entry.getValue();
                if ( paramValue instanceof String[] ) {
                  String[] lValues = (String[]) paramValue;
                  if ( lValues.length == 1 ) {
                    cpfRequest.setParameter( key, lValues[0] );
                  } else {
                    cpfRequest.setParameter( key, lValues );
                  }

                } else if ( paramValue != null ) {
                  cpfRequest.setParameter( key, paramValue.toString() );
                }
              }

              parameters.add( (HttpServletRequest) cpfRequest );

            } else if ( paramTypes[i] == HttpServletResponse.class ) {
              HttpServletResponse localResponse = (HttpServletResponse) getParameterProviders().get( "path" )
                .getParameter( "httpresponse" );

              if ( localResponse == null ) {
                localResponse = getResponse();
              }

              parameters.add( localResponse );
            }
          }
        }

        if ( requestParameters.containsKey( paramName ) ) {
          Object paramValue = requestParameters.get( paramName );
          if ( paramTypes[i] == int.class ) {
            if ( paramValue instanceof String[] ) {
              String[] lValues = (String[]) paramValue;
              if ( lValues.length > 0 ) {
                paramValue = lValues[0];
              } else {
                paramValue = null;
              }
            }

            int val = Integer.parseInt( (String) paramValue );
            parameters.add( val );

          } else if ( paramTypes[i] == java.lang.Boolean.class || paramTypes[i] == boolean.class ) {

            if ( paramValue instanceof String[] ) {
              String[] lValues = (String[]) paramValue;
              if ( lValues.length > 0 ) {
                paramValue = lValues[0];
              } else {
                paramValue = null;
              }
            }

            boolean val = Boolean.parseBoolean( (String) paramValue );
            parameters.add( val );

          } else if ( paramTypes[i] == java.util.List.class ) {
            List<String> list = new ArrayList<>();

            String[] splittedValues;
            if ( paramValue instanceof String[] ) {
              splittedValues = (String[]) paramValue;
            } else {
              splittedValues = ( (String) paramValue ).split( "," );
            }


            for ( String s : splittedValues ) {
              list.add( s );
            }

            parameters.add( list );

          } else if ( paramTypes[i] == java.lang.String.class ) {
            if ( paramValue instanceof String[] ) {
              String[] lValues = (String[]) paramValue;
              if ( lValues.length > 0 ) {
                paramValue = lValues[0];
              } else {
                paramValue = null;
              }
            }

            parameters.add( paramValue );
          }

          requestParameters.remove( paramName );
        } else {
          if ( paramTypes[i] == int.class ) {
            int val = Integer.parseInt( paramDefaultValue );
            parameters.add( val );

          } else if ( paramTypes[i] == Boolean.class || paramTypes[i] == boolean.class ) {
            boolean val = Boolean.parseBoolean( paramDefaultValue );
            parameters.add( val );

          } else if ( paramTypes[i] == java.util.List.class ) {
            List<String> list = new ArrayList<>();

            String values = paramDefaultValue;
            String[] splittedValues = values.split( "," );

            for ( String s : splittedValues ) {
              list.add( s );
            }

            parameters.add( list );

          } else if ( paramTypes[i] == java.lang.String.class ) {
            parameters.add( paramDefaultValue );
          }
        }
      }

      try {
        objectResponse = operation.invoke( bean, parameters.toArray() );

      } catch ( Exception ex ) {
        logger.error( "", ex );
      }
    }
  }

  public String call() {
    run();

    String contentFromResponse = getStringValueFromObject( response );
    String contentFromObjectResponse = getStringValueFromObject( objectResponse );

    if ( contentFromResponse != null ) {
      return contentFromResponse
        + ( contentFromObjectResponse != null ? contentFromObjectResponse : "" );
    } else {
      return contentFromObjectResponse;
    }
  }

  private String getStringValueFromObject( Object object ) {
    String value = null;
    if ( object != null ) {
      value  = "";
      if ( object instanceof CpfHttpServletResponse ) {
        try {
          CpfHttpServletResponse cpfResponse = (CpfHttpServletResponse) object;
          value = cpfResponse.getContentAsString();
        } catch ( UnsupportedEncodingException ex ) {
          logger.error( "Error getting content from CpfHttpServletResponse", ex );
        }
      } else if ( object instanceof Response ) {
        Response jaxResponse = (Response) object;
        value = getStringValueFromObject( jaxResponse.getEntity() );
      } else if ( object instanceof String ) {
        value = (String) object;
      } else if ( object.toString() != null && !object.toString().contains( object.getClass().getName() ) ) {
        //Avoid the Object.toString() implicit result which does not have business value meaning
        value = object.toString();
      }
    }
    return value;
  }

  public void runInPluginClassLoader() {
    getClassLoaderCaller().runInClassLoader( this );
  }

  public String callInPluginClassLoader() {
    try {
      return getClassLoaderCaller().callInClassLoader( this );
    } catch ( Exception e ) {
      logger.error( e );

      return null;
    }
  }

  public HttpServletResponse getResponse() {
    if ( response == null ) {
      logger.debug( "No response passed to method " + this.method + ", adding response." );

      response = new CpfHttpServletResponse();
    }

    return response;
  }

  public void setResponse( HttpServletResponse response ) {
    this.response = response;
  }

  public void setSession( IPentahoSession session ) {
    this.session = session;
  }

  public void setRequestParameters( Map<String, Object> parameters ) {
    this.requestParameters = parameters;
  }

  protected IPentahoSession getSession() {
    if ( session == null ) {
      session = PentahoSessionHolder.getSession();
    }

    return session;
  }

  protected IParameterProvider getRequestParameterProvider() {
    SimpleParameterProvider provider;

    if ( request != null ) {
      provider = new HttpRequestParameterProvider( request );
      provider.setParameters( requestParameters );
    } else {
      provider = new SimpleParameterProvider( requestParameters );
    }

    return provider;
  }

  protected ClassLoaderAwareCaller getClassLoaderCaller() {
    return new ClassLoaderAwareCaller( getPluginManager().getClassLoader( plugin.getTitle() ) );
  }

  protected IPluginManager getPluginManager() {
    if ( pluginManager == null ) {
      pluginManager = PentahoSystem.get( IPluginManager.class, getSession() );
    }

    return pluginManager;
  }

  protected IParameterProvider getPathParameterProvider() {
    Map<String, Object> pathMap = new HashMap<>();

    pathMap.put( "path", "/" + method );
    pathMap.put( "httpresponse", getResponse() );

    if ( getRequest() != null ) {
      pathMap.put( "httprequest", getRequest() );
    }

    return new SimpleParameterProvider( pathMap );
  }

  protected Map<String, IParameterProvider> getParameterProviders() {
    IParameterProvider requestParams = getRequestParameterProvider();
    IParameterProvider pathParams = getPathParameterProvider();

    Map<String, IParameterProvider> paramProvider = new HashMap<>();
    paramProvider.put( IParameterProvider.SCOPE_REQUEST, requestParams );
    paramProvider.put( "path", pathParams );

    return paramProvider;
  }
}

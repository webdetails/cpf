/*!
 * Copyright 2002 - 2017 Webdetails, a Hitachi Vantara company. All rights reserved.
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

import java.io.IOException;
import java.io.OutputStream;
import java.io.UnsupportedEncodingException;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.net.URLDecoder;
import java.util.EnumMap;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.TimeoutException;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import mondrian.olap.QueryTimeoutException;

import org.apache.commons.io.IOUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import org.json.JSONException;
import org.pentaho.platform.api.engine.IMimeTypeListener;
import org.pentaho.platform.api.engine.IOutputHandler;
import org.pentaho.platform.api.engine.IParameterProvider;
import org.pentaho.platform.api.repository.IContentItem;

import org.pentaho.platform.engine.core.system.PentahoSessionHolder;
import org.pentaho.platform.engine.security.SecurityHelper;
import org.pentaho.platform.engine.services.solution.BaseContentGenerator;

import org.pentaho.platform.web.http.api.resources.utils.SystemUtils;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import pt.webdetails.cpf.annotations.AccessLevel;
import pt.webdetails.cpf.annotations.Audited;
import pt.webdetails.cpf.annotations.Exposed;
import pt.webdetails.cpf.audit.CpfAuditHelper;
import pt.webdetails.cpf.messaging.JsonSerializable;
import pt.webdetails.cpf.utils.CharsetHelper;

/**
 * @author pdpi
 */
public abstract class SimpleContentGenerator extends BaseContentGenerator {

  private static final long serialVersionUID = 1L;
  protected Log logger = LogFactory.getLog( this.getClass(  ) );

  protected static final String ENCODING = CharsetHelper.getEncoding(  );

  protected static String getEncoding(  ) {
    return ENCODING;
  }

  public enum FileType {
    JPG, JPEG, PNG, GIF, BMP, JS, CSS, HTML, HTM, XML,
    SVG, PDF, TXT, DOC, DOCX, XLS, XLSX, PPT, PPTX;

    public static FileType parse( String value ) {
      return valueOf( StringUtils.upperCase( value ) );
    }
  }

  public static class MimeType {
    public static final String CSS = "text/css";
    public static final String JAVASCRIPT = "text/javascript";
    public static final String PLAIN_TEXT = "text/plain";
    public static final String HTML = "text/html";
    public static final String XML = "text/xml";
    public static final String JPEG = "img/jpeg";
    public static final String PNG = "image/png";
    public static final String GIF = "image/gif";
    public static final String BMP = "image/bmp";
    public static final String JSON = "application/json";
    public static final String PDF = "application/pdf";

    public static final String DOC = "application/msword";
    public static final String DOCX = "application/msword";

    public static final String XLS = "application/msexcel";
    public static final String XLSX = "application/msexcel";

    public static final String PPT = "application/mspowerpoint";
    public static final String PPTX = "application/mspowerpoint";
  }

  protected static final EnumMap<FileType, String> mimeTypes = new EnumMap<FileType, String>( FileType.class );

  static {
      /*
       * Image types
       */
    mimeTypes.put( FileType.JPG, MimeType.JPEG );
    mimeTypes.put( FileType.JPEG, MimeType.JPEG );
    mimeTypes.put( FileType.PNG, MimeType.PNG );
    mimeTypes.put( FileType.GIF, MimeType.GIF );
    mimeTypes.put( FileType.BMP, MimeType.BMP );

      /*
       * HTML (  and related  ) types
       */
    // Deprecated, should be application/javascript, but IE doesn't like that
    mimeTypes.put( FileType.JS, MimeType.JAVASCRIPT );
    mimeTypes.put( FileType.HTM, MimeType.HTML );
    mimeTypes.put( FileType.HTML, MimeType.HTML );
    mimeTypes.put( FileType.CSS, MimeType.CSS );
    mimeTypes.put( FileType.XML, MimeType.XML );
    mimeTypes.put( FileType.TXT, MimeType.PLAIN_TEXT );
  }

  protected String getMimeType( String fileName ) {
    String[] fileNameSplit = StringUtils.split( fileName, '.' ); // fileName.split(  "\\."  );
    try {
      return getMimeType( FileType.valueOf( fileNameSplit[fileNameSplit.length - 1].toUpperCase(  ) ) );
    } catch ( Exception e ) {
      logger.warn( "Unrecognized extension for file name " + fileName );
      return "";
    }
  }

  protected String getMimeType( FileType fileType ) {
    if ( fileType == null ) {
      return "";
    }
    String mimeType = mimeTypes.get( fileType );
    return mimeType == null ? "" : mimeType;
  }


  @Override
  public void createContent(  ) throws Exception {
    IParameterProvider pathParams = getPathParameters(  ); // parameterProviders.get(  "path"  );

    try {

      String path = pathParams.getStringParameter( "path", null );
      String[] pathSections = StringUtils.split( path, "/" );

      if ( pathSections == null || pathSections.length == 0 ) {
        String method = getDefaultPath( path );
        if ( !StringUtils.isEmpty( method ) ) {
          logger.warn( "No method supplied, redirecting." );
          redirect( method );
        } else {
          logger.error( "No method supplied." );
        }
      } else {

        final String methodName = pathSections[0];

        try {

          final Method method = getMethod( methodName );
          invokeMethod( methodName, method );

        } catch ( NoSuchMethodException e ) {
          String msg = "couldn't locate method: " + methodName;
          logger.warn( msg );
          getResponse(  ).sendError( HttpServletResponse.SC_NOT_FOUND, msg );
        } catch ( InvocationTargetException e ) {
          // get to the cause and log properly
          Throwable cause = e.getCause(  );
          if ( cause == null ) {
            cause = e;
          }
          handleError( methodName, cause );
        } catch ( Exception e ) {
          handleError( methodName, e );
        }

      }
    } catch ( SecurityException e ) {
      logger.warn( e.toString(  ) );
    }
  }

  private void handleError( final String methodName, Throwable e ) throws IOException {

    logger.error( methodName + ": " + e.getMessage(  ), e );

    String msg = e.getLocalizedMessage(  );
    if ( e instanceof QueryTimeoutException || e instanceof TimeoutException ) { //          ||
//          (  e instanceof RuntimeException &&
//              StringUtils.containsIgnoreCase(  e.getClass(    ).getName(    ), "timeout"  )  )  ) 
      getResponse(  ).sendError( HttpServletResponse.SC_REQUEST_TIMEOUT, msg );
    } else { // default to 500
      getResponse(  ).sendError( HttpServletResponse.SC_INTERNAL_SERVER_ERROR, msg );
    }
  }

  /**
   * @param methodName
   * @return
   * @throws NoSuchMethodException
   */
  protected Method getMethod( final String methodName ) throws NoSuchMethodException {
    final Class<?>[] params = getCGMethodParams(  );
    final Method method = this.getClass(  ).getMethod( methodName, params );
    return method;
  }

  /**
   * @return this plugin's name
   */
  public abstract String getPluginName(  );

  /**
   * @return this plugin's path
   */
  public String getPluginPath(  ) {
//      return PentahoRepositoryAccess.getSystemDir(    ) + "/" + getPluginName(    );
    //FIXME COMPILING use plugin-res
    return null;
  }

  /**
   * Get a map of all public methods with the Exposed annotation.
   * Map is not thread-safe and should be used read-only.
   *
   * @param classe    Class where to find methods
   * @param lowerCase if keys should be in lower case.
   * @return map of all public methods with the Exposed annotation
   */
  protected static Map<String, Method> getExposedMethods( Class<?> classe, boolean lowerCase ) {
    HashMap<String, Method> exposedMethods = new HashMap<String, Method>(  );
    Log log = LogFactory.getLog( classe );
    for ( Method method : classe.getMethods(  ) ) {
      if ( method.getAnnotation( Exposed.class ) != null ) {
        String methodKey = method.getName(  ).toLowerCase(  );
        if ( exposedMethods.containsKey( methodKey ) ) {
          log.error( "Method " + method + " differs from " + exposedMethods.get( methodKey ) + " only in case and will override calls to it!!" );
        }
        log.debug( "registering " + classe.getSimpleName(  ) + "." + method.getName(  ) );
        exposedMethods.put( methodKey, method );
      }
    }
    return exposedMethods;
  }

  /**
   * In case we need to use reflection with methods that don't just take the OutputStream parameter.
   *
   * @return classes of exposed methods parameters
   */
  protected Class<?>[] getCGMethodParams(  ) {
    return new Class<?>[] {OutputStream.class};
  }

  protected OutputStream getResponseOutputStream( final String mimeType ) throws IOException {
    IContentItem contentItem = outputHandler.getOutputContentItem( IOutputHandler.RESPONSE, IOutputHandler.CONTENT, instanceId, mimeType );
    return contentItem.getOutputStream( null );
  }

  protected HttpServletRequest getRequest(  ) {
    return (HttpServletRequest) parameterProviders.get( "path" ).getParameter( "httprequest" );
  }

  protected HttpServletResponse getResponse(  ) {
    return (HttpServletResponse) parameterProviders.get( "path" ).getParameter( "httpresponse" );
  }

  protected IParameterProvider getRequestParameters(  ) {
    return parameterProviders.get( "request" );
  }

  protected IParameterProvider getPathParameters(  ) {
    return parameterProviders.get( "path" );
  }

  protected String getRequestParameterAsString( String parameter, String defaultValue ) throws UnsupportedEncodingException {
    return getRequestParameterAsString( parameter, defaultValue, CharsetHelper.getEncoding(  ) );
  }

  protected String getRequestParameterAsString( String parameter, String defaultValue, String encoding )
    throws UnsupportedEncodingException {

    String enc = StringUtils.isEmpty( encoding ) ? CharsetHelper.getEncoding(  ) : encoding;

    if ( getRequestParameters(  ) != null && getRequestParameters(  ).hasParameter( parameter ) ) {
      return URLDecoder.decode( getRequestParameters(  ).getStringParameter( parameter, defaultValue ), enc );
    }
    return defaultValue;
  }

  protected String getPathParameterAsString( String parameter, String defaultValue ) throws UnsupportedEncodingException {
    return getPathParameterAsString( parameter, defaultValue, CharsetHelper.getEncoding(  ) );
  }

  protected String getPathParameterAsString( String parameter, String defaultValue, String encoding )
    throws UnsupportedEncodingException {

    String enc = StringUtils.isEmpty( encoding ) ? CharsetHelper.getEncoding(  ) : encoding;

    if ( getPathParameters(  ) != null && getPathParameters(  ).hasParameter( parameter ) ) {
      return URLDecoder.decode( getPathParameters(  ).getStringParameter( parameter, defaultValue ), enc );
    }
    return defaultValue;
  }

  protected String getDefaultPath( String path ) {
    return null;
  }

  private boolean canAccessMethod( Method method, Exposed exposed ) {
    if ( exposed != null ) {

      AccessLevel accessLevel = exposed.accessLevel(  );
      if ( accessLevel != null ) {

        boolean accessible = false;
        switch ( accessLevel ) {
          case ADMIN:
            accessible = SystemUtils.canAdminister(  );
            break;
          case ROLE:
            String role = exposed.role(  );
            if ( !StringUtils.isEmpty( role ) ) {
              accessible = SecurityHelper.getInstance(  ).isGranted( PentahoSessionHolder.getSession(  ), new SimpleGrantedAuthority( role ) );
            }
            break;
          case PUBLIC:
            accessible = true;
            break;
          default:
            logger.error( "Unsupported AccessLevel " + accessLevel );
        }

        return accessible;
      }

    }
    return false;
  }


  protected boolean invokeMethod( final String methodName, final Method method )
    throws InvocationTargetException, IllegalArgumentException, IllegalAccessException, IOException {

    Exposed exposed = method.getAnnotation( Exposed.class );

    if ( canAccessMethod( method, exposed ) ) {

      Audited audited = method.getAnnotation( Audited.class );
      UUID uuid = null;
      long start = System.currentTimeMillis(  );
      if ( audited != null ) {
        uuid = CpfAuditHelper.startAudit( getPluginName(  ), audited.action(  ), getObjectName(  ), userSession, this, getRequestParameters(  ) );

        if ( uuid != null ) {
          setInstanceId( uuid.toString() );
        }
      }

      final OutputStream out = getResponseOutputStream( exposed.outputType(  ) );
      setResponseHeaders( exposed.outputType(  ) );
      try {
        method.invoke( this, out );
      } finally {
        if ( audited != null ) {
          CpfAuditHelper.endAudit( getPluginName(  ), audited.action(  ), getObjectName(  ), userSession, this, start, uuid, System.currentTimeMillis(  ) );
        }
      }

      return true;
    }
    String msg = "Method " + methodName + " not exposed or user does not have required permissions.";
    logger.error( msg );
    getResponse(  ).sendError( HttpServletResponse.SC_FORBIDDEN, msg );
    return false;
  }

  protected void redirect( String method ) {

    final HttpServletResponse response = (HttpServletResponse) parameterProviders.get( "path" ).getParameter( "httpresponse" );

    if ( response == null ) {
      logger.error( "response not found" );
      return;
    }
    try {
      response.sendRedirect( method );
    } catch ( IOException e ) {
      logger.error( "could not redirect", e );
    }
  }

  /**
   * Write to OutputStream using defined encoding.
   *
   * @param out
   * @param contents
   * @throws IOException
   */
  protected void writeOut( OutputStream out, String contents ) throws IOException {
    IOUtils.write( contents, out, getEncoding(  ) );
  }

  protected void writeOut( OutputStream out, JsonSerializable contents ) throws IOException, JSONException {
    IOUtils.write( contents.toJSON(  ).toString(  ), out, getEncoding(  ) );
  }

  @Override
  public Log getLogger(  ) {
    return logger;
  }

  protected void setResponseHeaders( final String mimeType ) {
    setResponseHeaders( mimeType, 0, null );
  }

  protected void setResponseHeaders( final String mimeType, final String attachmentName ) {
    setResponseHeaders( mimeType, 0, attachmentName );
  }

  protected void setResponseHeaders( final String mimeType, final int cacheDuration, final String attachmentName ) {
    // Make sure we have the correct mime type

    final IMimeTypeListener mimeTypeListener = outputHandler.getMimeTypeListener(  );
    if ( mimeTypeListener != null ) {
      mimeTypeListener.setMimeType( mimeType );
    }

    final HttpServletResponse response = getResponse(  );

    if ( response == null ) {
      logger.warn( "Parameter 'httpresponse' not found!" );
      return;
    }

    response.setHeader( "Content-Type", mimeType );

    if ( attachmentName != null ) {
      response.setHeader( "content-disposition", "attachment; filename=" + attachmentName );
    } // Cache?

    if ( cacheDuration > 0 ) {
      response.setHeader( "Cache-Control", "max-age=" + cacheDuration );
    } else {
      response.setHeader( "Cache-Control", "max-age=0, no-store" );
    }
  }

  protected void copyParametersFromProvider( Map<String, Object> params, IParameterProvider provider ) {
    @SuppressWarnings( "unchecked" )
    Iterator<String> paramNames = provider.getParameterNames(  );
    while ( paramNames.hasNext(  ) ) {
      String paramName = paramNames.next(  );
      params.put( paramName, provider.getParameter( paramName ) );
    }
  }
}

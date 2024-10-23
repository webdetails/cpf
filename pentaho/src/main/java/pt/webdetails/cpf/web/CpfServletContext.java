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

package pt.webdetails.cpf.web;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Enumeration;
import java.util.Hashtable;
import java.util.LinkedHashSet;
import java.util.Properties;
import java.util.Set;
import java.util.Map;

import jakarta.servlet.Filter;
import jakarta.servlet.FilterRegistration;
import jakarta.servlet.RequestDispatcher;
import jakarta.servlet.Servlet;
import jakarta.servlet.ServletContext;
import jakarta.servlet.ServletRegistration;
import jakarta.servlet.SessionCookieConfig;
import jakarta.servlet.SessionTrackingMode;
import jakarta.servlet.ServletException;
import java.util.EventListener;
import jakarta.servlet.descriptor.JspConfigDescriptor;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import org.springframework.core.io.ResourceLoader;
import org.springframework.core.io.Resource;
import org.springframework.core.io.DefaultResourceLoader;
import org.springframework.util.ObjectUtils;


/**
 * @author diogomariano
 */
class CpfServletContext implements ServletContext {
  private final Log logger = LogFactory.getLog( getClass(  ) );

  private String resourceBasePath;
  private ResourceLoader resourceLoader;

  private static final String TEMP_DIR_SYSTEM_PROPERTY = "java.io.tmpdir";
  private final Hashtable attributes = new Hashtable(  );
  private final Properties initParameters = new Properties(  );


  private String servletContextName = "MockServletContext";

  public CpfServletContext(  ) {
    this( "", null );
  }

  public CpfServletContext( String resourceBasePath ) {
    this( resourceBasePath, null );
  }

  public CpfServletContext( ResourceLoader resourceLoader ) {
    this( "", resourceLoader );
  }

  public CpfServletContext( String resourceBasePath, ResourceLoader resourceLoader ) {
    this.resourceBasePath = ( resourceBasePath != null ? resourceBasePath : "" );
    this.resourceLoader = ( resourceLoader != null ? resourceLoader : new DefaultResourceLoader(  ) );
//
//         // Use JVM temp dir as ServletContext temp dir.
//         String tempDir = System.getProperty( TEMP_DIR_SYSTEM_PROPERTY );
//         if ( tempDir != null ) {
//             this.attributes.put( WebUtils.TEMP_DIR_CONTEXT_ATTRIBUTE, new File( tempDir ) );
//         }
  }


  @Override
  public String getContextPath() {
    return "";
  }

  @Override
  public ServletContext getContext( String string ) {
    throw new UnsupportedOperationException( "Not supported yet." );
  }

  @Override
  public int getMajorVersion(  ) {
    throw new UnsupportedOperationException( "Not supported yet." );
  }

  @Override
  public int getMinorVersion(  ) {
    throw new UnsupportedOperationException( "Not supported yet." );
  }

  @Override
  public int getEffectiveMajorVersion() {
    return 0;
  }

  @Override
  public int getEffectiveMinorVersion() {
    return 0;
  }

  @Override
  public String getMimeType( String string ) {
    throw new UnsupportedOperationException( "Not supported yet." );
  }

  protected String getResourceLocation( String path ) {
    if ( !path.startsWith( "/" ) ) {
      path = "/" + path;
    }
    return this.resourceBasePath + path;
  }

  @Override
  public Set getResourcePaths( String path ) {
    String actualPath = ( path.endsWith( "/" ) ? path : path + "/" );
    Resource resource = this.resourceLoader.getResource( getResourceLocation( actualPath ) );
    try {
      File file = resource.getFile(  );
      String[] fileList = file.list(  );
      if ( ObjectUtils.isEmpty( fileList ) ) {
        return null;
      }
      Set resourcePaths = new LinkedHashSet( fileList.length );
      for ( int i = 0; i < fileList.length; i++ ) {
        String resultPath = actualPath + fileList[i];
        if ( resource.createRelative( fileList[i] ).getFile(  ).isDirectory(  ) ) {
          resultPath += "/";
        }
        resourcePaths.add( resultPath );
      }
      return resourcePaths;
    } catch ( IOException ex ) {
      logger.warn( "Couldn't get resource paths for " + resource, ex );
      return null;
    }
  }

  @Override
  public URL getResource( String path ) throws MalformedURLException {
    Resource resource = this.resourceLoader.getResource( getResourceLocation( path ) );
    if ( !resource.exists(  ) ) {
      return null;
    }
    try {
      return resource.getURL(  );
    } catch ( MalformedURLException ex ) {
      throw ex;
    } catch ( IOException ex ) {
      logger.warn( "Couldn't get URL for " + resource, ex );
      return null;
    }
  }

  @Override
  public InputStream getResourceAsStream( String path ) {
    Resource resource = this.resourceLoader.getResource( getResourceLocation( path ) );
    if ( !resource.exists(  ) ) {
      return null;
    }
    try {
      return resource.getInputStream(  );
    } catch ( IOException ex ) {
      logger.warn( "Couldn't open InputStream for " + resource, ex );
      return null;
    }
  }

  @Override
  public RequestDispatcher getRequestDispatcher( String path ) {
    if ( !path.startsWith( "/" ) ) {
      throw new IllegalArgumentException( "RequestDispatcher path at ServletContext level must start with '/'" );
    }
    return new CpfRequestDispatcher( path );
  }

  @Override
  public RequestDispatcher getNamedDispatcher( String string ) {
    throw new UnsupportedOperationException( "Not supported yet." );
  }

  @Override
  public Servlet getServlet( String string ) throws ServletException {
    throw new UnsupportedOperationException( "Not supported yet." );
  }

  @Override
  public Enumeration getServlets(  ) {
    throw new UnsupportedOperationException( "Not supported yet." );
  }

  @Override
  public Enumeration getServletNames(  ) {
    throw new UnsupportedOperationException( "Not supported yet." );
  }

  @Override
  public void log( String message ) {
    logger.info( message );
  }

  @Override
  public void log( Exception ex, String message ) {
    logger.info( message, ex );
  }

  @Override
  public void log( String message, Throwable ex ) {
    logger.info( message, ex );
  }

  @Override
  public String getRealPath( String path ) {
    Resource resource = this.resourceLoader.getResource( getResourceLocation( path ) );
    try {
      return resource.getFile(  ).getAbsolutePath(  );
    } catch ( IOException ex ) {
      logger.warn( "Couldn't determine real path of resource " + resource, ex );
      return null;
    }
  }

  @Override
  public String getServerInfo(  ) {
    return "CpfServletContext";
  }

  @Override
  public String getInitParameter( String name ) {
    return this.initParameters.getProperty( name );
  }

  public void addInitParameter( String name, String value ) {
    this.initParameters.setProperty( name, value );
  }


  @Override
  public Enumeration getInitParameterNames(  ) {
    return this.initParameters.keys(  );
  }

  @Override
  public boolean setInitParameter(String s, String s1) {
    return false;
  }

  @Override
  public Object getAttribute( String name ) {
    return this.attributes.get( name );
  }

  @Override
  public Enumeration getAttributeNames(  ) {
    return this.attributes.keys(  );
  }

  @Override
  public void setAttribute( String name, Object value ) {
    if ( name != null ) {
      this.attributes.put( name, value );
    } else {
      this.attributes.remove( name );
    }
  }

  @Override
  public void removeAttribute( String name ) {
    this.attributes.remove( name );
  }

  @Override
  public String getServletContextName(  ) {
    return servletContextName;
  }

  @Override
  public ServletRegistration.Dynamic addServlet( String s, String s1 ) {
    return null;
  }

  @Override
  public ServletRegistration.Dynamic addServlet( String s, Servlet servlet ) {
    return null;
  }

  @Override
  public ServletRegistration.Dynamic addServlet( String s, Class<? extends Servlet> aClass ) {
    return null;
  }

  @Override
  public ServletRegistration.Dynamic addJspFile( String s, String s1 ) {
    return null;
  }

  @Override
  public <T extends Servlet> T createServlet( Class<T> aClass ) throws ServletException {
    return null;
  }

  @Override
  public ServletRegistration getServletRegistration( String s ) {
    return null;
  }

  @Override
  public Map<String, ? extends ServletRegistration> getServletRegistrations() {
    return Map.of();
  }

  @Override
  public FilterRegistration.Dynamic addFilter( String s, String s1 ) {
    return null;
  }

  @Override
  public FilterRegistration.Dynamic addFilter( String s, Filter filter ) {
    return null;
  }

  @Override
  public FilterRegistration.Dynamic addFilter( String s, Class<? extends Filter> aClass ) {
    return null;
  }

  @Override
  public <T extends Filter> T createFilter( Class<T> aClass ) throws ServletException {
    return null;
  }

  @Override
  public FilterRegistration getFilterRegistration( String s ) {
    return null;
  }

  @Override
  public Map<String, ? extends FilterRegistration> getFilterRegistrations() {
    return Map.of();
  }

  @Override
  public SessionCookieConfig getSessionCookieConfig() {
    return null;
  }

  @Override
  public void setSessionTrackingModes( Set<SessionTrackingMode> set ) {

  }

  @Override
  public Set<SessionTrackingMode> getDefaultSessionTrackingModes() {
    return Set.of();
  }

  @Override
  public Set<SessionTrackingMode> getEffectiveSessionTrackingModes() {
    return Set.of();
  }

  @Override
  public void addListener( String s ) {

  }

  @Override
  public <T extends EventListener> void addListener( T t ) {

  }

  @Override
  public void addListener( Class<? extends EventListener> aClass ) {

  }

  @Override
  public <T extends EventListener> T createListener( Class<T> aClass ) throws ServletException {
    return null;
  }

  @Override
  public JspConfigDescriptor getJspConfigDescriptor() {
    return null;
  }

  @Override
  public ClassLoader getClassLoader() {
    return null;
  }

  @Override
  public void declareRoles( String... strings ) {

  }

  @Override
  public String getVirtualServerName() {
    return "";
  }

  @Override
  public int getSessionTimeout() {
    return 0;
  }

  @Override
  public void setSessionTimeout( int i ) {

  }

  @Override
  public String getRequestCharacterEncoding() {
    return "";
  }

  @Override
  public void setRequestCharacterEncoding( String s ) {

  }

  @Override
  public String getResponseCharacterEncoding() {
    return "";
  }

  @Override
  public void setResponseCharacterEncoding( String s ) {

  }

  public void setServletContextName(  ) {
    this.servletContextName = servletContextName;
  }

}

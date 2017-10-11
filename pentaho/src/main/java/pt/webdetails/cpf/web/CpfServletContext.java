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
import javax.servlet.RequestDispatcher;
import javax.servlet.Servlet;
import javax.servlet.ServletContext;
import javax.servlet.ServletException;

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

  public void setServletContextName(  ) {
    this.servletContextName = servletContextName;
  }

}

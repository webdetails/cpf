/*!
* Copyright 2002 - 2013 Webdetails, a Pentaho company.  All rights reserved.
* 
* This software was developed by Webdetails and is provided under the terms
* of the Mozilla Public License, Version 2.0, or any later version. You may not use
* this file except in compliance with the license. If you need a copy of the license,
* please go to  http://mozilla.org/MPL/2.0/. The Initial Developer is Webdetails.
*
* Software distributed under the Mozilla Public License is distributed on an "AS IS"
* basis, WITHOUT WARRANTY OF ANY KIND, either express or  implied. Please refer to
* the license for the specific language governing your rights and limitations.
*/

package pt.webdetails.cpf;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

import org.apache.commons.io.IOUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import pt.webdetails.cpf.repository.api.IContentAccessFactory;
import pt.webdetails.cpf.repository.api.IReadAccess;

/**
 *
 * @author pdpi
 */
public class CpfProperties extends Properties {

  private static final long serialVersionUID = 1L;
  private static CpfProperties instance;
  private static final Log logger = LogFactory.getLog( CpfProperties.class );
  private static String PROPERTIES_FILE = "config.properties";

  private CpfProperties( IContentAccessFactory accessor ) {
    loadSettings( accessor );
  }

  public static CpfProperties getInstance() {
    if ( instance == null ) {
      instance = new CpfProperties( PluginEnvironment.repository() );
    }
    return instance;
  }

  private boolean loadProperties( IReadAccess location, String fileName ) throws IOException {
    if ( location.fileExists( fileName ) ) {
      loadAndClose( location.getFileInputStream( fileName ) );
      return true;
    }
    return false;
  }

  private void loadSettings( IContentAccessFactory accessor ) {
    try {

      // 1) a config.properties inside the jar
      // this one should always exist
      if ( !loadClassProperties( PROPERTIES_FILE ) ) {
        logger.warn( "No CPF base settings." );
      }

      // 2) a config.properties in repository:cpf/config.properties
      // factory not so good for this one
      IReadAccess inRepositoryCpf = accessor.getPluginRepositoryReader( "../cpf" );
      if ( !loadProperties( inRepositoryCpf, PROPERTIES_FILE ) && logger.isDebugEnabled() ) {
        logger.debug( "No global CPF settings." );
      }

      // 3) in system/<plugin>/config.properties
      IReadAccess inSystem = PluginEnvironment.repository().getPluginSystemReader( "" );
      if ( !loadProperties( inSystem, PROPERTIES_FILE ) && logger.isDebugEnabled() ) {
        logger.debug( "No plugin-specific CPF settings." );
      }

    } catch ( IOException ioe ) {
      logger.error( "Failed to read CPF settings", ioe );
    }
  }

  private boolean loadClassProperties( String fileName ) throws IOException {
    InputStream file = getClass().getResourceAsStream( fileName );
    if ( file != null ) {
      loadAndClose( file );
      return true;
    }
    // not found
    return false;
  }

  public boolean getBooleanProperty( String property, boolean defaultValue ) {
    String propertyValue = getProperty( property, null );
    if ( !StringUtils.isEmpty( propertyValue ) ) {
      return Boolean.parseBoolean( propertyValue );
    }
    return defaultValue;
  }

  public int getIntProperty( String property, int defaultValue ) {
    String propertyValue = getProperty( property, null );
    if ( !StringUtils.isEmpty( propertyValue ) ) {
      try {
        return Integer.parseInt( propertyValue );
      } catch ( NumberFormatException e ) {
        logger.error( "getIntProperty: " + property + " is not a valid int value." );
      }
    }
    return defaultValue;
  }

  public long getLongProperty( String property, long defaultValue ) {
    String propertyValue = getProperty( property, null );
    if ( !StringUtils.isEmpty( propertyValue ) ) {
      try {
        return Long.parseLong( propertyValue );
      } catch ( NumberFormatException e ) {
        logger.error( "getLongProperty: " + property + " is not a valid long value." );
      }
    }
    return defaultValue;
  }

  private void loadAndClose( InputStream input ) throws IOException {
    try {
      load( input );
    } finally {
      IOUtils.closeQuietly( input );
    }
  }

}

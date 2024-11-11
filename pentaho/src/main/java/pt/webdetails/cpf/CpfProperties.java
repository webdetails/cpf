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

import java.util.concurrent.Callable;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import org.pentaho.platform.engine.security.SecurityHelper;
import pt.webdetails.cpf.repository.api.IContentAccessFactory;
import pt.webdetails.cpf.repository.api.IReadAccess;

public class CpfProperties extends AbstractCpfProperties {

  private static final long serialVersionUID = 1L;
  private static CpfProperties instance;
  private static final Log logger = LogFactory.getLog( CpfProperties.class );

  private CpfProperties( IContentAccessFactory accessor ) {
    super( accessor );
  }

  public static CpfProperties getInstance() {
    if ( instance == null ) {
      instance = new CpfProperties( PluginEnvironment.repository() );
    }
    return instance;
  }

  protected boolean loadAsSystem( final IReadAccess inRepositoryCpf ) {
    // the properties file will probably not be publicly readable, so if a non admin user logs in
    // we must make sure we are reading with top priority still
    try {
      return SecurityHelper.getInstance().runAsSystem( new Callable<Boolean>() {
        @Override public Boolean call() throws Exception {
          return loadProperties( inRepositoryCpf, PROPERTIES_FILE );
        }
      } );
    } catch ( Exception e ) {
      logger.warn( "Couldn't load '" + PROPERTIES_FILE + "' as system", e );
      return false;
    }
  }

}

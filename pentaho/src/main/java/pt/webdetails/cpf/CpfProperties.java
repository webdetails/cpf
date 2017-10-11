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

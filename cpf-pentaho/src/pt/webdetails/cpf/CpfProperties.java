/*!
 * Copyright 2002 - 2015 Webdetails, a Pentaho company. All rights reserved.
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

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

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
    try {
      return loadProperties( inRepositoryCpf, PROPERTIES_FILE );
    } catch ( IOException e ) {
      return false;
    }
  }

}

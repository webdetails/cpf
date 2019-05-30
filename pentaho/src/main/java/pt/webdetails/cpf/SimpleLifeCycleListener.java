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

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.pentaho.platform.api.engine.IPlatformReadyListener;
import org.pentaho.platform.api.engine.IPluginLifecycleListener;
import org.pentaho.platform.api.engine.PluginLifecycleException;
import pt.webdetails.cpf.persistence.PersistenceEngine;

public abstract class SimpleLifeCycleListener implements IPluginLifecycleListener, IPlatformReadyListener {

  static Log logger = LogFactory.getLog( SimpleLifeCycleListener.class );

  @Override
  public void init() throws PluginLifecycleException {
    PluginEnvironment.init( getEnvironment() );
  }

  @Override
  public void loaded() throws PluginLifecycleException {
  }

  @Override
  public void unLoaded() throws PluginLifecycleException {
    logger.debug( "Shutting down and shutting down Orient DB " );

    PersistenceEngine.shutdown();
  }

  @Override
  public void ready() {
    final CpfProperties cpfProperties = CpfProperties.getInstance();

    final boolean usePersistence = cpfProperties.getBooleanProperty( "USE_PERSISTENCE", false );
    if ( usePersistence ) {
      PersistenceEngine.getInstance();
    }
  }

  public abstract PluginEnvironment getEnvironment();
}

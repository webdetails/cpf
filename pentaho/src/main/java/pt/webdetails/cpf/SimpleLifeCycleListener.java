/*! ******************************************************************************
 *
 * Pentaho
 *
 * Copyright (C) 2024 by Hitachi Vantara, LLC : http://www.pentaho.com
 *
 * Use of this software is governed by the Business Source License included
 * in the LICENSE.TXT file.
 *
 * Change Date: 2028-08-13
 ******************************************************************************/

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
  public void ready() throws PluginLifecycleException {
    final CpfProperties cpfProperties = CpfProperties.getInstance();

    final boolean usePersistence = cpfProperties.getBooleanProperty( "USE_PERSISTENCE", false );
    if ( usePersistence ) {
      PersistenceEngine.getInstance();
    }
  }

  public abstract PluginEnvironment getEnvironment();
}

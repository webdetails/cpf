/* This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this file,
 * You can obtain one at http://mozilla.org/MPL/2.0/. */

package pt.webdetails.cpf;

import org.pentaho.platform.api.engine.IPluginLifecycleListener;
import org.pentaho.platform.api.engine.PluginLifecycleException;
import pt.webdetails.cpf.persistence.PersistenceEngine;

/**
 *
 * @author pdpi
 */
public abstract class SimpleLifeCycleListener implements IPluginLifecycleListener {

    @Override
    public void init() throws PluginLifecycleException {
      PluginEnvironment.init(getEnvironment());
      if (CpfProperties.getInstance().getBooleanProperty("USE_PERSISTENCE",false)) {
          PersistenceEngine.getInstance();
      }
    }

    @Override
    public void loaded() throws PluginLifecycleException {
    }

    @Override
    public void unLoaded() throws PluginLifecycleException {
    }

    public abstract PluginEnvironment getEnvironment();
}

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
public class SimpleLifeCycleListener implements IPluginLifecycleListener {

    @Override
    public void init() throws PluginLifecycleException {
        CpfProperties settings = CpfProperties.getInstance();
        if (settings.getProperty("USE_PERSISTENCE","false").toLowerCase().equals("true")) {
            PersistenceEngine.getInstance();
        }
    }

    @Override
    public void loaded() throws PluginLifecycleException {
    }

    @Override
    public void unLoaded() throws PluginLifecycleException {
    }
}

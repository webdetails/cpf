/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
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
        Settings settings = Settings.getInstance();
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

/* This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this file,
 * You can obtain one at http://mozilla.org/MPL/2.0/. */

package pt.webdetails.cpf.resources;

import java.io.UnsupportedEncodingException;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.pentaho.platform.engine.core.system.PentahoSystem;
import org.pentaho.platform.api.engine.IPluginResourceLoader;

/**
 * 
 * @author dfscm
 */
public class PentahoPluginResourceLoader implements IResourceLoader{
    private IPluginResourceLoader resourceLoader;
    
    public PentahoPluginResourceLoader(){
        this(null);
    }
    
    public PentahoPluginResourceLoader(IPluginResourceLoader resourceLoader){
        this.resourceLoader = resourceLoader == null ? PentahoSystem.get(IPluginResourceLoader.class, null) : resourceLoader;
    }
        
    @Override
    public String getResourceAsString(Class<? extends Object> type, String string) {
        try {
            return resourceLoader.getResourceAsString(type, string);
        } catch (UnsupportedEncodingException ex) {
            Logger.getLogger(PentahoPluginResourceLoader.class.getName()).log(Level.SEVERE, null, ex);
            return "";
        }
    }

    @Override
    public String getPluginSetting(Class<?> type, String string) {
        return resourceLoader.getPluginSetting(type, string);
    }

}

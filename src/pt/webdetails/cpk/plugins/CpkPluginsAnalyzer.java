/* This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this file,
 * You can obtain one at http://mozilla.org/MPL/2.0/. */
package pt.webdetails.cpk.plugins;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import org.codehaus.jackson.map.ObjectMapper;
import pt.webdetails.cpf.plugins.Plugin;
import pt.webdetails.cpf.plugins.PluginsAnalyzer;

/**
 *
 * @author Luis Paulo Silva<luis.silva@webdetails.pt>
 */
public class CpkPluginsAnalyzer extends PluginsAnalyzer{
    
    private List<CpkPlugin> pluginsCpk;
    
    public CpkPluginsAnalyzer(){
        pluginsCpk = new ArrayList<CpkPlugin>();
        buildCpkPluginsList();
    }
    
    private void buildCpkPluginsList(){
        super.refresh();
        List<Plugin> installedPlugins = super.getInstalledPlugins();
        
        for(Plugin plugin: installedPlugins){
            CpkPlugin cpkPlugin = new CpkPlugin(plugin.getPath());
            if(cpkPlugin.isCpkPlugin()){
                pluginsCpk.add(cpkPlugin);
            }
        }
    }
    
    public List<CpkPlugin> getInstalledCpkPlugins(){
        return pluginsCpk;
    }
    
    public String getCpkPluginsListJson() throws IOException{
        ObjectMapper mapper = new ObjectMapper();
        return mapper.writeValueAsString(this.pluginsCpk);
    }
    
}

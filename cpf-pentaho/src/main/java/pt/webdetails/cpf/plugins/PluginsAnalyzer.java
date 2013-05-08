/* This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this file,
 * You can obtain one at http://mozilla.org/MPL/2.0/. */
package pt.webdetails.cpf.plugins;

import java.io.File;
import java.io.FilenameFilter;
import java.util.ArrayList;
import java.util.List;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.dom4j.Node;
import pt.webdetails.cpf.repository.IRepositoryAccess;





/**
 *
 * @author Luis Paulo Silva
 */
public class PluginsAnalyzer {
    
    private List<Plugin> installedPlugins;
    private IRepositoryAccess repoAccess;//XXX nedds to be initialized
    protected Log logger = LogFactory.getLog(this.getClass());

    public PluginsAnalyzer(IRepositoryAccess repoAccess){
        this.repoAccess=repoAccess;
        
    }
    public PluginsAnalyzer(){}
    public void refresh(){
        buildPluginsList();
    }
    
    public List<Plugin> getInstalledPlugins(){
        return installedPlugins;
    }

    
     public class PluginWithEntity {
        private Plugin plugin;
        private Node registeredEntity;
        
        public PluginWithEntity(Plugin plugin, Node registeredEntity) {
            this.plugin = plugin;
            this.registeredEntity = registeredEntity;
        }

        /**
         * @return the plugin
         */
        public Plugin getPlugin() {
            return plugin;
        }

        /**
         * @return the registeredEntity
         */
        public Node getRegisteredEntity() {
            return registeredEntity;
        }
    }; 
    
    
    public List<PluginWithEntity> getRegisteredEntities(String entityName) {
        List<PluginWithEntity> result = new ArrayList<PluginWithEntity>();
        for (Plugin p: installedPlugins) {
            Node registeredEntity = p.getRegisteredEntities(entityName);
            if (registeredEntity != null)
                result.add(new PluginWithEntity(p, registeredEntity));
        }
                
        return result;
    }
    
    private void buildPluginsList(){
        ArrayList<Plugin> plugins = new ArrayList<Plugin>();
        Plugin plugin = null;//pt.webdetails.cpf.plugins.Plugin plugin = null;
        String localPath = repoAccess.getSolutionPath("system/");
        
        String [] pluginDirs = new File(localPath).list(new FilenameFilter() {

            @Override
            public boolean accept(File dir, String name) {
                return new File(dir,name).isDirectory();
            }
        });
        
        for(String pluginDir : pluginDirs){
                
                
            plugin = new Plugin(localPath+pluginDir);//plugin = (pt.webdetails.cpf.plugins.Plugin)new Plugin(localPath+pluginDir);
            if(plugin.hasPluginXML()){
                plugins.add(plugin);
            }
        }
        
        this.installedPlugins = plugins;
    }
    
    public List<Plugin> getPlugins(IPluginFilter filter){
     List<Plugin> pluginsList = new ArrayList<Plugin>();
     
     for (Plugin plugin : installedPlugins) {     
       if (filter.include(plugin)){
         pluginsList.add(plugin);
       }
       
     }
     
     return pluginsList;
   }  
    
}
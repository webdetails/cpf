/* This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this file,
 * You can obtain one at http://mozilla.org/MPL/2.0/. */
package pt.webdetails.cpf.plugins;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.dom4j.Document;
import org.dom4j.Node;
import org.pentaho.platform.api.engine.IPluginManager;
import org.pentaho.platform.engine.core.system.PentahoSessionHolder;
import org.pentaho.platform.engine.core.system.PentahoSystem;
import org.pentaho.platform.util.xml.dom4j.XmlDom4JHelper;

/**
 *
 * @author Luis Paulo Silva
 */
public class PluginsAnalyzer {
    
    private ArrayList<Plugin> installedPlugins;
    private String PLUGIN_XML_FILENAME = "plugin.xml";
    private String SETTINGS_XML_FILENAME = "settings.xml";
        
    public PluginsAnalyzer(){
        //Empty
    }
    
    public List<Plugin> getInstalledPlugins(){
        return installedPlugins;
    }

    public void setInstalledPlugins(ArrayList<Plugin> installedPlugins) {
        this.installedPlugins = installedPlugins;
    }
    
    private void buildPluginsList(){
        ArrayList<Plugin> plugins = new ArrayList<Plugin>();
        HashMap<String,List<String>> pluginsXmls = getPluginXmlsPath();
        
        for(String pluginName : pluginsXmls.keySet()){
                
            for(String xmlFilePath : pluginsXmls.get(pluginName)){
                List<Node> documentNodes = getXmlFileContent(xmlFilePath);
                String name = null;
                String id = null;
                String description = null;
                String company = null;
                String companyUrl = null;
                List<Entity> entities = new ArrayList<Entity>();
                
                if(xmlFilePath.contains(PLUGIN_XML_FILENAME)){
                    for(Node node : documentNodes){

                        id = node.valueOf("/plugin/@title");
                        name = node.valueOf("/plugin/content-types/content-type/title");
                        description = node.valueOf("/plugin/content-types/content-type/description");
                        company = node.valueOf("/plugin/content-types/content-type/company/@name");
                        companyUrl = node.valueOf("/plugin/content-types/content-type/company/@url");
                        
                    }
                }else if(xmlFilePath.contains(SETTINGS_XML_FILENAME)){
                    for(Node node : documentNodes){
                        
                    }
                }
                
            }
        }
        
        setInstalledPlugins(plugins);
    }
    
    private HashMap<String,List<String>> getPluginXmlsPath(){
        IPluginManager pluginManager = PentahoSystem.get(IPluginManager.class, PentahoSessionHolder.getSession());
        List<String> pluginsNames = pluginManager.getRegisteredPlugins();
        
        
        
        ArrayList<String> pluginXmlsPath;
        HashMap<String,List<String>> pluginXmls = new HashMap<String, List<String>>();
        
        for(String name : pluginsNames){
            String pluginPath = PentahoSystem.getApplicationContext().getSolutionPath("system/"+name+"/");
            pluginXmlsPath = new ArrayList<String>();
            
            if(new File(pluginPath+PLUGIN_XML_FILENAME).isFile()){
                pluginXmlsPath.add(pluginPath+PLUGIN_XML_FILENAME);
            }
            
            if(new File(pluginPath+SETTINGS_XML_FILENAME).isFile()){
                pluginXmlsPath.add(pluginPath+SETTINGS_XML_FILENAME);
            }
            
            pluginXmls.put(name, pluginXmlsPath);
        }
        
        return pluginXmls;
    }
    
    private List<Node> getXmlFileContent(String filePath){
        FileInputStream fis = null;
        File xmlFile = null;
        Document doc = null;
        List<Node> nodes = null;
        
        try {
            xmlFile = new File(filePath);
            fis = new FileInputStream(xmlFile);
            Document xml = XmlDom4JHelper.getDocFromStream(fis, null);
            nodes = xml.selectNodes("plugin");
            
            
            
        } catch (Exception ex) {
            Logger.getLogger(PluginsAnalyzer.class.getName()).log(Level.SEVERE, null, ex);
        }
        
        return nodes;
    }
    
}

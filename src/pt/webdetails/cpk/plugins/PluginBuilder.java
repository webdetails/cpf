/* This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this file,
 * You can obtain one at http://mozilla.org/MPL/2.0/. */
package pt.webdetails.cpk.plugins;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.codehaus.jackson.JsonNode;
import pt.webdetails.cpf.plugins.Plugin;
import pt.webdetails.cpf.plugins.PluginsAnalyzer;
import pt.webdetails.cpk.CpkEngine;
import pt.webdetails.cpk.elements.IElementType;

/**
 *
 * @author Luis Paulo Silva<luis.silva@webdetails.pt>
 */
public class PluginBuilder {
    
    List<File> files;
    
    public PluginBuilder(JsonNode node){
        buildFilesList(node);
    }
    
    private void buildFilesList(JsonNode node){
        
    }
    
    public void writeFiles(boolean overwrite){
        
        FileOutputStream fos = null;
        if(files != null){
            for(File file : this.files){
                if(fileExists(file) && !overwrite){
                   break;
                }
                try {
                    if(file.canWrite()){
                        fos = new FileOutputStream(file);
                        fos.close();
                    }
                } catch (IOException ex) {
                    Logger.getLogger(PluginBuilder.class.getName()).log(Level.SEVERE, null, ex);
                }

            }
        }
    
    }
    
    private boolean fileExists(File file){
        return file.exists();
    }
    
    private boolean pluginExists(JsonNode node){
        boolean exists = false;
        PluginsAnalyzer pluginsAnalyzer = new PluginsAnalyzer();
        pluginsAnalyzer.refresh();
        
        for(Plugin plugin : pluginsAnalyzer.getInstalledPlugins()){
            if(node.get("id").getTextValue().equals(plugin.getId())){
                exists = true;
                break;
            }
        }
        
        return exists;
    }
    
    private void buildPluginXml(JsonNode node){
        try {
            File pluginXml = File.createTempFile("plugin", ".xml");
            
            
        } catch (Exception ex) {
            Logger.getLogger(PluginBuilder.class.getName()).log(Level.SEVERE, null, ex);
        }
        
    
    }
    
    private void buildSettingXml(JsonNode node){
    
    }
    
    private void buildCpkXml(JsonNode node){
        Map<String,String> typesClass = new HashMap<String, String>();
        
        for(String type : CpkEngine.getInstance().getElementTypes().keySet()){
            typesClass.put(type, getTypeClass(type));
        }
        
        
    }
    
    private boolean knownType(String type){
        IElementType elementType = CpkEngine.getInstance().getElementType(type);
        
        return (elementType == null) ? false: true;
    }
    
    private String getTypeClass(String type){
        IElementType elementType = CpkEngine.getInstance().getElementType(type);
        
        return (elementType != null) ? elementType.getClass().toString().substring(6) : null;
    }
    
    
    
}

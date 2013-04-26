/* This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this file,
 * You can obtain one at http://mozilla.org/MPL/2.0/. */
package pt.webdetails.cpk.plugins;

import java.io.File;
import java.io.FileFilter;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.apache.commons.io.FileDeleteStrategy;
import org.apache.commons.io.FileUtils;
import org.codehaus.jackson.JsonNode;
import org.pentaho.platform.engine.core.system.PentahoSystem;
import pt.webdetails.cpf.plugins.IPluginFilter;
import pt.webdetails.cpf.plugins.Plugin;
import pt.webdetails.cpf.plugins.PluginsAnalyzer;
import pt.webdetails.cpf.utils.PluginUtils;
import pt.webdetails.cpf.utils.ZipUtil;
import pt.webdetails.cpk.CpkEngine;
import pt.webdetails.cpk.elements.IElementType;

/**
 *
 * @author Luis Paulo Silva<luis.silva@webdetails.pt>
 */
public class PluginBuilder {
    
    private List<File> filesList = null;
    private String statusMessage = "There was a problem creating the new plugin!";//default message
    private String id = null,
            name = null, 
            description = null, 
            company = null, 
            packageUrl = null, 
            packageDescription = null, 
            styles = null, 
            components = null;
    
    public PluginBuilder(){
    }
    
    public void buildPlugin(JsonNode node){
        filesList = new ArrayList<File>();
        
        this.id = getNodeValue(node, "id");
        
        if(id == null){
            this.statusMessage = "A plugin must have an ID, please insert an ID (eg. \"cde\")";
            return;
        }
        
        this.name = getNodeValue(node, "name");
        this.company = node.get("company").getTextValue();
        this.description = node.get("description").getTextValue();
        this.components = node.get("components").getTextValue();
        this.styles = node.get("styles").getTextValue();
        this.packageDescription = node.get("packageDescription").getTextValue();
        this.packageUrl = node.get("packageUrl").getTextValue();
        
        /*
         * missing endpoints and dashboards
         */
        
        File cpkDirectory = PluginUtils.getInstance().getPluginDirectory();
        File cpkStubDir = new File(cpkDirectory.getAbsolutePath()+"/stub/");
        File stubZip = new File(cpkStubDir.getAbsolutePath()+"/plugin.zip");
        File stubUnzip = new File(cpkStubDir.getAbsolutePath()+"/.plugin/");
        
        if(stubZip.exists() && !pluginExists()){
            
            /*
             * Lets load the zip and extract it to a temporary directory
             */
            
            ZipUtil zipUtil = new ZipUtil();
            zipUtil.unzip(stubZip, stubUnzip);
            
            
            /*
             * Lets load all the files from the stub location
             */
            
            this.filesList = getAllFilesFromDirectory(stubUnzip); //List of stub files
            
            /*
             * Lets start to configure the content of the files (everything saying cpk will be changed to the plugin ID and so on!)
             */
            
            for(File file : filesList){
                replaceTokensOnFile(file);
            }
            
            
            /*
             * Now that the files configuration is complete let's copy them to their final destination!
             */
            
            for(File file : filesList){
                String relativePath = file.getAbsolutePath().replace(stubUnzip.getAbsolutePath()+File.separator, "");
                String pentahoSystemDir = PentahoSystem.getApplicationContext().getSolutionPath("system/");
                try {
                    FileUtils.copyFile(file, new File(pentahoSystemDir+File.separator+this.id+File.separator+relativePath));
                } catch (IOException ex) {
                    Logger.getLogger(PluginBuilder.class.getName()).log(Level.SEVERE, null, ex);
                }
            }
            
            /*
             * Remove the temporary directory from the file system
             */
            try {
                FileDeleteStrategy.FORCE.delete(stubUnzip);
            } catch (IOException ex) {
                Logger.getLogger(PluginBuilder.class.getName()).log(Level.SEVERE, null, ex);
            }
        
        }else{
            if(!stubZip.exists()){
                this.statusMessage = "There is no stub file, cannot create plugin!";
            }else if(pluginExists()){
                this.statusMessage = "There is already a plugin with this ID, please select another ID for the plugin or uninstall the other plugin first.";
            }
        }
        this.statusMessage = "Plugin created successfully!";
    
    }
    
    private String getNodeValue(JsonNode node, String field){
        String result = null;
        
        if(node.get(field) != null){
            result = node.get(field).getValueAsText();
        }
        
        return result;
    }
    
    public String getStatusMessage(){
        return statusMessage;
    }
    
    private List<File> getAllFilesFromDirectory(File directory){
        
        List<File> result = new ArrayList<File>();
        
        FileFilter filefilter = new FileFilter() {

            @Override
            public boolean accept(File pathname) {
                return pathname.isFile();
            }
        };
        
        FileFilter directoryFilter = new FileFilter() {

            @Override
            public boolean accept(File pathname) {
                return pathname.isDirectory();
            }
        };
        
        
        if(directory.isDirectory()){
            File [] files = directory.listFiles(filefilter);
            File [] directories = directory.listFiles(directoryFilter);
            
            for(File file : files){
                result.add(file);
            }
            
            for(File dir : directories){
                List<File> filesFromDir = getAllFilesFromDirectory(dir);
                
                for(File file : filesFromDir){
                    result.add(file);
                }
            }
            
            
        }
        
        return result;
    }
    
    private void replaceTokensOnFile(File file){
        try {
            Scanner sc = new Scanner(file);
            String content = new String();
            
            while(sc.hasNext()){
                content+=sc.nextLine()+"\n";
            }
            
            content = content.replaceAll("@PLUGIN_ID@", this.id);
            content = content.replaceAll("@PLUGIN_NAME@",  this.name);
            content = content.replaceAll("@PLUGIN_DESCRIPTION@", this.description);
            content = content.replaceAll("@PLUGIN_COMPANY@", this.company);
            content = content.replaceAll("@PLUGIN_PACKAGE_URL@", this.packageUrl);
            content = content.replaceAll("@PLUGIN_PACKAGE_DESCRIPTION@", this.packageDescription);
            content = content.replaceAll("@PLUGIN_STYLES@", "<cde-styles>\n<path>"+this.styles+"</path>\n<cde-styles>\n");
            content = content.replaceAll("@PLUGIN_COMPONENTS@", "<cde-components>\n<path>"+this.components+"</path>\n</cde-components>\n");
            
            /*
             * missing endpoints and dashboards 
             */
            
            
            FileOutputStream fos = new FileOutputStream(file);
            try {
                fos.write(content.getBytes("UTF-8"));
                fos.close();
            } catch (IOException ex) {
                Logger.getLogger(PluginBuilder.class.getName()).log(Level.SEVERE, null, ex);
            }
            
        } catch (FileNotFoundException ex) {
            Logger.getLogger(PluginBuilder.class.getName()).log(Level.SEVERE, null, ex);
        }
        
        
        
        
    }
    

    
    private boolean fileExists(File file){
        return file.exists();
    }
    
    private boolean pluginExists(){
        boolean exists = false;
        PluginsAnalyzer pluginsAnalyzer = new PluginsAnalyzer();
        pluginsAnalyzer.refresh();
        
        IPluginFilter idFilter = new  IPluginFilter() {

            @Override
            public boolean include(Plugin plugin) {
                return plugin.getId().equals(id);
            }
        };
        
        if(pluginsAnalyzer.getPlugins(idFilter).size() > 0 ){
            exists = true;
        }
        
        return exists;
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

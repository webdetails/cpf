/* This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this file,
 * You can obtain one at http://mozilla.org/MPL/2.0/. */
package pt.webdetails.cpf.plugins;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.dom4j.Document;
import org.dom4j.Node;
import org.pentaho.platform.util.xml.dom4j.XmlDom4JHelper;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.pentaho.platform.engine.core.system.PentahoSystem;

/**
 *
 * @author Luis Paulo Silva
 */
public class Plugin {
    private String id;
    private String name;
    private String description;
    private String company;
    private String companyUrl;
    private String path;
    private final String PLUGIN_XML_FILENAME = "plugin.xml";
    private final String SETTINGS_XML_FILENAME = "settings.xml";
    protected Log logger = LogFactory.getLog(this.getClass());
    
    public Plugin(String path){
        setPath(path+"/");
        pluginSelfBuild();
    }
    

    /**
     * 
     * @return Returns the path to the plugin directory (system) 
     */
    public String getPath() {
        return path;
    }

    private void setPath(String path) {
        this.path = path;
    }
    
    /**
     * 
     * @return Returns the company name if defined on the Plugin.xml 
     */
    public String getCompany() {
        return company;
    }

    private void setCompany(String company) {
        this.company = company;
    }

    /**
     * 
     * @return Returns the company URL if defined on the Plugin.xml
     */
    public String getCompanyUrl() {
        return companyUrl;
    }

    private void setCompanyUrl(String companyUrl) {
        this.companyUrl = companyUrl;
    }

    /**
     * 
     * @return Returns the plugin description if defined on the Plugin.xml 
     */
    public String getDescription() {
        return description;
    }

    private void setDescription(String description) {
        this.description = description;
    }

    /**
     * 
     * @return Returns the plugin ID if defined on the Plugin.xml "<plugin title=<ID-HERE>...>"
     */
    public String getId() {
        return id;
    }

    private void setId(String id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    private void setName(String name) {
        this.name = name;
    }
    
    private Node getXmlFileContent(String filePath){
        FileInputStream fis = null;
        BufferedInputStream bis = null;
        File xmlFile = null;
        Document xml = null;
        Node node = null;
        
        try {
            xmlFile = new File(filePath);
            fis = new FileInputStream(xmlFile);
            bis = new BufferedInputStream(fis);
            xml = XmlDom4JHelper.getDocFromStream(bis, null);
            node = xml.getRootElement();

            bis.close();
            fis.close();
        } catch (Exception ex) {
            Logger.getLogger(PluginsAnalyzer.class.getName()).log(Level.SEVERE, null, ex);
        }
        
        return node;
    }
    
    private void pluginSelfBuild(){
        if(hasPluginXML()){
            Node documentNode = getXmlFileContent(getPath()+PLUGIN_XML_FILENAME);
            setId(documentNode.valueOf("/plugin/@title"));
            setName(documentNode.valueOf("/plugin/content-types/content-type/title"));
            setDescription(documentNode.valueOf("/plugin/content-types/content-type/description"));
            setCompany(documentNode.valueOf("/plugin/content-types/content-type/company/@name"));
            setCompanyUrl(documentNode.valueOf("/plugin/content-types/content-type/company/@url"));
        }
    }
    
    public Node getRegisteredEntities(String entityName){
        Node documentNode = null;
        Node node = null;
        if(hasSettingsXML()){
            documentNode = getXmlFileContent(getPath()+SETTINGS_XML_FILENAME);
            node = documentNode.selectSingleNode("/settings"+entityName);
        }
        
        return node;
    }
    
    public boolean hasPluginXML(){
        boolean has = false;
        
        if(new File(getPath()+PLUGIN_XML_FILENAME).exists()){
            has = true;
        }
        
        return has;
    }
    
    public boolean hasSettingsXML(){
        boolean has = false;
        
        if(new File(getPath()+SETTINGS_XML_FILENAME).exists()){
            has = true;
        }
        
        return has;
    }
    
    public String getPluginSolutionPath(){
        return PentahoSystem.getApplicationContext().getSolutionPath("")+"/"+getId()+"/";
    }
    
    
    
}

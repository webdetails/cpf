/*!
* Copyright 2002 - 2013 Webdetails, a Pentaho company.  All rights reserved.
* 
* This software was developed by Webdetails and is provided under the terms
* of the Mozilla Public License, Version 2.0, or any later version. You may not use
* this file except in compliance with the license. If you need a copy of the license,
* please go to  http://mozilla.org/MPL/2.0/. The Initial Developer is Webdetails.
*
* Software distributed under the Mozilla Public License is distributed on an "AS IS"
* basis, WITHOUT WARRANTY OF ANY KIND, either express or  implied. Please refer to
* the license for the specific language governing your rights and limitations.
*/

package pt.webdetails.cpf.plugins;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.dom4j.Document;
import org.dom4j.Node;
import org.pentaho.platform.util.xml.dom4j.XmlDom4JHelper;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.codehaus.jackson.annotate.JsonIgnore;
import org.codehaus.jackson.annotate.JsonProperty;
import org.codehaus.jackson.map.ObjectMapper;
import org.pentaho.platform.engine.core.system.PentahoSystem;
import pt.webdetails.cpf.VersionChecker;
import pt.webdetails.cpf.plugin.CorePlugin;

/**
 *
 * @author Luis Paulo Silva
 */
public class Plugin extends CorePlugin{
    //private String id;
    //private String name;
    private String description;
    private String company;
    private String companyUrl;
    private String companyLogo;
    private String path;
    private String version;
    private final String PLUGIN_XML_FILENAME = "plugin.xml";
    private final String SETTINGS_XML_FILENAME = "settings.xml";
    private final String VERSION_XML_FILENAME = "version.xml";
    protected Log logger = LogFactory.getLog(this.getClass());
    
    public Plugin(String path){
        super();
        if(!path.endsWith("/"))
        {
            setPath(path+"/");
        }else{
            setPath(path);
        }
        pluginSelfBuild();
    }
    
        
    /**
     * 
     * @return Returns the path to the plugin directory (system) 
     */
    @JsonIgnore
    public String getPath() {
        return path;
    }

    public void setPath(String path) {
        this.path = path;
    }
    
    /**
     * 
     * @return Returns the company name if defined on the Plugin.xml 
     */
    @JsonProperty("company")
    public String getCompany() {
        return company;
    }

    public void setCompany(String company) {
        this.company = company;
    }

    /**
     * 
     * @return Returns the company URL if defined on the Plugin.xml
     */
    @JsonProperty("companyUrl")
    public String getCompanyUrl() {
        return companyUrl;
    }

    public void setCompanyUrl(String companyUrl) {
        this.companyUrl = companyUrl;
    }
    
    @JsonProperty("companyLogo")
    public String getCompanyLogo() {
        return companyLogo;
    }

    public void setCompanyLogo(String companyLogo) {
        this.companyLogo = companyLogo;
    }
    /**
     * 
     * @return Returns the plugin description if defined on the Plugin.xml 
     */
    @JsonProperty("description")
    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    /**
     * 
     * @return Returns the plugin ID if defined on the Plugin.xml "<plugin title=<ID-HERE>...>"
     */
    @JsonProperty("id")
    public String getId() {
        return this.id;
    }

  

    @JsonProperty("name")
    public String getName() {
        return this.name;
    }


    
    @JsonIgnore
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
    
    @JsonIgnore
    private void pluginSelfBuild(){
        if(hasPluginXML()){
            Node documentNode = getXmlFileContent(getPath()+PLUGIN_XML_FILENAME);
            setId(documentNode.valueOf("/plugin/@title"));
            setName(documentNode.valueOf("/plugin/content-types/content-type/title"));
            setDescription(documentNode.valueOf("/plugin/content-types/content-type/description"));
            setCompany(documentNode.valueOf("/plugin/content-types/content-type/company/@name"));
            setCompanyUrl(documentNode.valueOf("/plugin/content-types/content-type/company/@url"));
            setCompanyLogo(documentNode.valueOf("/plugin/content-types/content-type/company/@logo"));
        }
        
        if(hasVersionXML()){            
            this.version = new VersionChecker.Version(getXmlFileContent(getPath()+VERSION_XML_FILENAME).getDocument()).toString();
        }else{
            String unspecified = "unspecified or no version.xml present in plugin directory";
            this.version = unspecified;
        }
    }
    
    @JsonIgnore
    public Node getRegisteredEntities(String entityName){
        Node documentNode = null;
        Node node = null;
        if(hasSettingsXML()){
            documentNode = getXmlFileContent(getPath()+SETTINGS_XML_FILENAME);
            node = documentNode.selectSingleNode("/settings"+entityName);
        }
        
        return node;
    }
    
    @JsonIgnore
    public boolean hasPluginXML(){
        boolean has = false;
        
        if(new File(getPath()+PLUGIN_XML_FILENAME).exists()){
            has = true;
        }
        
        return has;
    }
    
    @JsonIgnore
    public boolean hasSettingsXML(){
        boolean has = false;
        
        if(new File(getPath()+SETTINGS_XML_FILENAME).exists()){
            has = true;
        }
        
        return has;
    }
    
    @JsonIgnore
    public boolean hasVersionXML(){
        boolean has = false;
        
        if(new File(getPath()+VERSION_XML_FILENAME).exists()){
            has = true;
        }
        
        return has;
    }
    
    @JsonProperty("solutionPath")
    public String getPluginSolutionPath(){
        return getId()+File.separator;
    }
    
    @JsonProperty("systemPath")
    public String getPluginRelativePath(){
        return getPath().replace(PentahoSystem.getApplicationContext().getSolutionPath(""), "");
    }
    
    @JsonIgnore
    public String getPluginJson() throws IOException{
        ObjectMapper mapper = new ObjectMapper();
        return mapper.writeValueAsString(this);
    }
    
    @JsonIgnore
    public String getXmlValue(String xpathExpression, String filename){
        Node documentNode = getXmlFileContent(this.getPath()+filename);
        String value = null;
        
            try{
                value = documentNode.valueOf(xpathExpression);
            }catch(Exception ex){
                logger.error(ex);
            }
            
        return value;
    }
    
    public String getVersion(){
        return version.toString();
    }
}

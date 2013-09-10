/* This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this file,
 * You can obtain one at http://mozilla.org/MPL/2.0/. */
package pt.webdetails.cpf.plugins;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.dom4j.Document;
import org.dom4j.Node;
import org.pentaho.platform.util.xml.dom4j.XmlDom4JHelper;
import org.apache.commons.io.IOUtils;
import org.apache.commons.lang.NotImplementedException;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.codehaus.jackson.annotate.JsonIgnore;
import org.codehaus.jackson.annotate.JsonProperty;
import org.codehaus.jackson.map.ObjectMapper;
import org.pentaho.platform.engine.core.system.PentahoSystem;
import pt.webdetails.cpf.VersionChecker;
import pt.webdetails.cpf.plugin.CorePlugin;
import pt.webdetails.cpf.repository.api.IReadAccess;

/**
 *
 * @author Luis Paulo Silva
 * @deprecated: this is for a very specific use and should be moved to CPK
 */
public class Plugin extends CorePlugin{
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

    private IReadAccess pluginDirAccess;

    /**
     * @deprecated and will now fail miserably
     */
    public Plugin(String path){
//        super();
//        if(!path.endsWith("/"))
//        {
//            setPath(path+"/");
//        }else{
//            setPath(path);
//        }
//        pluginSelfBuild();
      throw new NotImplementedException();
    }

    public Plugin(String id, IReadAccess pluginSysDir) {
      super(id);
      pluginSelfBuild(pluginSysDir);
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
    private Node getXmlFileContent(IReadAccess access, String xmlFile){
        InputStream input = null;
        try {
            input = access.getFileInputStream(xmlFile);
            Document xml = XmlDom4JHelper.getDocFromStream(input);
            return xml.getRootElement();
        } catch (Exception ex) {
            Logger.getLogger(PluginsAnalyzer.class.getName()).log(Level.SEVERE, null, ex);
            return null;
        }
        finally {
          IOUtils.closeQuietly(input);
        }
    }
    
    @JsonIgnore
    private void pluginSelfBuild(IReadAccess access){

        if (hasPluginXML()) {
            Node documentNode = getXmlFileContent(access, PLUGIN_XML_FILENAME);
            setId(documentNode.valueOf("/plugin/@title"));
            setName(documentNode.valueOf("/plugin/content-types/content-type/title"));
            setDescription(documentNode.valueOf("/plugin/content-types/content-type/description"));
            setCompany(documentNode.valueOf("/plugin/content-types/content-type/company/@name"));
            setCompanyUrl(documentNode.valueOf("/plugin/content-types/content-type/company/@url"));
            setCompanyLogo(documentNode.valueOf("/plugin/content-types/content-type/company/@logo"));
        }

        if(hasVersionXML()){
            Document versionDoc = getXmlFileContent(access, VERSION_XML_FILENAME).getDocument();
            this.version = new VersionChecker.Version(versionDoc).toString();
        } else {
            String unspecified = "unspecified or no version.xml present in plugin directory";
            this.version = unspecified;
        }
    }
    
    @JsonIgnore
    public Node getRegisteredEntities(String entityName){
        if(hasSettingsXML()){
            Node documentNode = getXmlFileContent(pluginDirAccess, SETTINGS_XML_FILENAME);
            return documentNode.selectSingleNode("/settings"+entityName);
        }
        return null;
    }
    
    @JsonIgnore
    public boolean hasPluginXML(){
      return pluginDirAccess.fileExists(PLUGIN_XML_FILENAME);
    }
    
    @JsonIgnore
    public boolean hasSettingsXML(){
      return pluginDirAccess.fileExists(SETTINGS_XML_FILENAME);
    }
    
    @JsonIgnore
    public boolean hasVersionXML(){
      return pluginDirAccess.fileExists(VERSION_XML_FILENAME);
    }
    @Deprecated
    @JsonProperty("solutionPath")
    public String getPluginSolutionPath(){
        return getId() + File.separator;
    }

    @Deprecated
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
    public String getXmlValue(String xpathExpression, String fileName) {
        Node documentRoot = getXmlFileContent(pluginDirAccess, fileName);
        try {
            return documentRoot.valueOf(xpathExpression);
        } catch(Exception ex){
            logger.error(ex);
        }
        return null;
    }
    
    public String getVersion(){
        return version.toString();
    }
}
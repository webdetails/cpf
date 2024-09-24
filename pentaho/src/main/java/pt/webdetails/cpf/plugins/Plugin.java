/*!
* Copyright 2002 - 2018 Webdetails, a Hitachi Vantara company.  All rights reserved.
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

import java.io.IOException;
import java.util.Collections;
import java.util.List;

import org.dom4j.Document;
import org.dom4j.Element;
import org.dom4j.Node;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.ObjectMapper;
import pt.webdetails.cpf.VersionChecker;
import pt.webdetails.cpf.plugin.CorePlugin;
import pt.webdetails.cpf.repository.api.IReadAccess;
import pt.webdetails.cpf.utils.XmlDom4JUtils;

public class Plugin extends CorePlugin {

  private String description;
  private String company;
  private String companyUrl;
  private String companyLogo;
  private String path;
  private String version;
  private final String PLUGIN_XML_FILENAME = "plugin.xml";
  private final String SETTINGS_XML_FILENAME = "settings.xml";
  private final String VERSION_XML_FILENAME = "version.xml";
  protected Log logger = LogFactory.getLog( this.getClass() );

  private IReadAccess pluginDirAccess;

  /**
   * @param id Plugin ID (aka title)
   * @param pluginSysDir access to target plugin's system folder
   */
  public Plugin( String id, IReadAccess pluginSysDir ) {
    super( id );
    pluginSelfBuild( pluginSysDir );
  }

  /**
   * @return Returns the path to the plugin directory (system)
   */
  @JsonIgnore
  public String getPath() {
    return path;
  }

  public void setPath( String path ) {
    this.path = path;
  }

  /**
   * @return Returns the company name if defined on the Plugin.xml
   */
  @JsonProperty( "company" )
  public String getCompany() {
    return company;
  }

  public void setCompany( String company ) {
    this.company = company;
  }

  /**
   * @return Returns the company URL if defined on the Plugin.xml
   */
  @JsonProperty( "companyUrl" )
  public String getCompanyUrl() {
    return companyUrl;
  }

  public void setCompanyUrl( String companyUrl ) {
    this.companyUrl = companyUrl;
  }

  @JsonProperty( "companyLogo" )
  public String getCompanyLogo() {
    return companyLogo;
  }

  public void setCompanyLogo( String companyLogo ) {
    this.companyLogo = companyLogo;
  }

  /**
   * @return Returns the plugin description if defined on the Plugin.xml
   */
  @JsonProperty( "description" )
  public String getDescription() {
    return description;
  }

  public void setDescription( String description ) {
    this.description = description;
  }

  /**
   * @return Returns the plugin ID if defined on the Plugin.xml "<plugin title=<ID-HERE>...>"
   */
  @JsonProperty( "id" )
  public String getId() {
    return this.id;
  }

  @JsonProperty( "name" )
  public String getName() {
    return this.name;
  }

  @JsonIgnore
  private void pluginSelfBuild( IReadAccess access ) {

    this.pluginDirAccess = access;

    try {

      if ( hasPluginXML() ) {

        Node documentNode = XmlDom4JUtils.getDocumentFromFile( access, PLUGIN_XML_FILENAME ).getRootElement();
        //String pluginTitle = documentNode.valueOf("/plugin/@title");
        String pluginName = documentNode.valueOf( "/plugin/@name" );
        setName( pluginName );
        //setTitle( pluginTitle );
        //setName(documentNode.valueOf("/plugin/content-types/content-type/title"));
        setDescription( documentNode.valueOf( "/plugin/content-types/content-type/description" ) );
        setCompany( documentNode.valueOf( "/plugin/content-types/content-type/company/@name" ) );
        setCompanyUrl( documentNode.valueOf( "/plugin/content-types/content-type/company/@url" ) );
        setCompanyLogo( documentNode.valueOf( "/plugin/content-types/content-type/company/@logo" ) );
      }

      if ( hasVersionXML() ) {

        Document versionDoc = XmlDom4JUtils.getDocumentFromFile( access, VERSION_XML_FILENAME );
        this.version = new VersionChecker.Version( versionDoc ).toString();
      } else {
        String unspecified = "unspecified or no version.xml present in plugin directory";
        this.version = unspecified;
      }

    } catch ( IOException e ) {
      logger.error( e );
    }
  }

  /**
   * what's a registered entity?
   */
  @JsonIgnore
  public Node getRegisteredEntities( String entityName ) {
    if ( hasSettingsXML() ) {
      try {
        Node documentNode = XmlDom4JUtils.getDocumentFromFile( pluginDirAccess, SETTINGS_XML_FILENAME );
        return documentNode.selectSingleNode( "/settings" + entityName );
      } catch ( IOException e ) {
        logger.error( e );
      }
    }
    return null;
  }

  /**
   * @param xpath path from root settings node
   * @return list of matching nodes, empty if not found
   */
  @JsonIgnore //TODO: do we have to stick this in everything now?
  @SuppressWarnings( "unchecked" )
  public List<Element> getSettingsSection( String xpath ) {
    if ( hasSettingsXML() ) {
      try {
        Node documentNode = XmlDom4JUtils.getDocumentFromFile( pluginDirAccess, SETTINGS_XML_FILENAME );
        return (List<Element>) (List<?>) documentNode.selectNodes( "/settings" + xpath );
      } catch ( IOException e ) {
        logger.error( e );
      }
    }
    return Collections.<Element>emptyList();
  }

  @JsonIgnore
  public boolean hasPluginXML() {
    //TODO: by definition, a plugin should have this
    return pluginDirAccess.fileExists( PLUGIN_XML_FILENAME );
  }

  @JsonIgnore
  public boolean hasSettingsXML() {
    return pluginDirAccess.fileExists( SETTINGS_XML_FILENAME );
  }

  @JsonIgnore
  public boolean hasVersionXML() {
    return pluginDirAccess.fileExists( VERSION_XML_FILENAME );
  }

  @JsonIgnore
  public String getPluginJson() throws IOException {
    // TODO: is the convenience here worth all the JsonIgnores?
    ObjectMapper mapper = new ObjectMapper();
    return mapper.writeValueAsString( this );
  }

  @JsonIgnore
  public String getXmlValue( String xpathExpression, String fileName ) {
    try {
      Node documentRoot = XmlDom4JUtils.getDocumentFromFile( pluginDirAccess, fileName );
      return documentRoot != null ? documentRoot.valueOf( xpathExpression ) : null;
    } catch ( Exception ex ) {
      logger.error( ex );
    }
    return null;
  }

  public String getVersion() {
    return version.toString();
  }
}

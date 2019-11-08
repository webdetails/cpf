/*!
* Copyright 2002 - 2019 Webdetails, a Hitachi Vantara company.  All rights reserved.
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
package pt.webdetails.cpf;

import java.io.IOException;

import org.apache.commons.lang.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.dom4j.Node;

import pt.webdetails.cpf.repository.api.IContentAccessFactory;
import pt.webdetails.cpf.repository.api.IReadAccess;
import pt.webdetails.cpf.repository.api.IRWAccess;
import pt.webdetails.cpf.repository.pentaho.SystemPluginResourceAccess;
import pt.webdetails.cpf.utils.XmlDom4JUtils;


public abstract class PentahoBasePluginEnvironment extends PluginEnvironment implements IContentAccessFactory {

  private static String pluginId = null;
  private static Log logger = LogFactory.getLog( PentahoBasePluginEnvironment.class );

  public IContentAccessFactory getContentAccessFactory() {
    return this;
  }

  @Override
  public IReadAccess getPluginSystemReader( String basePath ) {
    return new SystemPluginResourceAccess( this.getClass().getClassLoader(), basePath );
  }

  @Override
  public IRWAccess getPluginSystemWriter( String basePath ) {
    return new SystemPluginResourceAccess( this.getClass().getClassLoader(), basePath );
  }


  public PluginSettings getPluginSettings() {
    return new PluginSettings( new SystemPluginResourceAccess( this.getClass().getClassLoader(), null ) );
  }

  @Override
  public IReadAccess getOtherPluginSystemReader( String pluginId, String basePath ) {
    return new SystemPluginResourceAccess( pluginId, basePath );
  }

  @Override
  public IRWAccess getOtherPluginSystemWriter( String pluginId, String basePath ) {
    return new SystemPluginResourceAccess( pluginId, basePath );
  }

  /**
   * @return Plugin's directory in repository, relative to root; defaults to plugin id if not overridden
   */
  protected String getPluginRepositoryDir() {
    return Util.joinPath( "/public", getPluginId() );
  }

  /**
   * @return The plugin's ID. This isn't efficient and should be overridden by plugin.
   */
  public String getPluginId() {
    if ( pluginId == null ) {
      try {
        // this depends on cpf being loaded by the plugin classloader
        final Node documentNode = getPluginXmlRootElement();

        final String name = documentNode.valueOf( "/plugin/@name" );
        final String title = documentNode.valueOf( "/plugin/@title" );
        synchronized ( PentahoBasePluginEnvironment.class ) {
          pluginId = StringUtils.isEmpty( name ) ? title : name;
        }
      } catch ( IOException e ) {
        logger.fatal( "Problem reading plugin.xml", e );

        return "cpf";
      }
    }

    return pluginId;
  }

  private Node getPluginXmlRootElement() throws IOException {
    final ClassLoader pluginClassloader = PentahoBasePluginEnvironment.class.getClassLoader();
    final IReadAccess reader = new SystemPluginResourceAccess( pluginClassloader, null );

    return XmlDom4JUtils.getDocumentFromFile( reader, "plugin.xml" ).getRootElement();
  }
}

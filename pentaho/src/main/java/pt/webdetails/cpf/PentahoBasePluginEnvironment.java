/*! ******************************************************************************
 *
 * Pentaho
 *
 * Copyright (C) 2024 by Hitachi Vantara, LLC : http://www.pentaho.com
 *
 * Use of this software is governed by the Business Source License included
 * in the LICENSE.TXT file.
 *
 * Change Date: 2028-08-13
 ******************************************************************************/
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

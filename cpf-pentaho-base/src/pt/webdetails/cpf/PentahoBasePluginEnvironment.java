package pt.webdetails.cpf;

import java.io.IOException;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.dom4j.Node;

import pt.webdetails.cpf.repository.api.IContentAccessFactory;
import pt.webdetails.cpf.repository.api.IReadAccess;
import pt.webdetails.cpf.repository.api.IRWAccess;
import pt.webdetails.cpf.repository.pentaho.SystemPluginResourceAccess;
import pt.webdetails.cpf.utils.XmlDom4JUtils;

//TODO: doesn't make much sense right now
public abstract class PentahoBasePluginEnvironment extends PluginEnvironment implements IContentAccessFactory {

  private static String pluginId = null;
  private static Log logger = LogFactory.getLog(PentahoPluginEnvironment.class);

  public IContentAccessFactory getContentAccessFactory() {
    return this;
  }

  @Override
  public IReadAccess getPluginSystemReader(String basePath) {
    return new SystemPluginResourceAccess(this.getClass().getClassLoader(), basePath);
  }

  @Override
  public IRWAccess getPluginSystemWriter(String basePath) {
    return new SystemPluginResourceAccess(this.getClass().getClassLoader(), basePath);
  }

  //TODO: this is temporary
  public PluginSettings getPluginSettings() {
    return new PluginSettings(new SystemPluginResourceAccess(this.getClass().getClassLoader(), null));
  }

  @Override
  public IReadAccess getOtherPluginSystemReader(String pluginId, String basePath) {
    return new SystemPluginResourceAccess(pluginId, basePath);
  }

  @Override
  public IRWAccess getOtherPluginSystemWriter(String pluginId, String basePath) {
    return new SystemPluginResourceAccess(pluginId, basePath);
  }
  /**
   * @return Plugin's directory in repository, relative to root; defaults to plugin id if not overridden
   */
  protected String getPluginRepositoryDir() {
    return Util.joinPath( "/public", getPluginId());
  }

  /**
   * @return The plugin's ID. This isn't efficient and should be overridden by plugin.
   */
  public String getPluginId() {
    if (pluginId == null) {
      try {
        // this depends on cpf being loaded by the plugin classloader
        IReadAccess reader = new SystemPluginResourceAccess(PentahoBasePluginEnvironment.class.getClassLoader(), null);
        Node documentNode = XmlDom4JUtils.getDocumentFromFile(reader, "plugin.xml").getRootElement();
        pluginId = documentNode.valueOf("/plugin/@title");
      } catch (IOException e) {
        logger.fatal("Problem reading plugin.xml", e);
        return "cpf";
      }
    }
    return pluginId;
  }



}

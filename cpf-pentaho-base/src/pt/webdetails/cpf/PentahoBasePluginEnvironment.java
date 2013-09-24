package pt.webdetails.cpf;

import pt.webdetails.cpf.repository.api.IContentAccessFactory;
import pt.webdetails.cpf.repository.api.IReadAccess;
import pt.webdetails.cpf.repository.api.IRWAccess;
import pt.webdetails.cpf.repository.pentaho.SystemPluginResourceAccess;

//TODO: doesn't make much sense right now
public abstract class PentahoBasePluginEnvironment extends PluginEnvironment implements IContentAccessFactory {

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
}

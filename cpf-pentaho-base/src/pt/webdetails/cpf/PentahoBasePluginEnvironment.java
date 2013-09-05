package pt.webdetails.cpf;

import pt.webdetails.cpf.repository.api.IReadAccess;
import pt.webdetails.cpf.repository.api.IRepositoryAccessFactory;
import pt.webdetails.cpf.repository.pentaho.SystemPluginResourceAccess;

//TODO: doesn't make much sense right now
public abstract class PentahoBasePluginEnvironment extends PluginEnvironment implements IRepositoryAccessFactory {

  public IRepositoryAccessFactory getRepositoryFactory() {
    return this;
  }

  public IReadAccess getPluginResourceAccess(String basePath) {
    return new SystemPluginResourceAccess(this.getClass().getClassLoader(), null);
  }

  //TODO: this is temporary
  public PluginSettings getPluginSettings() {
    return new PluginSettings(new SystemPluginResourceAccess(this.getClass().getClassLoader(), null));
  }
}

package pt.webdetails.cpf;

import pt.webdetails.cpf.repository.api.IReadAccess;
import pt.webdetails.cpf.repository.api.IUserContentAccess;
import pt.webdetails.cpf.repository.pentaho.unified.PluginRepositoryResourceAccess;
import pt.webdetails.cpf.repository.pentaho.unified.UserContentRepositoryAccess;

//TODO: friggin long names, instanciation; there must be another singleton
public class PentahoPluginEnvironment extends PentahoBasePluginEnvironment {

  private static PentahoPluginEnvironment instance = new PentahoPluginEnvironment();

  private PentahoPluginEnvironment() {}
//
  public static PentahoPluginEnvironment getInstance() {
    return instance;
  }

  //IRepositoryAccessFactory
  public IUserContentAccess getUserContentAccess(String basePath) {
    return new UserContentRepositoryAccess(null);
  }

  public IReadAccess getPluginResourceAccess() {
    return getPluginRepositoryResourceAccess(null);
  }

  //IRepositoryAccessFactory
  public IReadAccess getPluginRepositoryResourceAccess(String basePath) {
    return new PluginRepositoryResourceAccess(basePath);
  }


}

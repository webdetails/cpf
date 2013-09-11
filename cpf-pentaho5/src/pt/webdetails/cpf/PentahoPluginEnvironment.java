package pt.webdetails.cpf;

import pt.webdetails.cpf.repository.api.IRWAccess;
import pt.webdetails.cpf.repository.api.IReadAccess;
import pt.webdetails.cpf.repository.api.IUserContentAccess;
import pt.webdetails.cpf.repository.pentaho.unified.PluginRepositoryResourceAccess;
import pt.webdetails.cpf.repository.pentaho.unified.UserContentRepositoryAccess;

//TODO: there must be another singleton
public class PentahoPluginEnvironment extends PentahoBasePluginEnvironment {

  private static PentahoPluginEnvironment instance = new PentahoPluginEnvironment();

  private PentahoPluginEnvironment() {}
//
  public static PentahoPluginEnvironment getInstance() {
    return instance;
  }

  @Override
  public IUserContentAccess getUserContentAccess(String basePath) {
    return new UserContentRepositoryAccess(null);
  }

  @Override
  public IReadAccess getPluginRepositoryReader(String basePath) {
    return new PluginRepositoryResourceAccess(basePath);
  }

  @Override
  public IRWAccess getPluginRepositoryWriter(String basePath) {
    return new PluginRepositoryResourceAccess(basePath);
  }

}

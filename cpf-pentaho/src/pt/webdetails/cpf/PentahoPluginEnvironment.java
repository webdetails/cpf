package pt.webdetails.cpf;

import pt.webdetails.cpf.repository.api.IReadAccess;
import pt.webdetails.cpf.repository.api.IRepositoryAccessFactory;
import pt.webdetails.cpf.repository.api.IUserContentAccess;
import pt.webdetails.cpf.repository.pentaho.PentahoLegacyUserContentAccess;
import pt.webdetails.cpf.repository.pentaho.PluginLegacySolutionResourceAccess;

public class PentahoPluginEnvironment extends PentahoBasePluginEnvironment implements IRepositoryAccessFactory {

  @Override
  public IUserContentAccess getUserContentAccess(String basePath) {
    return new PentahoLegacyUserContentAccess(basePath, null);
  }

  @Override
  public IReadAccess getPluginRepositoryResourceAccess(String basePath) {
    return new PluginLegacySolutionResourceAccess(basePath);
  }

  public static PentahoPluginEnvironment getInstance() {
    return new PentahoPluginEnvironment();
  }
}

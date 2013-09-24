package pt.webdetails.cpf;

import org.pentaho.platform.engine.core.system.PentahoSessionHolder;

import pt.webdetails.cpf.repository.api.IRWAccess;
import pt.webdetails.cpf.repository.api.IReadAccess;
import pt.webdetails.cpf.repository.api.IContentAccessFactory;
import pt.webdetails.cpf.repository.api.IUserContentAccess;
import pt.webdetails.cpf.repository.pentaho.PentahoLegacyUserContentAccess;
import pt.webdetails.cpf.repository.pentaho.PluginLegacySolutionResourceAccess;
import pt.webdetails.cpf.repository.util.RepositoryHelper;

public class PentahoPluginEnvironment extends PentahoBasePluginEnvironment implements IContentAccessFactory {

  public static PentahoPluginEnvironment getInstance() {
    return new PentahoPluginEnvironment();
  }


  @Override
  public IUserContentAccess getUserContentAccess(String basePath) {
    return new PentahoLegacyUserContentAccess(basePath, PentahoSessionHolder.getSession());
  }

  @Override
  public IReadAccess getPluginRepositoryReader(String basePath) {
    basePath = RepositoryHelper.appendPath(getPluginRepositoryDir(), basePath);
    return new PluginLegacySolutionResourceAccess(basePath);
  }


  @Override
  public IRWAccess getPluginRepositoryWriter(String basePath) {
    basePath = RepositoryHelper.appendPath(getPluginRepositoryDir(), basePath);
    return new PluginLegacySolutionResourceAccess(basePath);
  }

  

}

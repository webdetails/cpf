package pt.webdetails.cpf;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import pt.webdetails.cpf.repository.api.IRWAccess;
import pt.webdetails.cpf.repository.api.IReadAccess;
import pt.webdetails.cpf.repository.api.IUserContentAccess;
import pt.webdetails.cpf.repository.pentaho.unified.PluginRepositoryResourceAccess;
import pt.webdetails.cpf.repository.pentaho.unified.UserContentRepositoryAccess;
import pt.webdetails.cpf.repository.util.RepositoryHelper;

//TODO: there must be another singleton
public class PentahoPluginEnvironment extends PentahoBasePluginEnvironment {

  private static PentahoPluginEnvironment instance = new PentahoPluginEnvironment();
  private static Log logger = LogFactory.getLog(PentahoPluginEnvironment.class);

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
    basePath = RepositoryHelper.appendPath(getPluginRepositoryDir(), basePath);
    return new PluginRepositoryResourceAccess(basePath);
  }

  @Override
  public IRWAccess getPluginRepositoryWriter(String basePath) {
    basePath = RepositoryHelper.appendPath(getPluginRepositoryDir(), basePath);
    return new PluginRepositoryResourceAccess(basePath);
  }

}

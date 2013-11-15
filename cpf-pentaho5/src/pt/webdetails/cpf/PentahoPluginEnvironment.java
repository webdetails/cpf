package pt.webdetails.cpf;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.pentaho.platform.engine.core.system.PentahoRequestContextHolder;
import org.pentaho.platform.engine.core.system.PentahoSessionHolder;

import pt.webdetails.cpf.api.IContentAccessFactoryExtended;
import pt.webdetails.cpf.api.IUserContentAccessExtended;
import pt.webdetails.cpf.context.api.IUrlProvider;
import pt.webdetails.cpf.messaging.BeanyPluginCall;
import pt.webdetails.cpf.plugincall.api.IPluginCall;
import pt.webdetails.cpf.repository.api.IRWAccess;
import pt.webdetails.cpf.repository.api.IReadAccess;
import pt.webdetails.cpf.repository.api.IUserContentAccess;
import pt.webdetails.cpf.repository.pentaho.unified.PluginRepositoryResourceAccess;
import pt.webdetails.cpf.repository.pentaho.unified.UserContentRepositoryAccess;
import pt.webdetails.cpf.repository.util.RepositoryHelper;

//TODO: there must be another singleton
public class PentahoPluginEnvironment extends PentahoBasePluginEnvironment implements IContentAccessFactoryExtended {

  private static PentahoPluginEnvironment instance = new PentahoPluginEnvironment();
  private static Log logger = LogFactory.getLog(PentahoPluginEnvironment.class);

  protected PentahoPluginEnvironment() {}

  public static PentahoPluginEnvironment getInstance() {
    return instance;
  }

  public IContentAccessFactoryExtended getContentAccessFactory() {
	return this;
  }
  
  @Override
  public IUserContentAccessExtended getUserContentAccess(String basePath) {
    return new UserContentRepositoryAccess( PentahoSessionHolder.getSession(), basePath);
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


  @Override
  public IUrlProvider getUrlProvider() {
    return new IUrlProvider() {

      @Override
      public String getPluginBaseUrl( String pluginId ) {
        return Util.joinPath( getWebappContextPath(), "plugin", pluginId, "api" ) + "/";
      }

      @Override
      public String getPluginBaseUrl() {
        return getPluginBaseUrl( getPluginId() );
      }

      @Override
      public String getPluginStaticBaseUrl( String pluginId ) {
        return Util.joinPath( getWebappContextPath(), "api/repos/", pluginId ) + "/";
      }

      @Override
      public String getPluginStaticBaseUrl() {
        return getPluginStaticBaseUrl( getPluginId() );
      }

      @Override
      public String getRepositoryUrl(String fullPath) {
        String colonPath = fullPath.replaceAll( "/", ":" );
        return Util.joinPath( getWebappContextPath(), "/api/repos/", colonPath, "/content" );
      }

      private String getWebappContextPath() { //TODO: better alternative
        return PentahoRequestContextHolder.getRequestContext().getContextPath();
      }
    };
  }

  public IPluginCall getPluginCall( String pluginId, String servicePath, String method ) {
    return new InterPluginCall( new InterPluginCall.Plugin(pluginId), servicePath, method );
  }

}

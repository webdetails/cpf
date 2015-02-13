package pt.webdetails.cpf;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.pentaho.platform.engine.core.system.PentahoSystem;
import org.pentaho.platform.engine.core.system.PentahoSessionHolder;
import org.pentaho.platform.engine.core.system.PentahoRequestContextHolder;

import pt.webdetails.cpf.api.IContentAccessFactoryExtended;
import pt.webdetails.cpf.api.IUserContentAccessExtended;
import pt.webdetails.cpf.context.api.IUrlProvider;
import pt.webdetails.cpf.plugincall.api.IPluginCall;
import pt.webdetails.cpf.repository.api.IRWAccess;
import pt.webdetails.cpf.repository.api.IReadAccess;
import pt.webdetails.cpf.repository.pentaho.unified.PluginRepositoryResourceAccess;
import pt.webdetails.cpf.repository.pentaho.unified.UserContentRepositoryAccess;
import pt.webdetails.cpf.repository.util.RepositoryHelper;

//TODO: there must be another singleton
public class PentahoPluginEnvironment extends PentahoBasePluginEnvironment implements IContentAccessFactoryExtended {

  private static PentahoPluginEnvironment instance = new PentahoPluginEnvironment();
  private IUrlProvider pentahoUrlProvider;

  static {
    PluginEnvironment.init( instance );
  }

  private static Log logger = LogFactory.getLog( PentahoPluginEnvironment.class );

  static {
    PluginEnvironment.init( instance );
  }

  protected PentahoPluginEnvironment() {
  }

  public static PentahoPluginEnvironment getInstance() {
    return instance;
  }

  public IContentAccessFactoryExtended getContentAccessFactory() {
    return this;
  }

  @Override
  public IUserContentAccessExtended getUserContentAccess( String basePath ) {
    return new UserContentRepositoryAccess( PentahoSessionHolder.getSession(), basePath );
  }

  @Override
  public IReadAccess getPluginRepositoryReader( String basePath ) {
    basePath = RepositoryHelper.appendPath( getPluginRepositoryDir(), basePath );
    return new PluginRepositoryResourceAccess( basePath );
  }

  @Override
  public IRWAccess getPluginRepositoryWriter( String basePath ) {
    basePath = RepositoryHelper.appendPath( getPluginRepositoryDir(), basePath );
    return new PluginRepositoryResourceAccess( basePath );
  }

  @Override
  public IUrlProvider getUrlProvider() {
    if( pentahoUrlProvider == null ) {
      pentahoUrlProvider = new PentahoUrlProvider( getPluginId() );
    }
    return pentahoUrlProvider;
  }

  public IPluginCall getPluginCall( String pluginId, String servicePath, String method ) {
    return new InterPluginCall( new InterPluginCall.Plugin( pluginId ), servicePath, method );
  }

  @Override
  protected boolean systemWriteAuthorized() {
    return super.systemWriteAuthorized();
  }
}

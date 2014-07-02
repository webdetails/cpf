package pt.webdetails.cpf;

import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;

import org.pentaho.platform.engine.core.system.PentahoSessionHolder;

import pt.webdetails.cpf.context.api.IUrlProvider;
import pt.webdetails.cpf.plugin.CorePlugin;
import pt.webdetails.cpf.plugincall.api.IPluginCall;
import pt.webdetails.cpf.repository.api.IRWAccess;
import pt.webdetails.cpf.repository.api.IReadAccess;
import pt.webdetails.cpf.repository.api.IContentAccessFactory;
import pt.webdetails.cpf.repository.api.IUserContentAccess;
import pt.webdetails.cpf.repository.pentaho.PentahoLegacyUserContentAccess;
import pt.webdetails.cpf.repository.pentaho.PluginLegacySolutionResourceAccess;
import pt.webdetails.cpf.repository.util.RepositoryHelper;

public class PentahoPluginEnvironment extends PentahoBasePluginEnvironment implements IContentAccessFactory {

  private static final PentahoPluginEnvironment instance = new PentahoPluginEnvironment();
  private IUrlProvider pentahoUrlProvider;

  static {
    PluginEnvironment.init( instance );
  }

  public static PentahoPluginEnvironment getInstance() {
    return instance;
  }

  @Override
  public IUserContentAccess getUserContentAccess( String basePath ) {
    return new PentahoLegacyUserContentAccess( basePath, PentahoSessionHolder.getSession() );
  }

  @Override
  public IReadAccess getPluginRepositoryReader( String basePath ) {
    basePath = RepositoryHelper.appendPath( getPluginRepositoryDir(), basePath );
    return new PluginLegacySolutionResourceAccess( basePath );
  }

  @Override
  public IRWAccess getPluginRepositoryWriter( String basePath ) {
    basePath = RepositoryHelper.appendPath( getPluginRepositoryDir(), basePath );
    return new PluginLegacySolutionResourceAccess( basePath );
  }

  @Override
  public IUrlProvider getUrlProvider() {
    if( pentahoUrlProvider == null ) {
      pentahoUrlProvider = new PentahoUrlProvider( getPluginId() );
    }
    return pentahoUrlProvider;
  }

  //FIXME
  public IPluginCall getPluginCall( String pluginId, String servicePath, String method ) {
    final PentahoInterPluginCall theRealCall = new PentahoInterPluginCall( new CorePlugin( pluginId ), method );
    return new IPluginCall() {

      public void run( Map<String, String[]> params ) throws Exception {
        theRealCall.setRequestParameters( convertParams( params ) );
        theRealCall.run();
      }

      public InputStream getResult() {
        return null;
      }

      public boolean exists() {
        return false;
      }

      @Override
      public String call( Map<String, String[]> params ) throws Exception {
        theRealCall.setRequestParameters( convertParams( params ) );
        return theRealCall.call();
      }

      private Map<String, Object> convertParams( Map<String, String[]> params ) {
        HashMap<String, Object> map = new HashMap<String, Object>();
        for ( String key : params.keySet() ) {
          String[] value = params.get( key );
          if ( value != null ) {
            if ( value.length == 1 ) {
              map.put( key, value[ 0 ] );
            } else {
              map.put( key, value );
            }
          }
        }
        return map;
      }
    };
  }


}

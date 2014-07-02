package pt.webdetails.cpf;

import org.pentaho.platform.engine.core.system.PentahoRequestContextHolder;
import org.pentaho.platform.engine.core.system.PentahoSystem;
import pt.webdetails.cpf.context.api.IUrlProvider;

public class PentahoUrlProvider implements IUrlProvider {

  private static final String CONTENT = "content";
  private String pluginId;

  public PentahoUrlProvider( String id ){
    pluginId = id;
  }

  @Override
  public String getPluginStaticBaseUrl() {
    return getPluginStaticBaseUrl( pluginId );
  }

  @Override
  public String getPluginStaticBaseUrl( String pluginId ) {
    return Util.joinPath( getApplicationBaseUrl(), CONTENT, pluginId ) + "/";
  }

  @Override
  public String getPluginBaseUrl() {
    return getPluginBaseUrl( pluginId );
  }

  @Override
  public String getPluginBaseUrl( String pluginId ) {
    return Util.joinPath( getApplicationBaseUrl(), CONTENT, pluginId ) + "/";
  }

  @SuppressWarnings( "deprecation" )
  protected String getApplicationBaseUrl() {
    return PentahoSystem.getApplicationContext().getBaseUrl();
  }

  @Override
  public String getRepositoryUrl( String fullPath ) {
    return Util.joinPath( getApplicationBaseUrl(), CONTENT, pluginId, "res", fullPath );
  }

  @Override
  public String getWebappContextPath() {
    return PentahoRequestContextHolder.getRequestContext().getContextPath();
  }

  @Override
  public String getWebappContextRoot() {
    String url = PentahoSystem.getApplicationContext().getFullyQualifiedServerURL(),
      webappName = getWebappContextPath();

    return url.substring( 0, url.length() - webappName.length() + 1 );
  }

  @Override
  public String getResourcesBasePath() {
    return "";
  }
}

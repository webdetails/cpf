package pt.webdetails.cpf;

import org.pentaho.platform.engine.core.system.PentahoRequestContextHolder;
import org.pentaho.platform.engine.core.system.PentahoSystem;
import pt.webdetails.cpf.context.api.IUrlProvider;

public class PentahoUrlProvider implements IUrlProvider {
  private static final String REPOS = "api/repos/";
  private String pluginId;

  public PentahoUrlProvider( String id ){
    pluginId = id;
  }

  @Override
  public String getPluginBaseUrl( String pluginId ) {
    return Util.joinPath( getWebappContextPath(), "plugin", pluginId, "api" ) + "/";
  }

  @Override
  public String getPluginBaseUrl() {
    return getPluginBaseUrl( pluginId );
  }

  @Override
  public String getPluginStaticBaseUrl( String pluginId ) {
    return Util.joinPath( getWebappContextPath(), REPOS, pluginId ) + "/";
  }

  @Override
  public String getPluginStaticBaseUrl() {
    return getPluginStaticBaseUrl( pluginId );
  }

  @Override
  public String getRepositoryUrl( String fullPath ) {
    String colonPath = fullPath.replaceAll( "/", ":" );
    return Util.joinPath( getWebappContextPath(), REPOS, colonPath, "/content" );
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

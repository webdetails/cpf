package pt.webdetails.cpf.context.api;

/**
 * Provides base url paths needed for calls and file access.<br>
 */
public interface IUrlProvider {

  /**
   * Get the absolute URL path to plugin's request handlers.<br> 
   * <br>Ex.:<br>
   * <ul>
   * <li>Pentaho 4.x: "/pentaho/content/&lt;plugin&gt;/"</li>
   * <li>Pentaho 5.x: "/pentaho/plugin/&lt;plugin&gt;/api/"</li>
   * </ul>
   * @param pluginId the plugin id/title
   * @return the absolute url path starting and ending with slash.
   */
  String getPluginBaseUrl( String pluginId );

  /**
   * {@link #getPluginBaseUrl(String)} for current plugin.
   */
  String getPluginBaseUrl();

  /**
   * Get the absolute URL path to plugin's static paths.<br> 
   * <br>Ex.:<br>
   * <ul>
   * <li>Pentaho 4.x: "/pentaho/content/&lt;plugin&gt;/"</li>
   * <li>Pentaho 5.x: "/pentaho/api/plugins/&lt;plugin&gt;/files/"</li>
   * </ul>
   * @param pluginId the plugin id/title
   * @return the absolute url path starting and ending with slash.
   */
  String getPluginStaticBaseUrl( String pluginId );

  /**
   * {@link #getPluginStaticBaseUrl(String)} for current plugin.
   */
  String getPluginStaticBaseUrl();

  /**
   * The absolute URL path for the repository path
   *
   * @param fullPath an absolute solution path
   * @return the absolute url path starting and ending with slash.
   */
  String getRepositoryUrl( String fullPath ); //TODO: have others receive path as well?

  /**
   * @return the webapp name
   */
  String getWebappContextPath();

  /**
   * The server root. It removes the webapp name
   *
   * @return the webapp root
   */
  String getWebappContextRoot();

  /**
   * The endpoint provided by the plugin to get resources
   *
   * @return endpoint url to get resources
   */
  String getResourcesBasePath();

}

package pt.webdetails.cpf.repository.pentaho;

import pt.webdetails.cpf.repository.api.IRWAccess;

public class SystemPluginRWAccess extends SystemPluginResourceAccess implements IRWAccess {

  public SystemPluginRWAccess(ClassLoader classLoader, String basePath) {
    super(classLoader, basePath);
  }

  public SystemPluginRWAccess(String pluginId, String basePath) {
    super(pluginId, basePath);
  }
  
}

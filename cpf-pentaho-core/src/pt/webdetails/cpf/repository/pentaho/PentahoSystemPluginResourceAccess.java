package pt.webdetails.cpf.repository.pentaho;

import java.io.File;
import java.net.URL;

import org.apache.commons.lang.StringUtils;
import org.pentaho.platform.api.engine.IPluginManager;
import org.pentaho.platform.engine.core.system.PentahoSystem;
import org.pentaho.platform.plugin.services.pluginmgr.PluginClassLoader;

import pt.webdetails.cpf.repository.api.IPluginResourceAccess;
import pt.webdetails.cpf.repository.impl.FileBasedResourceAccess;
import pt.webdetails.cpf.repository.util.RepositoryHelper;

/**
 * Implementation of {@link IPluginResourceAccess} for directories under system.
 */
public class PentahoSystemPluginResourceAccess extends FileBasedResourceAccess implements IPluginResourceAccess {

  protected File basePath;

  public PentahoSystemPluginResourceAccess(ClassLoader classLoader, String basePath) {
    initPathFromClassLoader(classLoader, basePath);
  }

  public PentahoSystemPluginResourceAccess(String pluginId, String basePath) {
    IPluginManager pm = PentahoSystem.get(IPluginManager.class);
    ClassLoader classLoader = pm.getClassLoader(pluginId);
    initPathFromClassLoader(classLoader, basePath);
  }

  private void initPathFromClassLoader(ClassLoader classLoader, String basePath) {
    if (classLoader == null) {
      throw new IllegalArgumentException("Unknown plugin");
    }
    if (classLoader instanceof PluginClassLoader) {
      this.basePath = ((PluginClassLoader) classLoader).getPluginDir();
    }
    else {//shouldn't get here, but..
      URL rootFileUrl = RepositoryHelper.getClosestResource(classLoader, "plugin.xml");
      if (rootFileUrl != null) {
        this.basePath = new File(rootFileUrl.getPath()).getParentFile();
      }
    }
    if (this.basePath == null) {
      throw new IllegalArgumentException("Couldn't find a valid base path from class loader");
    }

    if (!StringUtils.isEmpty(basePath)) {
      this.basePath = new File(this.basePath, basePath);
    }
  }

  @Override
  public File getFile(String path) {
    return new File(basePath, path);
  }

}

package pt.webdetails.cpf;

import pt.webdetails.cpf.repository.api.IRepositoryAccessFactory;

/**
 * Intended as an all-purpose factory singleton for plugin interaction with its environment (repository, session, config..)<br>
 * Should be extended by plugins.
 */
public abstract class PluginEnvironment {

  /**
   * @return factory for accessing repository
   */
  public abstract IRepositoryAccessFactory getRepositoryFactory();
  /**
   * Should be overridden per plugin
   * @return
   */
  public abstract PluginSettings getPluginSettings();
//    return new PluginSettings(getRepositoryFactory().getPluginResourceRWAccess(null));
//  }

}

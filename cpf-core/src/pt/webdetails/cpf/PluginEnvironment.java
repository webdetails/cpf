package pt.webdetails.cpf;

import pt.webdetails.cpf.repository.api.IRepositoryAccessFactory;

/**
 * Intended as an all-purpose factory singleton for plugin interaction with its environment (repository, session, config..)<br>
 * Should be extended by plugins.
 */
public abstract class PluginEnvironment {

  private static PluginEnvironment env;

  /**
   * Use with care
   * @param defaultEnvironment what will be the environment in use for the static methods.
   */
  public static synchronized void init(PluginEnvironment defaultEnvironment) {
    //TODO: yeah, on the ugly side, but not ready to be springified yet.
    // could be an env = (PluginEnvironment) beanFactory.getBean(PluginEnvironment) or something
    env = defaultEnvironment;
  }

  public static PluginEnvironment env() {
    return env;
  }
  public static IRepositoryAccessFactory repository() {
    return env().getRepositoryFactory();
  }

  /**
   * @return factory for accessing repository
   */
  public abstract IRepositoryAccessFactory getRepositoryFactory();


  
  /**
   * Should be overridden per plugin TODO: something else here
   * @return
   */
  public abstract PluginSettings getPluginSettings();



}

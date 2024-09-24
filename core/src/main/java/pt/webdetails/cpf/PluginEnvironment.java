/*!
 * Copyright 2002 - 2019 Webdetails, a Hitachi Vantara company.  All rights reserved.
 *
 * This software was developed by Webdetails and is provided under the terms
 * of the Mozilla Public License, Version 2.0, or any later version. You may not use
 * this file except in compliance with the license. If you need a copy of the license,
 * please go to  http://mozilla.org/MPL/2.0/. The Initial Developer is Webdetails.
 *
 * Software distributed under the Mozilla Public License is distributed on an "AS IS"
 * basis, WITHOUT WARRANTY OF ANY KIND, either express or  implied. Please refer to
 * the license for the specific language governing your rights and limitations.
 */
package pt.webdetails.cpf;

import pt.webdetails.cpf.context.api.IUrlProvider;
import pt.webdetails.cpf.plugincall.api.IPluginCall;
import pt.webdetails.cpf.repository.api.IContentAccessFactory;

/**
 * Intended as an all-purpose factory singleton for plugin interaction with its environment (repository, session,
 * config..)<br> Should be extended by plugins.
 */
public abstract class PluginEnvironment {

  private static PluginEnvironment env;

  /**
   * Use with care
   *
   * @param defaultEnvironment what will be the environment in use for the static methods.
   */
  public static synchronized void init( PluginEnvironment defaultEnvironment ) {
    //TODO: yeah, on the ugly side, but not ready to be springified yet.
    // could be an env = (PluginEnvironment) beanFactory.getBean(PluginEnvironment) or something
    env = defaultEnvironment;
  }

  public static PluginEnvironment env() {
    return env;
  }

  public static IContentAccessFactory repository() {
    return env().getContentAccessFactory();
  }

  /**
   * @return factory for accessing repository
   */
  public abstract IContentAccessFactory getContentAccessFactory();


  /**
   * For getting base context-specific URLs
   */
  public abstract IUrlProvider getUrlProvider();

  /**
   * Should be overridden per plugin TODO: something else here
   *
   * @return
   */
  public abstract PluginSettings getPluginSettings();


  public abstract String getPluginId();

  /**
   * Get an inter-plugin call
   *
   * @param pluginId plugin to be invoked
   * @param service  service path to plugin, not used in legacy calls
   * @param method   method being invoked
   * @return
   */
  public abstract IPluginCall getPluginCall( String pluginId, String service, String method );
}

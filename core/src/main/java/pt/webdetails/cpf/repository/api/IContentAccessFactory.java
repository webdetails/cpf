/*!
 * Copyright 2002 - 2017 Webdetails, a Hitachi Vantara company.  All rights reserved.
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
package pt.webdetails.cpf.repository.api;

/**
 * Minimal repository access provider for basic plugin needs
 */
public interface IContentAccessFactory {

  /**
   * @param basePath (optional) all subsequent paths will be relative to this
   * @return {@link IUserContentAccess} for user repository access
   */
  IUserContentAccess getUserContentAccess( String basePath );

  /**
   * @param basePath (optional) base path relative to plugin's folder. all subsequent paths will be relative to it
   * @return {@link IReadAccess} for files in plugin's own folder
   */
  IReadAccess getPluginRepositoryReader( String basePath );

  /**
   * @param basePath (optional) base path relative to plugin's folder. all subsequent paths will be relative to it
   * @return {@link IRWAccess} for files in plugin's own folder
   */
  IRWAccess getPluginRepositoryWriter( String basePath );

  /**
   * @param basePath (optional) base path relative to plugin's folder. all subsequent paths will be relative to it
   * @return {@link IReadAccess} for files in plugin's own folder
   */
  IReadAccess getPluginSystemReader( String basePath );


  /**
   * @param basePath (optional) base path relative to plugin's folder. all subsequent paths will be relative to it
   * @return {@link IRWAccess} for files in plugin's own folder
   */
  IRWAccess getPluginSystemWriter( String basePath );


  /**
   * For snooping other plugins' stuff. Because IInterPlugin just seemed weird.
   *
   * @param pluginId id of the plugin, aka title for some reason
   * @param basePath (optional) base path relative to plugin's repository folder. all subsequent paths will be relative
   *                 to it
   * @return like {@link #getPluginResourceAccess(String)} of another plugin
   */
  IReadAccess getOtherPluginSystemReader( String pluginId, String basePath );


  /**
   * For snooping other plugins' stuff. Because IInterPlugin just seemed weird.
   *
   * @param pluginId id of the plugin, aka title for some reason
   * @param basePath (optional) base path relative to plugin's repository folder. all subsequent paths will be relative
   *                 to it
   * @return like {@link #getPluginResourceAccess(String)} of another plugin
   */
  IRWAccess getOtherPluginSystemWriter( String pluginId, String basePath );

  //  /**
  //   * @param basePath (optional) base path relative to plugin's repository folder.
  //   *                 all subsequent paths will be relative to it
  //   * @return {@link IPluginResourceAccess} for files in plugin's own folder
  //   */
  //  public IPluginResourceRWAccess getPluginResourceRWAccess(String basePath);
  //  //TODO: a legacy PluginResource/Solution accessor that checks the path for system?..
  //  // and multiplexes to the appropriate class; maybe deprecate it at birth

  //  /**
  //   * Multiplexes legacy paths to appropriate resource access
  //   * @param basePath
  //   * @return
  //   */
  //  IPluginResourceAccess getLegacyResourceAccess(String basePath);
  //
  //  /**
  //   * base path = system/plugin
  //   * @return {@link IPluginResourceAccess} for current plugin.
  //   */
  //  IPluginResourceAccess getPluginResourceAccess();
  //
  //  /**
  //   * base path = system/plugin
  //   * @return {@link IPluginResourceAccess} for specified plugin.
  //   */
  //  IPluginResourceAccess getPluginResourceAccess(String plugin);
  //
  //  //TODO: friggin long name
  //  /**
  //   * @param path relative to repository plugin folder.
  //   * @return {@link IPluginResourceAccess} for solution
  //   */
  //  IPluginResourceAccess getPluginRepositoryResourceAccess(String path);
}

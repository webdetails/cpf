/*!
 * Copyright 2018 Webdetails, a Hitachi Vantara company. All rights reserved.
 *
 * This software was developed by Webdetails and is provided under the terms
 * of the Mozilla Public License, Version 2.0, or any later version. You may not use
 * this file except in compliance with the license. If you need a copy of the license,
 * please go to http://mozilla.org/MPL/2.0/. The Initial Developer is Webdetails.
 *
 * Software distributed under the Mozilla Public License is distributed on an "AS IS"
 * basis, WITHOUT WARRANTY OF ANY KIND, either express or implied. Please refer to
 * the license for the specific language governing your rights and limitations.
 */

package pt.webdetails.cpf.repository.api;

import pt.webdetails.cpf.api.IUserContentAccessExtended;

/**
 * Minimal repository access provider for repository plugin needs.
 */
public interface IRepositoryAccessFactory {
  /**
   * @param basePath (optional) all subsequent paths will be relative to this
   * @return {@link IUserContentAccessExtended} for user repository access
   */
  IUserContentAccessExtended getUserContentAccess( String basePath );

  /**
   * @param pluginRepositoryDir plugin path to plugin's folder
   * @param basePath (optional) base path relative to plugin's folder. all subsequent paths will be relative to it
   * @return {@link IReadAccess} for files in plugin's own folder
   */
  IReadAccess getPluginRepositoryReader( String pluginRepositoryDir, String basePath );

  /**
   * @param pluginRepositoryDir plugin path to plugin's folder
   * @param basePath (optional) base path relative to plugin's folder. all subsequent paths will be relative to it
   * @return {@link IRWAccess} for files in plugin's own folder
   */
  IRWAccess getPluginRepositoryWriter( String pluginRepositoryDir, String basePath );

}

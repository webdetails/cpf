/*! ******************************************************************************
 *
 * Pentaho
 *
 * Copyright (C) 2024 by Hitachi Vantara, LLC : http://www.pentaho.com
 *
 * Use of this software is governed by the Business Source License included
 * in the LICENSE.TXT file.
 *
 * Change Date: 2029-07-20
 ******************************************************************************/


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

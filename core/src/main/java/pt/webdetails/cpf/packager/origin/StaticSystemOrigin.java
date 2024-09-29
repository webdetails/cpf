/*! ******************************************************************************
 *
 * Pentaho
 *
 * Copyright (C) 2024 by Hitachi Vantara, LLC : http://www.pentaho.com
 *
 * Use of this software is governed by the Business Source License included
 * in the LICENSE.TXT file.
 *
 * Change Date: 2028-08-13
 ******************************************************************************/

package pt.webdetails.cpf.packager.origin;

import pt.webdetails.cpf.context.api.IUrlProvider;
import pt.webdetails.cpf.repository.api.IContentAccessFactory;
import pt.webdetails.cpf.repository.api.IReadAccess;
import pt.webdetails.cpf.repository.util.RepositoryHelper;

/**
 * For keeping track of paths from static plugin system folders.
 *
 * @see PathOrigin
 */
public class StaticSystemOrigin extends PathOrigin {

  public StaticSystemOrigin( String basePath ) {
    super( basePath );
  }

  public String getUrl( String path, IUrlProvider urlProvider ) {
    String pluginStaticBaseUrl = urlProvider != null ? urlProvider.getPluginStaticBaseUrl() : "";
    return RepositoryHelper.joinPaths( pluginStaticBaseUrl, basePath, path );
  }

  public IReadAccess getReader( IContentAccessFactory factory ) {
    return factory.getPluginSystemReader( basePath );
  }
}

/*!
 * Copyright 2013 - 2018 Webdetails, a Hitachi Vantara company. All rights reserved.
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

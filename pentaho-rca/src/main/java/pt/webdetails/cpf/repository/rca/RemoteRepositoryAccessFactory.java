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

package pt.webdetails.cpf.repository.rca;

import pt.webdetails.cpf.api.IUserContentAccessExtended;
import pt.webdetails.cpf.repository.api.IRWAccess;
import pt.webdetails.cpf.repository.api.IReadAccess;
import pt.webdetails.cpf.repository.api.IRepositoryAccessFactory;
import pt.webdetails.cpf.repository.util.RepositoryHelper;

/**
 * Class {@code RemoteRepositoryAccessFactory} provides an implementation of {@code IRepositoryAccessFactory} via REST calls to the Pentaho Server.
 *
 * @see IRepositoryAccessFactory
 * @see IUserContentAccessExtended
 * @see IReadAccess
 * @see IRWAccess
 */
public class RemoteRepositoryAccessFactory implements IRepositoryAccessFactory {

  private final String USERNAME = System.getProperty( "repos.user" );
  private final String PASSWORD = System.getProperty( "repos.password" );
  private final String URI = System.getProperty( "repos.url" );

  @Override
  public IUserContentAccessExtended getUserContentAccess( String basePath ) {
    return new RemoteUserContentAccess( basePath, URI, USERNAME, PASSWORD );
  }

  @Override
  public IReadAccess getPluginRepositoryReader( String pluginRepositoryDir, String basePath ) {
    // credentials for this method should always be that of an administrator account!
    basePath = RepositoryHelper.appendPath( pluginRepositoryDir, basePath );
    return new RemoteReadAccess( basePath, URI, USERNAME, PASSWORD );
  }

  @Override
  public IRWAccess getPluginRepositoryWriter( String pluginRepositoryDir, String basePath ) {
    // credentials for this method should always be that of an administrator account!
    basePath = RepositoryHelper.appendPath( pluginRepositoryDir, basePath );
    return new RemoteReadWriteAccess( basePath, URI, USERNAME, PASSWORD );
  }
}

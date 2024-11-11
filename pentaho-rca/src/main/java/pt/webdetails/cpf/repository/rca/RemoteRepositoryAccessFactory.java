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

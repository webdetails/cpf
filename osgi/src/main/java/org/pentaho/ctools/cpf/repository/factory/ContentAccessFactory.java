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
package org.pentaho.ctools.cpf.repository.factory;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.pentaho.ctools.cpf.repository.bundle.ReadAccessProxy;
import org.pentaho.ctools.cpf.repository.bundle.UserContentAccess;
import pt.webdetails.cpf.repository.api.IContentAccessFactory;
import pt.webdetails.cpf.repository.api.IReadAccess;
import pt.webdetails.cpf.repository.api.IRWAccess;
import pt.webdetails.cpf.repository.api.IUserContentAccess;

/**
 * The {@code ContentAccessFactory} class creates repository access providers for basic plugin needs.
 * These access providers are instances of {@code ReadAccessProxy} that contain a reference to an internal dynamic list
 * of available {@code IReadAccess} services that allow access to the available resources.
 *
 * Note: Write access providers are currently not supported.
 *
 * @see IContentAccessFactory
 * @see IUserContentAccess
 * @see IReadAccess
 * @see IRWAccess
 */
public class ContentAccessFactory implements IContentAccessFactory {
  private static final Log logger = LogFactory.getLog( ContentAccessFactory.class );
  private List<IReadAccess> readAccesses = new ArrayList<>();

  public void addReadAccess( IReadAccess readAccess ) {
    this.readAccesses.add( readAccess );
  }
  public void removeReadAccess( IReadAccess readAccess ) {
    this.readAccesses.remove( readAccess );
  }

  @Override
  public IUserContentAccess getUserContentAccess( String path ) {
    IReadAccess readAccess = this.getReadAccessProxy( path );
    return new UserContentAccess( readAccess );
  }

  @Override
  public IReadAccess getPluginRepositoryReader( String path ) {
    return this.getReadAccessProxy( path );
  }

  @Override
  public IRWAccess getPluginRepositoryWriter( String basePath ) {
    logger.fatal( "Not implemented for the OSGi environment" );
    return null;
  }

  @Override
  public IReadAccess getPluginSystemReader( String path ) {
    return this.getReadAccessProxy( path );
  }

  @Override
  public IRWAccess getPluginSystemWriter( String basePath ) {
    logger.fatal( "Not implemented for the OSGi environment" );
    return null;
  }

  @Override
  public IReadAccess getOtherPluginSystemReader( String pluginId, String path ) {
    return this.getReadAccessProxy( path );
  }

  @Override
  public IRWAccess getOtherPluginSystemWriter( String pluginId, String basePath ) {
    logger.fatal( "Not implemented for the OSGi environment" );
    return null;
  }

  private IReadAccess getReadAccessProxy( String path ) {
    return new ReadAccessProxy( this.readAccesses, path );
  }
}

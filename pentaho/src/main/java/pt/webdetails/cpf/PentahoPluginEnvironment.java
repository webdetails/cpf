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

package pt.webdetails.cpf;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.pentaho.platform.engine.core.system.PentahoSessionHolder;

import pt.webdetails.cpf.api.IContentAccessFactoryExtended;
import pt.webdetails.cpf.api.IUserContentAccessExtended;
import pt.webdetails.cpf.context.api.IUrlProvider;
import pt.webdetails.cpf.plugincall.api.IPluginCall;
import pt.webdetails.cpf.repository.api.IRWAccess;
import pt.webdetails.cpf.repository.api.IReadAccess;
import pt.webdetails.cpf.repository.pentaho.unified.PluginRepositoryResourceAccess;
import pt.webdetails.cpf.repository.pentaho.unified.UserContentRepositoryAccess;
import pt.webdetails.cpf.repository.util.RepositoryHelper;

//TODO: there must be another singleton
public class PentahoPluginEnvironment extends PentahoBasePluginEnvironment implements IContentAccessFactoryExtended {

  private static PentahoPluginEnvironment instance = new PentahoPluginEnvironment();
  private IUrlProvider pentahoUrlProvider;

  static {
    PluginEnvironment.init( instance );
  }

  private static Log logger = LogFactory.getLog( PentahoPluginEnvironment.class );

  static {
    PluginEnvironment.init( instance );
  }

  protected PentahoPluginEnvironment() {
  }

  public static PentahoPluginEnvironment getInstance() {
    return instance;
  }

  public IContentAccessFactoryExtended getContentAccessFactory() {
    return this;
  }

  @Override
  public IUserContentAccessExtended getUserContentAccess( String basePath ) {
    return new UserContentRepositoryAccess( PentahoSessionHolder.getSession(), basePath );
  }

  @Override
  public IReadAccess getPluginRepositoryReader( String basePath ) {
    basePath = RepositoryHelper.appendPath( getPluginRepositoryDir(), basePath );
    return new PluginRepositoryResourceAccess( basePath );
  }

  @Override
  public IRWAccess getPluginRepositoryWriter( String basePath ) {
    basePath = RepositoryHelper.appendPath( getPluginRepositoryDir(), basePath );
    return new PluginRepositoryResourceAccess( basePath );
  }

  @Override
  public IUrlProvider getUrlProvider() {
    if ( pentahoUrlProvider == null ) {
      pentahoUrlProvider = new PentahoUrlProvider( getPluginId() );
    }
    return pentahoUrlProvider;
  }

  public IPluginCall getPluginCall( String pluginId, String servicePath, String method ) {
    return new InterPluginCall( new InterPluginCall.Plugin( pluginId ), servicePath, method );
  }

}

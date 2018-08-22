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

package pt.webdetails.cpf.repository.pentaho.unified;

import org.pentaho.platform.engine.core.system.PentahoSessionHolder;
import pt.webdetails.cpf.api.IUserContentAccessExtended;
import pt.webdetails.cpf.repository.api.IRWAccess;
import pt.webdetails.cpf.repository.api.IReadAccess;
import pt.webdetails.cpf.repository.api.IRepositoryAccessFactory;
import pt.webdetails.cpf.repository.util.RepositoryHelper;

/**
 * Class {@code UnifiedRepositoryAccessFactor} implements {@code IRepositoryAccessFactory} via Unified Repository.
 */
public class UnifiedRepositoryAccessFactory implements IRepositoryAccessFactory {
  @Override
  public IUserContentAccessExtended getUserContentAccess( String basePath ) {
    return new UserContentRepositoryAccess( PentahoSessionHolder.getSession(), basePath );
  }

  @Override
  public IReadAccess getPluginRepositoryReader( String pluginRepositoryDir, String basePath ) {
    basePath = RepositoryHelper.appendPath( pluginRepositoryDir, basePath );
    return new PluginRepositoryResourceAccess( basePath );
  }

  @Override
  public IRWAccess getPluginRepositoryWriter( String pluginRepositoryDir, String basePath ) {
    basePath = RepositoryHelper.appendPath( pluginRepositoryDir, basePath );
    return new PluginRepositoryResourceAccess( basePath );
  }
}

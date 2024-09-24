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

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
package pt.webdetails.cpf.repository.pentaho.unified;

import java.util.concurrent.Callable;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.pentaho.platform.api.repository2.unified.IUnifiedRepository;
import org.pentaho.platform.engine.core.system.PentahoSystem;
import org.pentaho.platform.engine.security.SecurityHelper;

import pt.webdetails.cpf.repository.api.IRWAccess;

/**
 * {@link IPluginResourceAccess} for {@link IUnifiedRepositoryAccess}.<br> Uses system credentials: be careful with user
 * input!<br> TODO: shorter name?
 */
public class PluginRepositoryResourceAccess extends UnifiedRepositoryAccess implements IRWAccess {

  private static final Log logger = LogFactory.getLog( PluginRepositoryResourceAccess.class );

  /**
   * Used for read-only access. TODO: will we need to refresh this?
   */
  private static IUnifiedRepository systemRepository;

  public PluginRepositoryResourceAccess( String basePath ) {
    this.basePath = basePath;
  }

  /**
   * @return {@link IUnifiedRepository} with system credentials.
   */
  private static synchronized IUnifiedRepository getSystemAccessRepository() {
    if ( systemRepository == null ) {
      try {
        systemRepository = SecurityHelper.getInstance().runAsSystem( new Callable<IUnifiedRepository>() {
          public IUnifiedRepository call() throws Exception {
            return PentahoSystem.get( IUnifiedRepository.class );
          }
        } );
      } catch ( Exception e ) {
        // TODO: how fatal is this?
        logger.fatal( "Unable to get repository as system.", e );
        systemRepository = PentahoSystem.get( IUnifiedRepository.class );
        assert systemRepository != null;
      }
    }
    return systemRepository;
  }

  @Override
  protected IUnifiedRepository getRepository() {
    return getSystemAccessRepository();
  }

}

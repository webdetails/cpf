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

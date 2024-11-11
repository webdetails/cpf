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

package pt.webdetails.cpf.repository.pentaho.unified;

import java.util.EnumSet;

import org.apache.commons.lang.StringUtils;
import org.pentaho.platform.api.engine.IPentahoSession;
import org.pentaho.platform.api.repository2.unified.IUnifiedRepository;
import org.pentaho.platform.api.repository2.unified.RepositoryFilePermission;
import org.pentaho.platform.engine.core.system.PentahoSystem;

import pt.webdetails.cpf.Util;
import pt.webdetails.cpf.api.IUserContentAccessExtended;
import pt.webdetails.cpf.repository.api.FileAccess;

/**
 * {@link IUserContentAccessExtended} implementation for {@link IUnifiedRepository}
 */
public class UserContentRepositoryAccess extends UnifiedRepositoryAccess implements IUserContentAccessExtended {

  private IUnifiedRepository repository;
  private IPentahoSession session;

  private static final String DEFAULT_USER_DIR = "/";

  /**
   * @param session User session. If null defaults to user that initiated current thread.
   */
  public UserContentRepositoryAccess( IPentahoSession session ) {
    this( session, DEFAULT_USER_DIR );
  }

  public UserContentRepositoryAccess( IPentahoSession pentahoSession, String startPath ) {
    session = pentahoSession;
    initRepository();
    basePath = StringUtils.isEmpty( startPath ) ? DEFAULT_USER_DIR : startPath;
  }

  protected IUnifiedRepository initRepository() {
    return PentahoSystem.get( IUnifiedRepository.class, session );
  }

  @Override
  protected IUnifiedRepository getRepository() {
    if ( repository == null ) {
      repository = initRepository();
    }
    return repository;
  }

  @Override
  public boolean hasAccess( String path, FileAccess access ) {
    String normalizedPath = Util.joinPath( basePath, path );
    return getRepository().hasAccess( normalizedPath, toRepositoryFilePermissions( access ) );
  }

  protected static EnumSet<RepositoryFilePermission> toRepositoryFilePermissions( FileAccess access ) {
    switch ( access ) {
      case READ:
      case EXECUTE:
        return EnumSet.of( RepositoryFilePermission.READ );
      case WRITE:
        return EnumSet.of( RepositoryFilePermission.WRITE );
      case DELETE:
        return EnumSet.of( RepositoryFilePermission.DELETE );
      default:
        return EnumSet.of( RepositoryFilePermission.ALL );
    }
  }

}

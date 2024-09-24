/*!
 * Copyright 2002 - 2018 Webdetails, a Hitachi Vantara company. All rights reserved.
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

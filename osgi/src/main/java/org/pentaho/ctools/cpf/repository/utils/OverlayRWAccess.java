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
package org.pentaho.ctools.cpf.repository.utils;

import pt.webdetails.cpf.repository.api.IRWAccess;
import pt.webdetails.cpf.repository.api.IReadAccess;

import java.util.List;

/**
 * Class {@code OverlayRWAccess} combines one IRWAccess implementation over a list of IReadAccess implementations,
 * providing a union over the contents of all implementations. The write implementation takes precedence over other
 * implementations and, after that, paths are resolved following the supplied list order.
 *
 * @see IReadAccess
 * @see IRWAccess
 */
public final class OverlayRWAccess extends OverlayAccess<IRWAccess> {

  public OverlayRWAccess( String basePath, IRWAccess writeAccess, List<IReadAccess> readAccessList ) {
    super( basePath, writeAccess, readAccessList );
  }

  @Override
  protected IBasicFileExt obtainReadAccess( String path ) {
    IBasicFileExt result = super.obtainReadAccess( path );
    if ( result == null && path.startsWith( "/system/" ) ) {
      // see: cpf\pentaho\src\main\java\pt\webdetails\cpf\repository\pentaho\SystemPluginResourceAccess.java
      logger.error( "Use of '/system/<pluginid>/' as a special prefix for accessing content from other plugins is not supported. Use the appropriate call from IContentAccessFactory." );
    }
    return result;
  }
}

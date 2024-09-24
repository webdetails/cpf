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

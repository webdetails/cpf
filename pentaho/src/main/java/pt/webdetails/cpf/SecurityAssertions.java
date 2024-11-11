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


package pt.webdetails.cpf;

import org.pentaho.platform.engine.core.system.PentahoSessionHolder;
import org.pentaho.platform.engine.security.SecurityHelper;
import org.pentaho.platform.web.http.api.resources.utils.SystemUtils;
import org.springframework.security.core.authority.SimpleGrantedAuthority;


public final class SecurityAssertions {

  public static void assertIsAdmin(  ) {
    if ( !SystemUtils.canAdminister(  ) ) {
      throw new RuntimeException( "Administrator privileges required." );
    }
  }

  public static void assertHasRole( String role ) {
    if ( !SecurityHelper.getInstance(  ).isGranted( PentahoSessionHolder.getSession(  ), new SimpleGrantedAuthority( role ) ) ) {
      throw new RuntimeException( role + " privileges required." );
    }
  }

}

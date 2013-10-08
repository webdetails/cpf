/*!
* Copyright 2002 - 2013 Webdetails, a Pentaho company.  All rights reserved.
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

import org.pentaho.platform.engine.core.system.PentahoSessionHolder;
import org.pentaho.platform.engine.security.SecurityHelper;
import org.springframework.security.GrantedAuthorityImpl;

public final class SecurityAssertions {

  public static void assertIsAdmin(){
    if(!SecurityHelper.isPentahoAdministrator(PentahoSessionHolder.getSession())){
      throw new RuntimeException("Administrator privileges required.");
    }
  }
  
  public static void assertHasRole(String role){
    if(!SecurityHelper.isGranted(PentahoSessionHolder.getSession(), new GrantedAuthorityImpl(role))){
      throw new RuntimeException(role + " privileges required.");
    }
  }
  
}

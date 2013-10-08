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

package pt.webdetails.cpf.session;

import java.util.List;

import org.pentaho.platform.api.engine.IUserDetailsRoleListService;
import org.pentaho.platform.engine.core.system.PentahoSessionHolder;
import org.pentaho.platform.engine.core.system.PentahoSystem;


public class PentahoSessionUtils implements ISessionUtils {
  
  
   public IUserSession getCurrentSession() {
     return new PentahoSession(PentahoSessionHolder.getSession());
   }

	@Override
	public String[] getSystemPrincipals() {
	    IUserDetailsRoleListService service = PentahoSystem.getUserDetailsRoleListService();
	    if (service != null) {
	      return convert(service.getAllUsers());
	    }
	    return null;

	}
	
	@Override
	public String[] getSystemAuthorities() {
	    IUserDetailsRoleListService service = PentahoSystem.getUserDetailsRoleListService();
	    if (service != null) {
	      return convert(service.getAllRoles());
	    }
	    return null;
	}
	
	private String[] convert(List obj) {
		if (obj != null ) {
			String[] asArray = new String[obj.size()];
			for(int i = 0; i < obj.size();i++) {
				asArray[i] = obj.get(i).toString();
			}
			return asArray;
		}
		return new String[0];
	}
  
}

/* This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this file,
 * You can obtain one at http://mozilla.org/MPL/2.0/. */

package pt.webdetails.cpf.session;

import java.util.List;

//import org.pentaho.platform.api.engine.IUserDetailsRoleListService;
import org.pentaho.platform.engine.core.system.PentahoSessionHolder;
import org.pentaho.platform.engine.core.system.PentahoSystem;


public class PentahoSessionUtils implements ISessionUtils {
  
  
   public IUserSession getCurrentSession() {
     if ( PentahoSessionHolder.getSession() != null ) {
       return new PentahoSession(PentahoSessionHolder.getSession());
     }
     return null;
   }

	@Override
	public String[] getSystemPrincipals() {
//	    IUserDetailsRoleListService service = PentahoSystem.getUserDetailsRoleListService();
//	    if (service != null) {
//	      return convert(service.getAllUsers());
//	    }//FIXME do something!
	    return null;

	}
	
	@Override
	public String[] getSystemAuthorities() {
//	    IUserDetailsRoleListService service = PentahoSystem.getUserDetailsRoleListService();
//	    if (service != null) {
//	      return convert(service.getAllRoles());
//	    }//FIXME do something!
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

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

import org.pentaho.platform.api.engine.IPentahoSession;
import org.pentaho.platform.engine.core.system.PentahoSessionHolder;
import org.pentaho.platform.engine.security.SecurityHelper;
import org.springframework.security.Authentication;
import org.springframework.security.GrantedAuthority;

public class PentahoSession implements IUserSession {
    private IPentahoSession userSession;
    
    public PentahoSession(){
        this(null);
    }
    
    public PentahoSession(IPentahoSession userSession){
        this.userSession = userSession == null ? PentahoSessionHolder.getSession() : userSession;
    }
    
    @Override
    public String getUserName() {
        return userSession.getName();
    }
    
    
    @Override
    public boolean isAdministrator() {
      return SecurityHelper.isPentahoAdministrator(userSession);
    }
    
    public IPentahoSession getPentahoSession(){
        return userSession;
    }

  @Override
  public String[] getAuthorities() {
    Authentication auth = SecurityHelper.getAuthentication(PentahoSessionHolder.getSession(), true);
    GrantedAuthority[] authorities = auth.getAuthorities();
    String[] result = new String[authorities.length];
    int i=0; 
    
    for (GrantedAuthority authority : authorities)
      result[i++] = authority.getAuthority();
    return result;
  }

	@Override
	public Object getParameter(String name) {
		if (name != null)
			return userSession.getAttribute(name.toString());
		return null;
	}
	
	@Override
	public String getStringParameter(String name) {
		Object r = getParameter(name);
		if (r != null)
			return r.toString();
		return null;
	}
	
	@Override
	public void setParameter(String key, Object value) {
		userSession.setAttribute(key.toString(), value);
	}
}

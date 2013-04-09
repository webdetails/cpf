/* This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this file,
 * You can obtain one at http://mozilla.org/MPL/2.0/. */

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

}

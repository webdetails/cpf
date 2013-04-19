/* This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this file,
 * You can obtain one at http://mozilla.org/MPL/2.0/. */

package pt.webdetails.cpk.security;

import org.pentaho.platform.engine.core.system.PentahoSessionHolder;
import org.pentaho.platform.engine.security.SecurityHelper;
import org.springframework.security.Authentication;
import org.springframework.security.GrantedAuthority;
import org.springframework.security.context.SecurityContextHolder;
import org.springframework.security.ui.WebAuthenticationDetails;
import org.springframework.security.userdetails.UserDetails;
import pt.webdetails.cpk.elements.IElement;

/**
 *
 * @author Luís Paulo Silva
 */
public class AccessControl {
    private final String UNAUTHORIZED = "Unauthorized access";
    
    public boolean isAllowed(IElement element){
        boolean is = false;
        
        if(element.isAdminOnly() && isAdmin()){
            is = true;
        }else if(!element.isAdminOnly()){
            is = true;
        }
        
        return is;
    }
    
    public boolean isAdmin(){
        boolean is = false;
        is = SecurityHelper.isPentahoAdministrator(PentahoSessionHolder.getSession());
        
        
        return is;
    }
    
    public void throwAccessDenied(){
        throw new RuntimeException(UNAUTHORIZED);
    }
    
    public void throwAccessDenied(IElement element){
        throw new RuntimeException(UNAUTHORIZED+" "+element.getElementType().toLowerCase()+": "+element.getId());
    }
    
    public UserDetails getUserDetails(){
        Authentication auth = getAuthentication();
        UserDetails ud = null;
        if(auth != null && auth.getPrincipal() != null && auth.getPrincipal() instanceof UserDetails){
            ud = (UserDetails)auth.getPrincipal();
        }
        
        return ud;
    }
    
    public Authentication getAuthentication(){
        return SecurityContextHolder.getContext().getAuthentication();
    }
    
    public GrantedAuthority[] getGrantedAuthorities(){
        return getUserDetails().getAuthorities();
    }
    
    public String getUsername(){
        return getUserDetails().getUsername();
    }
    
    public String getUserPassword(){
        return (String)getAuthentication().getCredentials();
    }
    
    public WebAuthenticationDetails getWebAuthenticationDetails(){
        return (WebAuthenticationDetails)getAuthentication().getDetails();
    }
    
    public String getUserIPAddress(){
        return getWebAuthenticationDetails().getRemoteAddress();
    }
    
    public String getUserSessionID(){
        return getWebAuthenticationDetails().getSessionId();
    }
    
}

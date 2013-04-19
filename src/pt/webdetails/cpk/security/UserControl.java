/* This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this file,
 * You can obtain one at http://mozilla.org/MPL/2.0/. */
package pt.webdetails.cpk.security;

import org.springframework.security.Authentication;
import org.springframework.security.GrantedAuthority;
import org.springframework.security.context.SecurityContextHolder;
import org.springframework.security.ui.WebAuthenticationDetails;
import org.springframework.security.userdetails.UserDetails;

/**
 *
 * @author Luis Paulo Silva<luis.silva@webdetails.pt>
 */
public class UserControl {
    
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
    
    
    private WebAuthenticationDetails getWebAuthenticationDetails(){
        WebAuthenticationDetails details = null;
        
        if(getAuthentication().getDetails() instanceof WebAuthenticationDetails){
            details = (WebAuthenticationDetails)getAuthentication().getDetails();
        }
        
        return details;
    }
    
    public String getUserIPAddress(){
        String ip = null;
        
        if(getWebAuthenticationDetails()!=null){
            ip = getWebAuthenticationDetails().getRemoteAddress();
        }
        
        return ip;
    }
    
    public String getRolesAsCSV(){
        GrantedAuthority [] authorities = getGrantedAuthorities();
        String roles = null;
        
        if(authorities != null){
            roles = new String();
            for(GrantedAuthority auth : authorities){
                roles += auth.getAuthority()+",";
            }
        }
        return roles.substring(0, roles.length()-2);
    }
}

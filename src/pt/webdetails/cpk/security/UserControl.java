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

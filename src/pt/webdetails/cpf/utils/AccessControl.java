/* This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this file,
 * You can obtain one at http://mozilla.org/MPL/2.0/. */

package pt.webdetails.cpf.utils;

import org.pentaho.platform.engine.core.system.PentahoSessionHolder;
import org.pentaho.platform.engine.security.SecurityHelper;
import pt.webdetails.cpk.elements.IElement;

/**
 *
 * @author bandjalah
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
    
}

/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
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
    
    public boolean isAllowed(String path){
        boolean is = false;
        
        if(path.contains("/admin") && isAdmin()){
            is = true;
        }else if(!path.contains("/admin")){
            is = true;
        }
        
        return is;
    }
    
    private boolean isAdmin(){
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

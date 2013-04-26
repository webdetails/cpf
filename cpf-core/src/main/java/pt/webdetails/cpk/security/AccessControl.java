/* This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this file,
 * You can obtain one at http://mozilla.org/MPL/2.0/. */

package pt.webdetails.cpk.security;

import java.util.Map;
import javax.servlet.http.HttpServletResponse;
import javax.xml.ws.http.HTTPException;
import org.pentaho.platform.engine.core.system.PentahoSessionHolder;
import org.pentaho.platform.engine.security.SecurityHelper;
import org.pentaho.platform.web.http.session.PentahoHttpSession;
import pt.webdetails.cpf.http.ICommonParameterProvider;
import pt.webdetails.cpf.utils.PluginUtils;
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
    
    public void throwAccessDenied(Map<String,ICommonParameterProvider> parameterProviders){
        final HttpServletResponse response = PluginUtils.getInstance().getResponse(parameterProviders);
        response.setStatus(HttpServletResponse.SC_FORBIDDEN);
        return;
    }
    
    
    
    
}

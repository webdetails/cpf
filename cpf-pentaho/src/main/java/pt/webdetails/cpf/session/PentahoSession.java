/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package pt.webdetails.cpf.session;

import org.pentaho.platform.api.engine.IPentahoSession;
import org.pentaho.platform.engine.core.system.PentahoSessionHolder;
import org.pentaho.platform.engine.core.system.PentahoSystem;

/**
 * 
 * @author Sammy Guergachi <sguergachi at gmail.com>
 */
public class PentahoSession implements IUserSession{
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
    
    public IPentahoSession getPentahoSession(){
        return userSession;
    }

}

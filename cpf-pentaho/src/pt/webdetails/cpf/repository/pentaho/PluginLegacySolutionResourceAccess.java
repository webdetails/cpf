package pt.webdetails.cpf.repository.pentaho;

import org.pentaho.platform.api.engine.IPentahoSession;
import org.pentaho.platform.api.engine.IUserDetailsRoleListService;
import org.pentaho.platform.engine.core.system.PentahoSystem;
import org.pentaho.platform.engine.core.system.UserSession;
import org.pentaho.platform.engine.security.SecurityHelper;
import org.springframework.security.Authentication;
import org.springframework.security.GrantedAuthority;
import org.springframework.security.providers.anonymous.AnonymousAuthenticationToken;

import pt.webdetails.cpf.repository.api.IRWAccess;

@SuppressWarnings("deprecation")
public class PluginLegacySolutionResourceAccess extends PentahoLegacySolutionAccess implements IRWAccess {

    private static IPentahoSession systemSession;

    public PluginLegacySolutionResourceAccess(String basePath) {
      super(basePath, getAdminSession());
    }

    private static synchronized IPentahoSession getAdminSession() {
      if (systemSession == null) {
        IUserDetailsRoleListService userDetailsRoleListService = PentahoSystem.getUserDetailsRoleListService();
        UserSession session = new UserSession("admin", null, false, null);
        GrantedAuthority[] auths = userDetailsRoleListService.getUserRoleListService().getAllAuthorities();
        Authentication auth = new AnonymousAuthenticationToken("admin", SecurityHelper.SESSION_PRINCIPAL, auths);
        session.setAttribute(SecurityHelper.SESSION_PRINCIPAL, auth);
        session.doStartupActions(null);
        systemSession = session;
      }
      return systemSession;
    }

    
}

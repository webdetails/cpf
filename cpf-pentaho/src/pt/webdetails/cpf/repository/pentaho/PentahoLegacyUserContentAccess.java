package pt.webdetails.cpf.repository.pentaho;


import org.pentaho.platform.api.engine.IPentahoSession;

import pt.webdetails.cpf.repository.api.IUserContentAccess;

public class PentahoLegacyUserContentAccess extends PentahoLegacySolutionAccess implements IUserContentAccess {

  public PentahoLegacyUserContentAccess(String basePath, IPentahoSession session) {
    super(basePath, session);
  }

}

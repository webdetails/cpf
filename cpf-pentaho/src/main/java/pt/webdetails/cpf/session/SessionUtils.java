/* This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this file,
 * You can obtain one at http://mozilla.org/MPL/2.0/. */
package pt.webdetails.cpf.session;

import org.pentaho.platform.engine.core.system.PentahoSessionHolder;


public class SessionUtils implements ISessionUtils {
  
  
   public IUserSession getCurrentSession() {
     return new PentahoSession(PentahoSessionHolder.getSession());
   }
  
}

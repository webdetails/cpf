/* This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this file,
 * You can obtain one at http://mozilla.org/MPL/2.0/. */

package pt.webdetails.cpf.session;

/**
 *
 * @author dfscm
 */
public interface IUserSession {
    
    
    public String getUserName();
    public boolean isAdministrator();
    public String[] getAuthorities();
    
    public Object getParameter(Object key);
    public String getStringParameter(Object key);
    public void setParameter(Object key, Object value);
}

/*! ******************************************************************************
 *
 * Pentaho
 *
 * Copyright (C) 2024 by Hitachi Vantara, LLC : http://www.pentaho.com
 *
 * Use of this software is governed by the Business Source License included
 * in the LICENSE.TXT file.
 *
 * Change Date: 2028-08-13
 ******************************************************************************/

package pt.webdetails.cpf.session;

/**
 * @author dfscm
 */
public interface IUserSession {


  public String getUserName();

  public boolean isAdministrator();

  public String[] getAuthorities();

  public Object getParameter( String key );

  public String getStringParameter( String key );

  public void setParameter( String key, Object value );
}

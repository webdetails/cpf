/*! ******************************************************************************
 *
 * Pentaho
 *
 * Copyright (C) 2024 - 2026 by Pentaho Canada Inc. : http://www.pentaho.com
 *
 * Use of this software is governed by the Business Source License included
 * in the LICENSE.TXT file.
 *
 * Change Date: 2030-06-15
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

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


package pt.webdetails.cpf.impl;

import pt.webdetails.cpf.session.IUserSession;

import java.util.HashMap;
import java.util.Map;

public class SimpleUserSession implements IUserSession {

  private String userName;
  private String[] authorities;
  private boolean isAdministrator;
  private Map<String, Object> attributes = new HashMap<String, Object>();

  public SimpleUserSession( String username, String[] authorities, boolean isAdministrator,
                            Map<String, Object> attributes ) {
    this.userName = username;
    this.authorities = authorities;
    this.isAdministrator = isAdministrator;
    if ( attributes != null ) {
      this.attributes.putAll( attributes );
    }
  }

  public SimpleUserSession() {
    this.isAdministrator = false;
  }


  @Override
  public String getUserName() {
    return userName;
  }

  @Override
  public boolean isAdministrator() {
    return isAdministrator;
  }

  @Override
  public String[] getAuthorities() {
    return authorities;
  }

  @Override
  public Object getParameter( String key ) {
    if ( attributes.containsKey( key ) ) {
      return attributes.get( key );
    }
    return null;
  }

  @Override
  public String getStringParameter( String key ) {
    Object val = getParameter( key );
    if ( val != null ) {
      return val.toString();
    }
    return null;
  }

  @Override
  public void setParameter( String key, Object value ) {
    attributes.put( key, value );
  }

  /**
   * @param userName the userName to set
   */
  public void setUserName( String userName ) {
    this.userName = userName;
  }

  /**
   * @param authorities the authorities to set
   */
  public void setAuthorities( String[] authorities ) {
    this.authorities = authorities;
  }

  /**
   * @param isAdministrator the isAdministrator to set
   */
  public void setAdministrator( boolean isAdministrator ) {
    this.isAdministrator = isAdministrator;
  }

  /**
   * @param attributes the attributes to set
   */
  public void setAttributes( Map<String, Object> attributes ) {
    this.attributes = attributes;
  }

}

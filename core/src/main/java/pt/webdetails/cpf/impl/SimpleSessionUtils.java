/*!
* Copyright 2002 - 2017 Webdetails, a Hitachi Vantara company.  All rights reserved.
* 
* This software was developed by Webdetails and is provided under the terms
* of the Mozilla Public License, Version 2.0, or any later version. You may not use
* this file except in compliance with the license. If you need a copy of the license,
* please go to  http://mozilla.org/MPL/2.0/. The Initial Developer is Webdetails.
*
* Software distributed under the Mozilla Public License is distributed on an "AS IS"
* basis, WITHOUT WARRANTY OF ANY KIND, either express or  implied. Please refer to
* the license for the specific language governing your rights and limitations.
*/

package pt.webdetails.cpf.impl;

import pt.webdetails.cpf.session.ISessionUtils;
import pt.webdetails.cpf.session.IUserSession;

//TODO: do we actually need this anywhere? CDA's usage of this as a dummy doesn't count
public class SimpleSessionUtils implements ISessionUtils {


  private String[] principals;

  private String[] authorities;
  private IUserSession session;

  public SimpleSessionUtils() {
  }

  public SimpleSessionUtils( IUserSession session, String[] principals, String[] authorities ) {
    this.session = session;
    this.principals = principals;
    this.authorities = authorities;
  }

  @Override
  public IUserSession getCurrentSession() {
    return this.session;
  }

  @Override
  public String[] getSystemPrincipals() {
    return this.principals;
  }

  @Override
  public String[] getSystemAuthorities() {
    return this.authorities;
  }

  /**
   * @param principals the principals to set
   */
  public void setSystemPrincipals( String[] principals ) {
    this.principals = principals;
  }

  /**
   * @param authorities the authorities to set
   */
  public void setSystemAuthorities( String[] authorities ) {
    this.authorities = authorities;
  }

  /**
   * @param session the session to set
   */
  public void setSession( IUserSession session ) {
    this.session = session;
  }
}

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

/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package pt.webdetails.cpf.scripting;

import org.mozilla.javascript.Context;
import org.mozilla.javascript.ContextFactory;
import org.mozilla.javascript.Function;
import org.mozilla.javascript.Scriptable;
import pt.webdetails.cpf.session.IUserSession;

/**
 * @author joao
 */
public interface IGlobalScope extends Scriptable {


  public IGlobalScope getInstance();

  public IGlobalScope reset();

  public void init();

  public ContextFactory getContextFactory();

  public Object registerHandler( Context cx, Scriptable thisObj, Object[] args, Function funObj );

  public Object loadTests( Context cx, Scriptable thisObj, Object[] args, Function funObj );

  public void executeScript( String path );

  public void executeScript( Context cx, String path, Scriptable scope );

  public Object print( Context cx, Scriptable thisObj, Object[] args, Function funObj );

  public Object load( Context cx, Scriptable thisObj, Object[] args, Function funObj );

  public Object lib( Context cx, Scriptable thisObj, Object[] args, Function funObj );

  public Object callWithDefaultSession( final Context cx, final Scriptable thisObj, Object[] args, Function funObj );

  public Object getPluginSetting( Context cx, Scriptable thisObj, Object[] args, Function funObj );

  public IUserSession getSession();

  public IUserSession getAdminSession();


}

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

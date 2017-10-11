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

package pt.webdetails.cpf;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import pt.webdetails.cpf.plugin.CorePlugin;

import java.util.HashMap;
import java.util.Map;

public abstract class AbstractInterPluginCall implements IPluginCall {

  protected static final Log logger = LogFactory.getLog( AbstractInterPluginCall.class );

  protected CorePlugin plugin;
  protected String method;

  protected Map<String, Object> requestParameters;

  public AbstractInterPluginCall() {
  }


  /**
   * Creates a new call.
   *
   * @param plugin the plugin to call
   * @param method
   */
  public AbstractInterPluginCall( CorePlugin plugin, String method ) {
    init( plugin, method, new HashMap<String, Object>() );
  }

  public AbstractInterPluginCall( CorePlugin plugin, String method, Map<String, Object> params ) {
    init( plugin, method, params );
  }


  public void init( CorePlugin plugin, String method, Map<String, Object> params ) {
    if ( plugin == null ) {
      throw new IllegalArgumentException( "Plugin must be specified" );
    }

    this.plugin = plugin;
    this.method = method;
    if ( this.requestParameters == null ) {
      this.requestParameters = new HashMap<String, Object>();
    }
    this.requestParameters.putAll( params != null ? params : new HashMap<String, Object>() );
  }

  public String getMethod() {
    return method;
  }

  public void setMethod( String method ) {
    this.method = method;
  }

  public abstract String call();

}

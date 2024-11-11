/*! ******************************************************************************
 *
 * Pentaho
 *
 * Copyright (C) 2024 by Hitachi Vantara, LLC : http://www.pentaho.com
 *
 * Use of this software is governed by the Business Source License included
 * in the LICENSE.TXT file.
 *
 * Change Date: 2029-07-20
 ******************************************************************************/


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

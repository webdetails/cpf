/* This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this file,
 * You can obtain one at http://mozilla.org/MPL/2.0/. */

package pt.webdetails.cpf;

import java.util.HashMap;
import java.util.Map;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import pt.webdetails.cpf.plugin.Plugin;

public abstract class AbstractInterPluginCall implements IPluginCall {
  
  protected static final Log logger = LogFactory.getLog(AbstractInterPluginCall.class);

  protected Plugin plugin;
  protected String method;

  protected Map<String, Object> requestParameters;
  
  public AbstractInterPluginCall(){
  }
  
  
  /**
   * Creates a new call.
   * @param plugin the plugin to call
   * @param method 
   */
  public AbstractInterPluginCall(Plugin plugin, String method){    
    init(plugin, method, new HashMap<String, Object>());
  }
  
  public AbstractInterPluginCall(Plugin plugin, String method, Map<String, Object> params) {
    init(plugin, method, params);    
  }
  
  
  /* (non-Javadoc)
 * @see pt.webdetails.cpf.IPluginCall#init(pt.webdetails.cpf.AbstractInterPluginCall.Plugin, java.lang.String, java.util.Map)
 */
@Override
public void init(Plugin plugin, String method, Map<String, Object>params) {
    if(plugin == null) throw new IllegalArgumentException("Plugin must be specified");
    
    this.plugin = plugin;
    this.method = method;
    this.requestParameters.putAll(
      params != null ?
          params :
          new HashMap<String, Object>());    
  }
  
  /* (non-Javadoc)
 * @see pt.webdetails.cpf.IPluginCall#getMethod()
 */
@Override
public String getMethod() {
    return method;
  }

  /* (non-Javadoc)
 * @see pt.webdetails.cpf.IPluginCall#setMethod(java.lang.String)
 */
@Override
public void setMethod(String method) {
    this.method = method;
  }
  
  /* (non-Javadoc)
 * @see pt.webdetails.cpf.IPluginCall#call()
 */
@Override
public  abstract String call();
}

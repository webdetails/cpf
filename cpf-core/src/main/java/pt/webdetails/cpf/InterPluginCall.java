/* This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this file,
 * You can obtain one at http://mozilla.org/MPL/2.0/. */

package pt.webdetails.cpf;

import java.util.HashMap;
import java.util.Map;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import pt.webdetails.cpf.plugin.Plugin;

public  abstract class InterPluginCall {

  
  protected final static String DEFAULT_ENCODING = "UTF-8";
  
  
  protected static final Log logger = LogFactory.getLog(InterPluginCall.class);

  protected Plugin plugin;
  protected String method;

  protected Map<String, Object> requestParameters;
  
  public InterPluginCall(){
  }
  
  
  /**
   * Creates a new call.
   * @param plugin the plugin to call
   * @param method 
   */
  public InterPluginCall(Plugin plugin, String method){    
    init(plugin, method, new HashMap<String, Object>());
  }
  
  public InterPluginCall(Plugin plugin, String method, Map<String, Object> params) {
    init(plugin, method, params);    
  }
  
  
  public void init(Plugin plugin, String method, Map<String, Object>params) {
    if(plugin == null) throw new IllegalArgumentException("Plugin must be specified");
    
    this.plugin = plugin;
    this.method = method;
    this.requestParameters.putAll(
      params != null ?
          params :
          new HashMap<String, Object>());    
  }
  
  public String getMethod() {
    return method;
  }

  public void setMethod(String method) {
    this.method = method;
  }
  
  public  abstract String call();
}

/* This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this file,
 * You can obtain one at http://mozilla.org/MPL/2.0/. */

package pt.webdetails.cpf;

import java.util.HashMap;
import java.util.Map;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

public  abstract class InterPluginCall {
  public final static Plugin CDA = new Plugin("cda");
  public final static Plugin CDB = new Plugin("cdb");
  public final static Plugin CDC = new Plugin("cdc");
  public final static Plugin CDE = new Plugin("pentaho-cdf-dd");
  public final static Plugin CDF = new Plugin("pentaho-cdf");
  public final static Plugin CDV = new Plugin("cdv");
  
  private final static String DEFAULT_ENCODING = "UTF-8";
  
  public static class Plugin {
    
    private String name;
    private String title;
    
    public String getName() {
      return name;
    }

    public String getTitle() {
      return title;
    }
    
    public Plugin(String name, String title){
      this.name = name;
      this.title = title;
    }
    
    public Plugin(String id){
      this.name = id;
      this.title = id;
    }
    
  }
  
  private static final Log logger = LogFactory.getLog(InterPluginCall.class);

  private Plugin plugin;
  private String method;

  private Map<String, Object> requestParameters;
  
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

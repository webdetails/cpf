/* This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this file,
 * You can obtain one at http://mozilla.org/MPL/2.0/. */

package pt.webdetails.cpf;

import java.io.UnsupportedEncodingException;
import java.lang.annotation.Annotation;
import java.lang.reflect.InvocationTargetException;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.concurrent.Callable;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;
import javax.ws.rs.*;


import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.pentaho.platform.api.engine.IParameterProvider;
import org.pentaho.platform.api.engine.IPentahoSession;
import org.pentaho.platform.api.engine.IPluginManager;
import org.pentaho.platform.api.engine.PluginBeanException;
import org.pentaho.platform.engine.core.solution.SimpleParameterProvider;
import org.pentaho.platform.engine.core.system.PentahoSessionHolder;
import org.pentaho.platform.engine.core.system.PentahoSystem;
import org.pentaho.platform.web.http.request.HttpRequestParameterProvider;
import pt.webdetails.cpf.web.CpfHttpServletRequest;
import pt.webdetails.cpf.web.CpfHttpServletResponse;


/**
 * Call to another pentaho plugin through its content generator.
 * Not thread safe.
 */
public class InterPluginCall implements Runnable, Callable<String> {

  public final static Plugin CDA = new Plugin("cda");
  public final static Plugin CDB = new Plugin("cdb");
  public final static Plugin CDC = new Plugin("cdc");
  public final static Plugin CDE = new Plugin("pentaho-cdf-dd");
  public final static Plugin CDF = new Plugin("pentaho-cdf");
  public final static Plugin CDV = new Plugin("cdv");
  
  public final static String SUFIX = ".utils";
  
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
  private HttpServletResponse response;
  private HttpServletRequest request;
  
  private IPentahoSession session;
  private IPluginManager pluginManager;
  
  private InterPluginCall(){
  }
  
  /**
   * Creates a new call.
   * @param plugin the plugin to call
   * @param method 
   */
  public InterPluginCall(Plugin plugin, String method){
    this();
    
    if(plugin == null) throw new IllegalArgumentException("Plugin must be specified");
    
    this.plugin = plugin;
    this.method = method;
    this.requestParameters = new HashMap<String, Object>();
  }
  
  public InterPluginCall(Plugin plugin, String method, Map<String, Object> params) {
    this(plugin, method);

    this.plugin = plugin;
    this.method = method;
    
    this.requestParameters.putAll(params!=null?params:new HashMap<String, Object>());
  }
  
  protected String getMethod() {
    return method;
  }

  protected void setMethod(String method) {
    this.method = method;
  }
  
  
  protected HttpServletRequest getRequest() {
      if(request == null){
          request = new pt.webdetails.cpf.web.CpfHttpServletRequest();
      }    
      return request;
  }

  protected void setRequest(HttpServletRequest request) {
    this.request = request;
  }

  public boolean pluginExists(){
    return getPluginManager().getClassLoader(plugin.getName()) != null;
  }
  
  /**
   * Put a request parameter 
   * @param name
   * @param value
   * @return this
   */
  public InterPluginCall putParameter(String name, Object value){
    requestParameters.put(name, value);
    return this;
  }
  
  public void run() {
    String pluginName = plugin.getName();

    Class<?> classe = null;
    Method operation = null;
    Object o = null;
    try {
       classe = getPluginManager().getBean(pluginName+SUFIX).getClass();
       Method[] methods = classe.getMethods();
       o = classe.newInstance();
       
       for(Method m : methods){
           if(m.getName() == method){
              operation = m;
           }
       }
    } catch(PluginBeanException ex){
       logger.error("Trying to get a plugin not declared on beans", ex); 
    } catch(InstantiationException ex){
       logger.error("Error while instanciating class of bean with id "+pluginName, ex);
    } catch(IllegalAccessException ex){
       logger.error("Error while instanciating class of bean with id "+pluginName, ex);
    }
    
    Annotation[][] params = operation.getParameterAnnotations();
    Class<?>[] paramTypes = operation.getParameterTypes();
    
    List<Object> parameters = new ArrayList<Object>();
    
    for(int i = 0; i < params.length; i++){
        String paramName = "";
        String paramDefaultValue = "";
        
        for(Annotation annotation : params[i]){
            String annotationClass = annotation.annotationType().getName();
            
            if(annotationClass == "javax.ws.rs.QueryParam"){
                QueryParam param = (QueryParam)annotation;
                paramName = param.value();
            } else if(annotationClass == "javax.ws.rs.DefaultValue"){
                DefaultValue param = (DefaultValue)annotation;
                paramDefaultValue = param.value();
            }
        }
        
        if(requestParameters.containsKey(paramName)){
            if(paramTypes[i] == int.class){
                int val = Integer.parseInt((String)requestParameters.get(paramName));
                parameters.add(val);
            } else if(paramTypes[i] == java.lang.Boolean.class){
                boolean val = Boolean.parseBoolean((String)requestParameters.get(paramName));
                parameters.add(val);
            } else if(paramTypes[i] == java.util.List.class){
                List<String> list = new ArrayList<String>();
                
                String values = (String)requestParameters.get(paramName);
                String[] splittedValues = values.split(",");
                
                for(String s : splittedValues){
                    list.add(s);
                }
                
                parameters.add(list);
            } else if(paramTypes[i] == java.lang.String.class){
                parameters.add(requestParameters.get(paramName));
            }
            requestParameters.remove(paramName);
        } else {
            if(paramTypes[i] == int.class){
                int val = Integer.parseInt((String)paramDefaultValue);
                parameters.add(val);
            } else if(paramTypes[i] == java.lang.Boolean.class){
                boolean val = Boolean.parseBoolean((String)paramDefaultValue);
                parameters.add(val);
            } else if(paramTypes[i] == java.util.List.class){
                List<String> list = new ArrayList<String>();
                
                String values = paramDefaultValue;
                String[] splittedValues = values.split(",");
                
                for(String s : splittedValues){
                    list.add(s);
                }
                parameters.add(list);
            } else if(paramTypes[i] == java.lang.String.class){
                parameters.add(paramDefaultValue);
            }
        }
    }
    
    parameters.add((HttpServletResponse) getParameterProviders().get("path").getParameter("httpresponse"));
    
    CpfHttpServletRequest cpfRequest = (CpfHttpServletRequest)getRequest();
    for(Map.Entry<String, Object> entry : requestParameters.entrySet()){
        String key = entry.getKey();
        Object value = entry.getValue();
        
        cpfRequest.setParameter(key, (String)value);
    }
    
    parameters.add(getRequest());
    
        
    try {
        operation.invoke(o, parameters.toArray());
    } catch (IllegalAccessException ex){
        logger.error("",ex);
    } catch (IllegalArgumentException ex){
        logger.error("",ex);
    } catch (InvocationTargetException ex){
        logger.error("",ex);
    } catch (Exception ex){
        logger.error("",ex);
    }
  }
  
  public String call() {
    run();
    
    CpfHttpServletResponse cpfResponse = (CpfHttpServletResponse)response;
    String content = "";
    
    try{
        content = cpfResponse.getContentAsString();
    } catch(UnsupportedEncodingException ex){
        logger.error("Error getting content from CpfHttpServletResponse", ex);
    }
    return content;
  }

  public void runInPluginClassLoader(){
    getClassLoaderCaller().runInClassLoader(this);
  }

  public String callInPluginClassLoader() {
    try {
      return getClassLoaderCaller().callInClassLoader(this);
    } catch (Exception e) {
      logger.error(e);
      return null;
    }
  }

  public HttpServletResponse getResponse() {
    if(response == null){
      logger.debug("No response passed to method " + this.method + ", adding response.");
      response = new CpfHttpServletResponse();
    }
    
    return response;
  }

  public void setResponse(HttpServletResponse response) {
    this.response = response;
  }
  
  public void setSession(IPentahoSession session) {
    this.session = session;
  }
  
  public void setRequestParameters(Map<String, Object> parameters){
    this.requestParameters = parameters;
  }
  
  public void setRequestParameters(IParameterProvider requestParameterProvider){
    if(!requestParameters.isEmpty()) requestParameters.clear();
    
    for(@SuppressWarnings("unchecked")
    Iterator<String> params = requestParameterProvider.getParameterNames(); params.hasNext();){
      String parameterName = params.next();
      requestParameters.put(parameterName, requestParameterProvider.getParameter(parameterName));
    }
  }
  
  protected IPentahoSession getSession(){
    if(session == null){
      session = PentahoSessionHolder.getSession();
    }
    return session;
  }
  
  protected IParameterProvider getRequestParameterProvider(){
    SimpleParameterProvider provider = null;
    if(request != null){
       provider = new HttpRequestParameterProvider(request);
      provider.setParameters(requestParameters);
    } else {
      provider = new SimpleParameterProvider(requestParameters);
    }
    return provider;
  }
  
  protected ClassLoaderAwareCaller getClassLoaderCaller(){
    return new ClassLoaderAwareCaller(getPluginManager().getClassLoader(plugin.getTitle()));
  }

  protected IPluginManager getPluginManager() {
    if(pluginManager == null){
      pluginManager = PentahoSystem.get(IPluginManager.class, getSession());
    }
    return pluginManager;
  }

  protected IParameterProvider getPathParameterProvider() {
    Map<String, Object> pathMap = new HashMap<String, Object>();
    pathMap.put("path", "/" + method);
//    if (response != null) {
      pathMap.put("httpresponse", getResponse());
//    }
    if(getRequest() != null){
      pathMap.put("httprequest", getRequest());
    }
    IParameterProvider pathParams = new SimpleParameterProvider(pathMap);
    return pathParams;
  }
  
  protected Map<String,IParameterProvider> getParameterProviders(){
    IParameterProvider requestParams = getRequestParameterProvider();
    IParameterProvider pathParams = getPathParameterProvider();
    Map<String, IParameterProvider> paramProvider = new HashMap<String, IParameterProvider>();
    paramProvider.put(IParameterProvider.SCOPE_REQUEST, requestParams);
    paramProvider.put("path", pathParams);
    return paramProvider;
  }

   protected String getEncoding(){
     return DEFAULT_ENCODING;
   }
  

}

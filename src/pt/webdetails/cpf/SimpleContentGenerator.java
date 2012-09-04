/* This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this file,
 * You can obtain one at http://mozilla.org/MPL/2.0/. */

package pt.webdetails.cpf;

import java.io.IOException;
import java.io.OutputStream;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.EnumMap;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.UUID;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.io.IOUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import org.json.JSONException;
import org.pentaho.platform.api.engine.IMimeTypeListener;
import org.pentaho.platform.api.engine.IOutputHandler;
import org.pentaho.platform.api.engine.IParameterProvider;
import org.pentaho.platform.api.repository.IContentItem;

import org.pentaho.platform.engine.core.system.PentahoSessionHolder;
import org.pentaho.platform.engine.security.SecurityHelper;
import org.pentaho.platform.engine.services.solution.BaseContentGenerator;
import org.springframework.security.GrantedAuthorityImpl;

import pt.webdetails.cpf.annotations.AccessLevel;
import pt.webdetails.cpf.annotations.Audited;
import pt.webdetails.cpf.annotations.Exposed;
import pt.webdetails.cpf.audit.CpfAuditHelper;
import pt.webdetails.cpf.repository.RepositoryAccess;

/**
 *
 * @author pdpi
 */
public abstract class SimpleContentGenerator extends BaseContentGenerator {

    private static final long serialVersionUID = 1L;
    protected Log logger = LogFactory.getLog(this.getClass());
   
    protected static final String ENCODING = PluginSettings.ENCODING;
    protected static String getEncoding() { return ENCODING; }
    
    public enum FileType
    {
      JPG, JPEG, PNG, GIF, BMP, JS, CSS, HTML, HTM, XML,
      SVG, PDF, TXT;
      
      public static FileType parse(String value){
        return valueOf(StringUtils.upperCase(value));
      }
    }
    
    public static class MimeType {
      public static final String CSS = "text/css";
      public static final String JAVASCRIPT = "text/javascript";
      public static final String PLAIN_TEXT = "text/plain";
      public static final String HTML = "text/html";
      public static final String XML = "text/xml";
      public static final String JPEG = "img/jpeg";
      public static final String PNG = "image/png";
      public static final String GIF = "image/gif";
      public static final String BMP = "image/bmp";
      public static final String JSON = "application/json";
    }
    
    protected static final EnumMap<FileType, String> mimeTypes = new EnumMap<FileType, String>(FileType.class);
    
    static
    {
      /*
       * Image types
       */
      mimeTypes.put(FileType.JPG, MimeType.JPEG);
      mimeTypes.put(FileType.JPEG, MimeType.JPEG);
      mimeTypes.put(FileType.PNG, MimeType.PNG);
      mimeTypes.put(FileType.GIF, MimeType.GIF);
      mimeTypes.put(FileType.BMP, MimeType.BMP);

      /*
       * HTML (and related) types
       */
      // Deprecated, should be application/javascript, but IE doesn't like that
      mimeTypes.put(FileType.JS, MimeType.JAVASCRIPT);
      mimeTypes.put(FileType.HTM, MimeType.HTML);
      mimeTypes.put(FileType.HTML, MimeType.HTML);
      mimeTypes.put(FileType.CSS, MimeType.CSS);
      mimeTypes.put(FileType.XML, MimeType.XML);
      mimeTypes.put(FileType.TXT, MimeType.PLAIN_TEXT);
    }
    
    protected String getMimeType(String fileName){
      String[] fileNameSplit = StringUtils.split(fileName, '.');// fileName.split("\\.");
      try{
        return getMimeType(FileType.valueOf(fileNameSplit[fileNameSplit.length - 1].toUpperCase()));
      }
      catch(Exception e){
        logger.warn("Unrecognized extension for file name " + fileName);
        return "";
      }
    }
    
    protected String getMimeType(FileType fileType){
      if(fileType == null) return "";
      String mimeType = mimeTypes.get(fileType);
      return mimeType == null ? "" : mimeType;
    }

    
    @Override
    public void createContent() throws Exception {
      IParameterProvider pathParams = getPathParameters();// parameterProviders.get("path");
  
      try {
  
//        final OutputStream out = getResponseOutputStream(MimeType.HTML);

        String path = pathParams.getStringParameter("path", null);
        String[] pathSections = StringUtils.split(path, "/");
  
        if (pathSections == null || pathSections.length == 0) {
          String method = getDefaultPath(path);
          if (!StringUtils.isEmpty(method)) {
            logger.warn("No method supplied, redirecting.");
            redirect(method);
          } else {
            logger.error("No method supplied.");
          }
        } else {
  
          final String methodName = pathSections[0];
          
          try {
            
            final Method method = getMethod(methodName);
            invokeMethod(methodName, method);
  
          } catch (NoSuchMethodException e) {
            logger.warn("couldn't locate method: " + methodName);
            
          } catch (InvocationTargetException e) {
            // get to the cause and log properly
            Throwable cause = e.getCause();
            if(cause == null) cause = e;
            logger.error(methodName, cause);    
          } 
          catch (IllegalAccessException e) {
            logger.warn(methodName + ": " + e.toString());
          } 
          catch (IllegalArgumentException e) {
            logger.error(methodName + ": " + e.toString());
          } 
          catch (IOException e) {
            logger.error(e.toString());
          }
          catch(Exception e) {
            logger.error(methodName, e);
          }
  
        }
      } catch (SecurityException e) {
        logger.warn(e.toString());
      } 
    }

    /**
     * @param methodName
     * @return
     * @throws NoSuchMethodException
     */
    protected Method getMethod(final String methodName) throws NoSuchMethodException {
      final Class<?>[] params = getCGMethodParams();
      final Method method = this.getClass().getMethod(methodName, params);
      return method;
    }
    
    /**
     * @return this plugin's name
     */
    public abstract String getPluginName();
    
    /**
     * @return this plugin's path
     */
    public String getPluginPath(){
      return RepositoryAccess.getSystemDir() + "/" + getPluginName();
    }
    
    /**
     * Get a map of all public methods with the Exposed annotation.
     * Map is not thread-safe and should be used read-only.
     * @param classe Class where to find methods
     * @param log classe's logger
     * @param lowerCase if keys should be in lower case.
     * @return map of all public methods with the Exposed annotation
     */
    protected static Map<String, Method> getExposedMethods(Class<?> classe, boolean lowerCase){
      HashMap<String, Method> exposedMethods = new HashMap<String, Method>();
      Log log = LogFactory.getLog(classe);
      for(Method method : classe.getMethods()){
        if(method.getAnnotation(Exposed.class) != null){
          String methodKey = method.getName().toLowerCase();
          if(exposedMethods.containsKey(methodKey)){
            log.error("Method " + method + " differs from " + exposedMethods.get(methodKey) + " only in case and will override calls to it!!");
          }
          log.debug("registering " + classe.getSimpleName() + "." + method.getName());
          exposedMethods.put(methodKey, method);
        }
      }
      return exposedMethods;
    }
    
    /**
     * In case we need to use reflection with methods that don't just take the OutputStream parameter.
     * @return classes of exposed methods parameters
     */
    protected Class<?>[] getCGMethodParams(){
     return new Class<?>[]{ OutputStream.class };
    }
    
    @SuppressWarnings("deprecation")
    protected OutputStream getResponseOutputStream(final String mimeType) throws IOException {
      IContentItem contentItem = outputHandler.getOutputContentItem(IOutputHandler.RESPONSE, IOutputHandler.CONTENT, "", instanceId, mimeType);
      return contentItem.getOutputStream(null);
    }

    protected HttpServletRequest getRequest(){
      return (HttpServletRequest) parameterProviders.get("path").getParameter("httprequest");
    }
    
    protected HttpServletResponse getResponse(){
      return (HttpServletResponse) parameterProviders.get("path").getParameter("httpresponse");
    }
    
    protected IParameterProvider getRequestParameters(){
      return parameterProviders.get("request");
    }
    protected IParameterProvider getPathParameters(){
      return parameterProviders.get("path");
    }
    
    protected String getDefaultPath(String path){
      return null;
    }
    
    private boolean canAccessMethod(Method method, Exposed exposed){
      if (exposed != null) {
        
        AccessLevel accessLevel = exposed.accessLevel();
        if(accessLevel != null) {
          
          boolean accessible = false;
          switch (accessLevel) {
            case ADMIN:
              accessible = SecurityHelper.isPentahoAdministrator(PentahoSessionHolder.getSession());
              break;
            case ROLE:
              String role = exposed.role();
              if (!StringUtils.isEmpty(role)) {
                accessible = SecurityHelper.isGranted(PentahoSessionHolder.getSession(), new GrantedAuthorityImpl(role));
              }
              break;
            case PUBLIC:
              accessible = true;
              break;
            default:
              logger.error("Unsupported AccessLevel " + accessLevel);
          }
          
          return accessible;
        }
        
      }
      return false;
    }

    
    protected boolean invokeMethod(final String methodName, final Method method) 
        throws InvocationTargetException, IllegalArgumentException, IllegalAccessException, IOException {
      
      Exposed exposed = method.getAnnotation(Exposed.class);
      
      if(canAccessMethod(method, exposed)){
        
        Audited audited = method.getAnnotation(Audited.class);
        UUID uuid = null;
        long start = System.currentTimeMillis(); 
        if(audited != null){
          uuid = CpfAuditHelper.startAudit(getPluginName(), audited.action(), getObjectName(), userSession, this, getRequestParameters());
        }
        final OutputStream out = getResponseOutputStream(exposed.outputType());
        setResponseHeaders(exposed.outputType());
        try{
          method.invoke(this, out);
        }
        finally {
          if(audited != null){
            CpfAuditHelper.endAudit(getPluginName(), audited.action(), getObjectName(), userSession, this, start, uuid, System.currentTimeMillis());
          }
        }
        
        return true;
      }
      String msg = "Method " + methodName + " not exposed or user does not have required permissions."; 
      logger.error(msg);
      getResponse().sendError(HttpServletResponse.SC_FORBIDDEN, msg);
      return false;
    }
    
    protected void redirect(String method){
      
      final HttpServletResponse response = (HttpServletResponse) parameterProviders.get("path").getParameter("httpresponse");
      
      if (response == null)
      {
        logger.error("response not found");
        return;
      }
      try {
        response.sendRedirect(method);
      } catch (IOException e) {
        logger.error("could not redirect", e);
      }
    }
    
    /**
     * Write to OutputStream using defined encoding.
     * @param out
     * @param contents
     * @throws IOException
     */
    protected void writeOut(OutputStream out, String contents) throws IOException {
      IOUtils.write(contents, out, getEncoding());
    }
    
    protected void writeOut(OutputStream out, JsonSerializable contents) throws IOException, JSONException {
      IOUtils.write(contents.toJSON().toString(), out, getEncoding());
    }

    @Override
    public Log getLogger() {
        return logger;
    }
    
    protected void setResponseHeaders(final String mimeType){
      setResponseHeaders(mimeType, 0, null);
    }
    
    protected void setResponseHeaders(final String mimeType, final String attachmentName){
      setResponseHeaders(mimeType, 0, attachmentName);
    }
    
    protected void setResponseHeaders(final String mimeType, final int cacheDuration, final String attachmentName)
    {
      // Make sure we have the correct mime type
      
      final IMimeTypeListener mimeTypeListener = outputHandler.getMimeTypeListener();
      if (mimeTypeListener != null)
      {
        mimeTypeListener.setMimeType(mimeType);
      }
      
      final HttpServletResponse response = getResponse();

      if (response == null)
      {
        logger.warn("Parameter 'httpresponse' not found!");
        return;
      }

      response.setHeader("Content-Type", mimeType);

      if (attachmentName != null)
      {
        response.setHeader("content-disposition", "attachment; filename=" + attachmentName);
      } // Cache?

      if (cacheDuration > 0)
      {
        response.setHeader("Cache-Control", "max-age=" + cacheDuration);
      }
      else
      {
        response.setHeader("Cache-Control", "max-age=0, no-store");
      }
    }
    
    protected void copyParametersFromProvider(Map<String, Object> params, IParameterProvider provider){
      @SuppressWarnings("unchecked")
      Iterator<String> paramNames = provider.getParameterNames();
      while(paramNames.hasNext()){
        String paramName = paramNames.next();
        params.put(paramName, provider.getParameter(paramName));
      }
    }
}

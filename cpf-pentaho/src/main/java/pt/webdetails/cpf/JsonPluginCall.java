/* This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this file,
 * You can obtain one at http://mozilla.org/MPL/2.0/. */

package pt.webdetails.cpf;

import org.json.JSONException;
import org.json.JSONObject;

import pt.webdetails.cpf.InterPluginCall.Plugin;

public class JsonPluginCall {
  
    //InterPluginCall internal;
  PentahoInterPluginCall internal;
  
  public JsonPluginCall(Plugin plugin, String method) {
    //internal = new InterPluginCall(plugin, method);
      internal = new PentahoInterPluginCall();
      
  }
  
  public JSONObject call(JSONObject request) throws JSONException {
    internal.setOutputStream(null);
    internal.putParameter(JsonRequestHandler.JSON_REQUEST_PARAM, request);
    String result = internal.call();
    return new JSONObject(result);
  }
  
//  public static class EnumerationOfIterator<T> implements Enumeration<T>{
//    
//    private Iterator<T> iterator;
//    
//    public EnumerationOfIterator(Iterator<T> iterator){
//      this.iterator = iterator;
//    }
//
//    @Override
//    public boolean hasMoreElements() {
//      return iterator.hasNext();
//    }
//
//    @Override
//    public T nextElement() {
//      return iterator.next();
//    }
//  }
//  
//  public static class BogusRequest implements HttpServletRequest {
//
//    private String method;
//    private Map<String,Object> reqParameters;
//    
//    public BogusRequest(HttpMethod httpMethod, Map<String,Object> reqParameters){
//      this.method = httpMethod.toString();
//      this.reqParameters = reqParameters;
//    }
//    
//    @Override
//    public Object getAttribute(String name) {
//      // TODO Auto-generated method stub
//      return null;
//    }
//
//    @Override
//    public Enumeration getAttributeNames() {
//      // TODO Auto-generated method stub
//      return null;
//    }
//
//    @Override
//    public String getCharacterEncoding() {
//      // TODO Auto-generated method stub
//      return null;
//    }
//
//    @Override
//    public void setCharacterEncoding(String env) throws UnsupportedEncodingException {
//      // TODO Auto-generated method stub
//      
//    }
//
//    @Override
//    public int getContentLength() {
//      // TODO Auto-generated method stub
//      return 0;
//    }
//
//    @Override
//    public String getContentType() {
//      // TODO Auto-generated method stub
//      return null;
//    }
//
//    @Override
//    public ServletInputStream getInputStream() throws IOException {
//      // TODO Auto-generated method stub
//      return null;
//    }
//
//    @Override
//    public String getParameter(String name) {
//      return (String) reqParameters.get(name);
//    }
//
//    @Override
//    public Enumeration<String> getParameterNames() {
//      return new EnumerationOfIterator<String>(reqParameters.keySet().iterator());
//    }
//
//    @Override
//    public String[] getParameterValues(String name) {
//      return reqParameters.values().toArray(new String[reqParameters.size()]);
//    }
//
//    @Override
//    public Map<String, Object> getParameterMap() {
//      return reqParameters;
//    }
//
//    @Override
//    public String getProtocol() {
//      // TODO Auto-generated method stub
//      return null;
//    }
//
//    @Override
//    public String getScheme() {
//      // TODO Auto-generated method stub
//      return null;
//    }
//
//    @Override
//    public String getServerName() {
//      // TODO Auto-generated method stub
//      return null;
//    }
//
//    @Override
//    public int getServerPort() {
//      // TODO Auto-generated method stub
//      return 0;
//    }
//
//    @Override
//    public BufferedReader getReader() throws IOException {
//      // TODO Auto-generated method stub
//      return null;
//    }
//
//    @Override
//    public String getRemoteAddr() {
//      // TODO Auto-generated method stub
//      return null;
//    }
//
//    @Override
//    public String getRemoteHost() {
//      // TODO Auto-generated method stub
//      return null;
//    }
//
//    @Override
//    public void setAttribute(String name, Object o) {
//      // TODO Auto-generated method stub
//      
//    }
//
//    @Override
//    public void removeAttribute(String name) {
//      // TODO Auto-generated method stub
//      
//    }
//
//    @Override
//    public Locale getLocale() {
//      // TODO Auto-generated method stub
//      return null;
//    }
//
//    @Override
//    public Enumeration getLocales() {
//      // TODO Auto-generated method stub
//      return null;
//    }
//
//    @Override
//    public boolean isSecure() {
//      // TODO Auto-generated method stub
//      return false;
//    }
//
//    @Override
//    public RequestDispatcher getRequestDispatcher(String path) {
//      // TODO Auto-generated method stub
//      return null;
//    }
//
//    @Override
//    public String getRealPath(String path) {
//      // TODO Auto-generated method stub
//      return null;
//    }
//
//    @Override
//    public int getRemotePort() {
//      // TODO Auto-generated method stub
//      return 0;
//    }
//
//    @Override
//    public String getLocalName() {
//      // TODO Auto-generated method stub
//      return null;
//    }
//
//    @Override
//    public String getLocalAddr() {
//      // TODO Auto-generated method stub
//      return null;
//    }
//
//    @Override
//    public int getLocalPort() {
//      // TODO Auto-generated method stub
//      return 0;
//    }
//
//    @Override
//    public String getAuthType() {
//      // TODO Auto-generated method stub
//      return null;
//    }
//
//    @Override
//    public Cookie[] getCookies() {
//      // TODO Auto-generated method stub
//      return null;
//    }
//
//    @Override
//    public long getDateHeader(String name) {
//      // TODO Auto-generated method stub
//      return 0;
//    }
//
//    @Override
//    public String getHeader(String name) {
//      // TODO Auto-generated method stub
//      return null;
//    }
//
//    @Override
//    public Enumeration getHeaders(String name) {
//      // TODO Auto-generated method stub
//      return null;
//    }
//
//    @Override
//    public Enumeration getHeaderNames() {
//      // TODO Auto-generated method stub
//      return null;
//    }
//
//    @Override
//    public int getIntHeader(String name) {
//      // TODO Auto-generated method stub
//      return 0;
//    }
//
//    @Override
//    public String getMethod() {
//      return method.toString();
//    }
//
//    @Override
//    public String getPathInfo() {
//      // TODO Auto-generated method stub
//      return null;
//    }
//
//    @Override
//    public String getPathTranslated() {
//      // TODO Auto-generated method stub
//      return null;
//    }
//
//    @Override
//    public String getContextPath() {
//      // TODO Auto-generated method stub
//      return null;
//    }
//
//    @Override
//    public String getQueryString() {
//      // TODO Auto-generated method stub
//      return null;
//    }
//
//    @Override
//    public String getRemoteUser() {
//      // TODO Auto-generated method stub
//      return null;
//    }
//
//    @Override
//    public boolean isUserInRole(String role) {
//      // TODO Auto-generated method stub
//      return false;
//    }
//
//    @Override
//    public Principal getUserPrincipal() {
//      // TODO Auto-generated method stub
//      return null;
//    }
//
//    @Override
//    public String getRequestedSessionId() {
//      // TODO Auto-generated method stub
//      return null;
//    }
//
//    @Override
//    public String getRequestURI() {
//      // TODO Auto-generated method stub
//      return null;
//    }
//
//    @Override
//    public StringBuffer getRequestURL() {
//      // TODO Auto-generated method stub
//      return null;
//    }
//
//    @Override
//    public String getServletPath() {
//      // TODO Auto-generated method stub
//      return null;
//    }
//
//    @Override
//    public HttpSession getSession(boolean create) {
//      // TODO Auto-generated method stub
//      return null;
//    }
//
//    @Override
//    public HttpSession getSession() {
//      // TODO Auto-generated method stub
//      return null;
//    }
//
//    @Override
//    public boolean isRequestedSessionIdValid() {
//      // TODO Auto-generated method stub
//      return false;
//    }
//
//    @Override
//    public boolean isRequestedSessionIdFromCookie() {
//      // TODO Auto-generated method stub
//      return false;
//    }
//
//    @Override
//    public boolean isRequestedSessionIdFromURL() {
//      // TODO Auto-generated method stub
//      return false;
//    }
//
//    @Override
//    public boolean isRequestedSessionIdFromUrl() {
//      // TODO Auto-generated method stub
//      return false;
//    }
//    
//  }

}

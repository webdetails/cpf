package pt.webdetails.cpf.messaging;

import java.net.URLEncoder;
import java.util.Collections;
import java.util.Enumeration;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import org.apache.commons.lang.StringUtils;
import org.pentaho.platform.engine.core.system.PentahoRequestContextHolder;

import pt.webdetails.cpf.Util;
import pt.webdetails.cpf.utils.CharsetHelper;
import pt.webdetails.cpf.web.CpfHttpServletRequest;

/**
 * InterPluginCall request, only cares about path and parameters
 */
//TODO: just extend CpfHtt...
public class MockHttpServletRequest extends CpfHttpServletRequest implements HttpServletRequest {

//  private static final HttpServletRequest DEFAULT_BASE_REQUEST = new CpfHttpServletRequest();

  protected Map<String, String[]> parameters;
  protected String pathInfo;

//  public MockHttpServletRequest( HttpServletRequest request, Map<String, String[]> requestParameters ) {
////    super( request );
//    this.parameters = requestParameters;
//  }

  public MockHttpServletRequest(String path, Map<String, String[]> requestParameters) {
//    this(DEFAULT_BASE_REQUEST, requestParameters);
    this.pathInfo = path;
    this.parameters = requestParameters;
    setParameterMap( this.parameters );
  }

  @Override
  public String getMethod() {
    return "GET";//XXX need to define in pluginCall, no other way
  }

  @Override
  public String getPathInfo() {
    return pathInfo;
  }

  @Override
  public String getServletPath() {
    return "/plugin";
  }

  @Override
  public String getContextPath() {
    return StringUtils.removeEnd( PentahoRequestContextHolder.getRequestContext().getContextPath(), "/" );//XXX ?
  }

  @Override
  public String getRequestURI() {
    return Util.joinPath( getContextPath(), getServletPath() , getPathInfo() );
  }

  @Override
  public String getQueryString() {

    if (parameters == null || parameters.size() < 1) {
      return "";
    }
    StringBuilder qb = new StringBuilder();
    qb.append( "?" );
    boolean isFirst = true;
    for (String key : parameters.keySet()) {
      for (String value : parameters.get(key)) {
        if (!isFirst) {
          qb.append( "&" );
        }
        
        qb.append( Util.urlEncode( value ) );//TODO: URLEncode etc
      }
    }
    //for (String key )
    return super.getQueryString();
  }

  public Enumeration<String> getParameterNames() {
    return Collections.enumeration(this.parameters.keySet());
  }

  @Override
  public String getParameter(String name) {
    String[] paramValue = parameters.get( name );
    if (paramValue != null && paramValue.length > 0) {
      return paramValue[0];
    }
    return null;
  }

  @Override
  public String[] getParameterValues(String name) {
      return this.parameters.get(name);
  }

  @Override
  public Map<String, String[]> getParameterMap() {
      return Collections.unmodifiableMap(this.parameters);
  }

}

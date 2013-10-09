package pt.webdetails.cpf.messaging;

import java.util.Collections;
import java.util.Enumeration;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import org.apache.commons.lang.StringUtils;
import org.pentaho.platform.engine.core.system.PentahoRequestContextHolder;

import pt.webdetails.cpf.Util;
import pt.webdetails.cpf.web.CpfHttpServletRequest;

/**
 * InterPluginCall request, only cares about path and parameters.<br>
 * Intended to hide complexity, migh end up replicatig..
 */
//TODO: just extend CpfHtt...
public class MockHttpServletRequest extends CpfHttpServletRequest implements HttpServletRequest {


  protected Map<String, String[]> parameters;
  protected String pathInfo;
  protected String method = "GET";

  public MockHttpServletRequest(String path, Map<String, String[]> requestParameters) {
    this.pathInfo = path;
    this.parameters = requestParameters;
    setParameterMap( this.parameters );
  }

  @Override
  public String getMethod() {
    return method;
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
    // apis different, in request no trailing / can be present
    return StringUtils.removeEnd( PentahoRequestContextHolder.getRequestContext().getContextPath(), "/" );
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

    boolean isFirst = true;
    for (String key : parameters.keySet()) {
      for (String value : parameters.get(key)) {
        if (!isFirst) {
          qb.append( "&" );
        }
        else {
          isFirst = false;
        }
        qb.append( Util.urlEncode( key ));
        qb.append( '=' );
        qb.append( Util.urlEncode( value ) );
      }
    }
    return qb.toString();
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

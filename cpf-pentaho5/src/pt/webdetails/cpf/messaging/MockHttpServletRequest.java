package pt.webdetails.cpf.messaging;

import java.util.Collections;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import org.pentaho.platform.engine.core.system.PentahoRequestContextHolder;

import pt.webdetails.cpf.Util;
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
  }

//  @Override
//  public String getMethod() {
//    return HttpMethod.GET.toString();//XXX
//  }

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
    return PentahoRequestContextHolder.getRequestContext().getContextPath();//XXX ?
  }

  @Override
  public String getRequestURI() {
    return Util.joinPath( getContextPath(), getServletPath() , getPathInfo() );
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

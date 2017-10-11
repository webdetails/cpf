/*!
* Copyright 2002 - 2017 Webdetails, a Hitachi Vantara company.  All rights reserved.
* 
* This software was developed by Webdetails and is provided under the terms
* of the Mozilla Public License, Version 2.0, or any later version. You may not use
* this file except in compliance with the license. If you need a copy of the license,
* please go to  http://mozilla.org/MPL/2.0/. The Initial Developer is Webdetails.
*
* Software distributed under the Mozilla Public License is distributed on an "AS IS"
* basis, WITHOUT WARRANTY OF ANY KIND, either express or  implied. Please refer to
* the license for the specific language governing your rights and limitations.
*/

package pt.webdetails.cpf;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.mozilla.javascript.Callable;
import org.mozilla.javascript.Context;
import org.mozilla.javascript.Scriptable;
import pt.webdetails.cpf.http.ICommonParameterProvider;
import pt.webdetails.cpf.scripting.IGlobalScope;
import pt.webdetails.cpf.utils.CharsetHelper;
import pt.webdetails.cpf.utils.MimeTypes;

import javax.servlet.http.HttpServletResponse;
import java.io.OutputStream;
import java.util.HashMap;
import java.util.Map;

/**
 * @author pdpi
 */
//TODO: move, rename
public class Router implements RestRequestHandler {

  private Map<Key, Callable> javaScriptHandlers;
  private Map<Key, RequestHandler> javaHandlers;
  private static Router _instance;
  protected static final Log logger = LogFactory.getLog( Router.class );
  private static IGlobalScope globalScope;

  public static synchronized Router getBaseRouter() {
    if ( _instance == null ) {
      _instance = new Router( globalScope );
    }
    return _instance;
  }

  public static synchronized Router resetBaseRouter() {
    _instance = new Router( globalScope );
    return _instance;
  }

  public Router( IGlobalScope globalScope ) {
    javaScriptHandlers = new HashMap<Key, Callable>();
    javaHandlers = new HashMap<Key, RequestHandler>();
    this.globalScope = globalScope;
  }

  //    @Override
  public void call( OutputStream out, ICommonParameterProvider pathParams, ICommonParameterProvider requestParams ) {
    route( HttpMethod.GET, "", out, pathParams, requestParams );
  }

  @Override
  public boolean canHandle( HttpMethod method, String path ) {
    Key key = new Key( method, path );
    return javaScriptHandlers.containsKey( key ) || javaHandlers.containsKey( key );
  }

  @Override
  public void route( HttpMethod method, String path, OutputStream out, ICommonParameterProvider pathParams,
                     ICommonParameterProvider requestParams ) {
    Key key = new Key( method, path );
    if ( javaScriptHandlers.containsKey( key ) ) {
      Callable handler = javaScriptHandlers.get( key );
      Context cx = globalScope.getContextFactory().enterContext();
      try {
        IGlobalScope scope = globalScope.getInstance();
        ResponseWrapper r = new ResponseWrapper( (HttpServletResponse) pathParams.getParameter( "httpresponse" ) );
        Scriptable thiz = cx.getWrapFactory().wrapAsJavaObject( cx, scope, r, null ),
            pParams = cx.getWrapFactory().wrapAsJavaObject( cx, scope, pathParams, null ),
            rParams = cx.getWrapFactory().wrapAsJavaObject( cx, scope, requestParams, null );

        Object[] params = { out, pParams, rParams };
        handler.call( cx, scope, thiz, params ).toString().getBytes( CharsetHelper.getEncoding() );
      } catch ( Exception e ) {
        logger.error( e );
      } finally {
        Context.exit();
      }
    } else if ( javaHandlers.containsKey( key ) ) {
      RequestHandler handler = javaHandlers.get( key );
      handler.call( out, pathParams, requestParams );
    }
  }

  public void registerHandler( HttpMethod method, String path, Callable handler ) {
    javaScriptHandlers.put( new Key( method, path ), handler );
  }

  public void registerHandler( HttpMethod method, String path, RequestHandler handler ) {
    javaHandlers.put( new Key( method, path ), handler );
  }

  @Override
  public String getResponseMimeType() {
    return MimeTypes.HTML;
  }

  class Key {

    private String path;
    private HttpMethod method;

    public Key( HttpMethod method, String path ) {
      this.method = method;
      this.path = path;
    }

    @Override
    public boolean equals( Object obj ) {
      if ( obj == null ) {
        return false;
      }
      if ( getClass() != obj.getClass() ) {
        return false;
      }
      final Key other = (Key) obj;
      if ( ( this.method == null ) ? ( other.method != null ) : !this.method.equals( other.method ) ) {
        return false;
      }
      if ( ( this.path == null ) ? ( other.path != null ) : !this.path.equals( other.path ) ) {
        return false;
      }
      return true;
    }

    @Override
    public int hashCode() {
      int hash = 7;
      hash = 83 * hash + ( this.method != null ? this.method.hashCode() : 0 );
      hash = 83 * hash + ( this.path != null ? this.path.hashCode() : 0 );
      return hash;
    }
  }
}

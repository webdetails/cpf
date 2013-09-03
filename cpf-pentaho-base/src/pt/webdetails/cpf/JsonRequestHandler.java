/* This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this file,
 * You can obtain one at http://mozilla.org/MPL/2.0/. */

package pt.webdetails.cpf;

import java.io.IOException;
import java.io.OutputStream;

import org.apache.commons.io.IOUtils;
import org.apache.commons.logging.LogFactory;
import org.json.JSONException;
import org.json.JSONObject;
import org.pentaho.platform.api.engine.IParameterProvider;

import pt.webdetails.cpf.utils.CharsetHelper;


public abstract class JsonRequestHandler implements RequestHandler {

  public static String JSON_REQUEST_PARAM = "payload";
  
  //@Override
  public void call(OutputStream out, IParameterProvider pathParams, IParameterProvider requestParams) {
    
    Object request = requestParams.getParameter(JSON_REQUEST_PARAM);
    JSONObject jsonRequest = null;
    if(request instanceof JSONObject){
      jsonRequest = (JSONObject) request;
    }
    else if(request instanceof String){
      try {
        jsonRequest = new JSONObject((String) request);
      } catch (JSONException e) {
        String msg = "Error deserializing JSON request '" + request + "'";
        try {
          IOUtils.write(msg, out, CharsetHelper.getEncoding());
        } catch (IOException e1) {
          
        }
        LogFactory.getLog(this.getClass()).error(msg, e);
      }
    }
    
    try {
      JSONObject result = call(jsonRequest);
      IOUtils.write(result.toString(), out, CharsetHelper.getEncoding());
    } catch (Exception e) {
      LogFactory.getLog(this.getClass()).error("", e);
    }
    
  }
  
  public abstract JSONObject call(JSONObject request) throws Exception;


}

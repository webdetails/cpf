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

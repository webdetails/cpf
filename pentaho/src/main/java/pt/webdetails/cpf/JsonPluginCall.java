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

import org.json.JSONException;
import org.json.JSONObject;

import pt.webdetails.cpf.InterPluginCall.Plugin;

public class JsonPluginCall {
  
  InterPluginCall internal;
  
  public JsonPluginCall(Plugin plugin, String method) {
    internal = new InterPluginCall(plugin, method);
  }
  
  public JSONObject call(JSONObject request) throws JSONException {
    //internal.setOutputStream(null);
    internal.putParameter(JsonRequestHandler.JSON_REQUEST_PARAM, request);
    String result = internal.call();
    return new JSONObject(result);
  }


}

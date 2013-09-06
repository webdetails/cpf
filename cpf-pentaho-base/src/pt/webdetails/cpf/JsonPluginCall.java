/* This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this file,
 * You can obtain one at http://mozilla.org/MPL/2.0/. */

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

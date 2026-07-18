/*! ******************************************************************************
 *
 * Pentaho
 *
 * Copyright (C) 2024 - 2026 by Pentaho Canada Inc. : http://www.pentaho.com
 *
 * Use of this software is governed by the Business Source License included
 * in the LICENSE.TXT file.
 *
 * Change Date: 2030-06-15
 ******************************************************************************/



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

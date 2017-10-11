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

import org.apache.commons.lang.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.json.JSONException;
import org.json.JSONObject;
import pt.webdetails.cpf.messaging.JsonSerializable;


/**
 * JSON method call result wrapper
 */
public class Result implements JsonSerializable {
  
  private static Log logger = LogFactory.getLog(Result.class);
  
  public enum Status {
    OK,
    ERROR
  }
  
  private JSONObject json;
  
  public Result() {}
  public Result(Status status, Object result) {
    json = new JSONObject();
    try {
      json.put("status", status.toString());
      
      if(result != null && result instanceof JsonSerializable){
        json.put("result",  ((JsonSerializable)result).toJSON());
      }
      else json.put("result", result);

    } catch (JSONException e) {
      logger.error("Error writing JSON",e);
    }
    
  }
  
  public Result(JSONObject json) {
    this.json = json;
  }
  
  public Status getStatus(){
    try {
      return Status.valueOf(json.getString("status"));
    } catch (Exception e) {
      return Status.ERROR;
    }
  }

  public static Result getFromException(Exception e)
  {  
    String msg = e.getLocalizedMessage();
    if(StringUtils.isEmpty(msg)){
      msg = e.getMessage();
      if(StringUtils.isEmpty(msg)){
        msg = e.getClass().getName();
      }
    }
    return getError(msg);
  }
  
  public static Result getOK(Object result){
    
    return new Result(Status.OK, result);
  }
  

  public static Result getError(String msg){
    return new Result(Status.ERROR, msg);
  }
  
  @Override
  public String toString(){
    return json != null ? json.toString() : "null";
  }
  @Override
  public JSONObject toJSON() throws JSONException {
    return json;
  }

  
}

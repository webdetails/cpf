/*! ******************************************************************************
 *
 * Pentaho
 *
 * Copyright (C) 2024 by Hitachi Vantara, LLC : http://www.pentaho.com
 *
 * Use of this software is governed by the Business Source License included
 * in the LICENSE.TXT file.
 *
 * Change Date: 2028-08-13
 ******************************************************************************/


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

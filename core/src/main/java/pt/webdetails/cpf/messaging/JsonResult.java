/*!
* Copyright 2002 - 2018 Webdetails, a Hitachi Vantara company.  All rights reserved.
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

package pt.webdetails.cpf.messaging;

import com.fasterxml.jackson.core.JsonGenerationException;
import com.fasterxml.jackson.core.JsonGenerator;

import java.io.IOException;

public class JsonResult implements JsonGeneratorSerializable {

  public static String OK_FIELD = "status";
  public static String SUCCESS_FIELD = "success";
  public static String OK_STATUS = "ok";
  public static String ERROR_STATUS = "error";
  public static String PAYLOAD_FIELD = "result";

  private boolean status;
  private JsonGeneratorSerializable payload;

  public JsonResult( boolean success, JsonGeneratorSerializable payload ) {
    this.status = success;
    this.payload = payload;
  }

  public JsonResult( boolean success, final String contents ) {
    this.status = success;
    this.payload = new JsonGeneratorSerializable() {
      public void writeToGenerator( JsonGenerator jsonGenerator ) throws JsonGenerationException, IOException {
        jsonGenerator.writeString( contents );
      }
    };
  }

  public void writeToGenerator( JsonGenerator jsonGenerator ) throws JsonGenerationException, IOException {
    jsonGenerator.writeStartObject();
    jsonGenerator.writeBooleanField( SUCCESS_FIELD, status );
    jsonGenerator.writeStringField( OK_FIELD, status ? OK_STATUS : ERROR_STATUS );
    jsonGenerator.writeFieldName( PAYLOAD_FIELD );
    payload.writeToGenerator( jsonGenerator );
    jsonGenerator.writeEndObject();
  }

}

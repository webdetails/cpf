/*! ******************************************************************************
 *
 * Pentaho
 *
 * Copyright (C) 2024 by Hitachi Vantara, LLC : http://www.pentaho.com
 *
 * Use of this software is governed by the Business Source License included
 * in the LICENSE.TXT file.
 *
 * Change Date: 2029-07-20
 ******************************************************************************/


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

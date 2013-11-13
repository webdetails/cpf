package pt.webdetails.cpf.messaging;

import java.io.IOException;

import org.codehaus.jackson.JsonGenerationException;
import org.codehaus.jackson.JsonGenerator;

public class SimpleJsonResult implements JsonGeneratorSerializable {

  public static String OK_FIELD = "status";
  public static String PAYLOAD_FIELD = "result";

  private boolean status;
  private JsonGeneratorSerializable payload;

  public SimpleJsonResult( boolean success, JsonGeneratorSerializable payload ) {
    this.status = success;
    this.payload = payload;
  }

  public SimpleJsonResult( boolean success, final String contents ) {
    this.status = success;
    this.payload = new JsonGeneratorSerializable () {
      public void writeToGenerator( JsonGenerator jsonGenerator ) throws JsonGenerationException, IOException {
        jsonGenerator.writeString(contents);
      }
    };
  }

  public void writeToGenerator( JsonGenerator jsonGenerator ) throws JsonGenerationException, IOException {
    jsonGenerator.writeStartObject();
    jsonGenerator.writeBooleanField( OK_FIELD, status );
    jsonGenerator.writeFieldName( PAYLOAD_FIELD );
    payload.writeToGenerator( jsonGenerator );
    jsonGenerator.writeEndObject();
  }

}

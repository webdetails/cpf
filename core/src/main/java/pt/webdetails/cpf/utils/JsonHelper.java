package pt.webdetails.cpf.utils;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.OutputStream;

import org.codehaus.jackson.JsonEncoding;
//import org.apache.commons.logging.Log;
//import org.apache.commons.logging.LogFactory;
import org.codehaus.jackson.JsonFactory;
import org.codehaus.jackson.JsonGenerationException;
import org.codehaus.jackson.JsonGenerator;

import pt.webdetails.cpf.messaging.JsonGeneratorSerializable;

public class JsonHelper {

  public static String getJsonString( JsonGeneratorSerializable jsonSerializable ) throws IOException {

    if ( jsonSerializable == null ) {
      return null;
    }

    ByteArrayOutputStream output = new ByteArrayOutputStream();
    writeJson( jsonSerializable, output );
    return output.toString( CharsetHelper.getEncoding() );
  }

  public static void writeJson ( JsonGeneratorSerializable jsonSerializable, OutputStream out ) throws IOException {
    JsonGenerator jGen = getJsonFactory().createJsonGenerator( out, JsonEncoding.UTF8 );
    jsonSerializable.writeToGenerator( jGen );
    jGen.flush();
  }

  public static JsonGeneratorSerializable toJson( final boolean bool ) {
    return new JsonGeneratorSerializable() {
      public void writeToGenerator( JsonGenerator jsonGenerator ) throws JsonGenerationException, IOException {
        jsonGenerator.writeBoolean( bool );
      }
    };
  }
  public static JsonGeneratorSerializable toJson( final long number ) {
    return new JsonGeneratorSerializable() {
      public void writeToGenerator( JsonGenerator jsonGenerator ) throws JsonGenerationException, IOException {
        jsonGenerator.writeNumber( number );
      }
    };
  }
  public static JsonGeneratorSerializable toJson( final double number ) {
    return new JsonGeneratorSerializable() {
      public void writeToGenerator( JsonGenerator jsonGenerator ) throws JsonGenerationException, IOException {
        jsonGenerator.writeNumber( number );
      }
    };
  }

  public static JsonFactory getJsonFactory() {
    return new JsonFactory();
  }

  public static JsonGeneratorSerializable toJson( final Iterable<? extends JsonGeneratorSerializable> elements ) {
    return new JsonGeneratorSerializable() {
      public void writeToGenerator( JsonGenerator jsonGenerator ) throws JsonGenerationException, IOException {
        jsonGenerator.writeStartArray();
        for ( JsonGeneratorSerializable element : elements ) {
          element.writeToGenerator( jsonGenerator );
        }
        jsonGenerator.writeEndArray();
      }
    };
  }
}

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


package pt.webdetails.cpf.utils;

import com.fasterxml.jackson.core.JsonEncoding;
import com.fasterxml.jackson.core.JsonFactory;
import com.fasterxml.jackson.core.JsonGenerationException;
import com.fasterxml.jackson.core.JsonGenerator;
import pt.webdetails.cpf.messaging.JsonGeneratorSerializable;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.OutputStream;

//import org.apache.commons.logging.Log;
//import org.apache.commons.logging.LogFactory;

public class JsonHelper {

  public static String getJsonString( JsonGeneratorSerializable jsonSerializable ) throws IOException {

    if ( jsonSerializable == null ) {
      return null;
    }

    ByteArrayOutputStream output = new ByteArrayOutputStream();
    writeJson( jsonSerializable, output );
    return output.toString( CharsetHelper.getEncoding() );
  }

  public static void writeJson( JsonGeneratorSerializable jsonSerializable, OutputStream out ) throws IOException {
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

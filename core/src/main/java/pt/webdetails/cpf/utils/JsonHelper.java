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

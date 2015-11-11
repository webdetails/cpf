package pt.webdetails.cpf.messaging;

import org.codehaus.jackson.JsonGenerationException;
import org.codehaus.jackson.JsonGenerator;

import java.io.IOException;

public interface JsonGeneratorSerializable {

  void writeToGenerator( JsonGenerator jsonGenerator ) throws JsonGenerationException, IOException;

}

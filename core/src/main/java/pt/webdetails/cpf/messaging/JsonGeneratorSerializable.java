package pt.webdetails.cpf.messaging;

import java.io.IOException;

import org.codehaus.jackson.JsonGenerationException;
import org.codehaus.jackson.JsonGenerator;

public interface JsonGeneratorSerializable {

  void writeToGenerator(JsonGenerator jsonGenerator) throws JsonGenerationException, IOException;

}

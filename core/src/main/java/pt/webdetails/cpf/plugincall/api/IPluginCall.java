package pt.webdetails.cpf.plugincall.api;

import java.io.InputStream;
import java.util.Map;

public interface IPluginCall {

  /**
   * Equivalent to run(params) + toString(getResult())
   */
  String call( Map<String, String[]> params ) throws Exception;

  void run( Map<String, String[]> params ) throws Exception;

  InputStream getResult();

  boolean exists(); //TODO: baah
}

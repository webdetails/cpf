package pt.webdetails.cpf.utils;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import org.json.JSONException;

import pt.webdetails.cpf.messaging.JsonSerializable;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import org.apache.commons.io.IOUtils;

/**
 * User: diogomariano
 * Date: 10/09/13
 */
public class PluginIOUtils {
  private static PluginIOUtils instance;
  private static final String ENCODING = CharsetHelper.getEncoding();

  private static Log logger;

  private static String WRITE_MESSAGE = "Error writing output";
  private static String JSON_MESSAGE = "Error getting Json";

  public PluginIOUtils getInstance(){
    if (instance == null){
      instance = new PluginIOUtils();
      instance.logger = LogFactory.getLog(this.getClass());
    }

    return instance;
  }

  private static String getEncoding() { return ENCODING; }

  public static void writeOut(OutputStream out, String data){
    try {
      IOUtils.write(data, out, getEncoding());
    } catch (IOException ex){
      logger.error(WRITE_MESSAGE, ex);
    }
  }

  public static void writeOut(OutputStream out, InputStream data){
    try {
      IOUtils.copy(data, out);
    } catch (IOException ex){
      logger.error(WRITE_MESSAGE, ex);
    }
  }

  public static void writeOut(OutputStream out, JsonSerializable data){
    try {
      IOUtils.write(data.toJSON().toString(), out, getEncoding());
    } catch (IOException ex){
      logger.error(WRITE_MESSAGE, ex);
    } catch (JSONException ex){
      logger.error(JSON_MESSAGE, ex);
    }
  }

}

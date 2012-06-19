package pt.webdetails.cpf.messaging;

import java.util.concurrent.Callable;
import java.util.concurrent.FutureTask;
import java.util.concurrent.TimeUnit;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.json.JSONObject;

import pt.webdetails.cpf.InterPluginCall;
import pt.webdetails.cpf.JsonPluginCall;
import pt.webdetails.cpf.RestRequestHandler.HttpMethod;
import pt.webdetails.cpf.messaging.PluginEvent;

/**
 * Inefficient but thread-safe..
 */
public class EventPublisher {
  
  protected static final long TIMEOUT = 360;//TODO: 
  private static Log logger = LogFactory.getLog(EventPublisher.class);
  
  private static Boolean cdvExists;
  
  public synchronized static boolean canPush(){
    if(cdvExists == null){
      InterPluginCall checkExistence = new InterPluginCall(InterPluginCall.CDV, "whatever");
      cdvExists = checkExistence.pluginExists();
    }
    return cdvExists;
  }

  private EventPublisher() {}
  
  public static EventPublisher getPublisher(){
    return new EventPublisher();
  }
  
  public void publish(final PluginEvent event){
    
    if(!canPush()){
      logger.warn("publishToCDV: plugin not available");
      return;
    }
    
    Thread publishThread = new Thread(){
      @Override
      public void run() {
        
      FutureTask<JSONObject> toRun = new FutureTask<JSONObject>(new Callable<JSONObject>(){

          @Override
          public JSONObject call() throws Exception {
            JsonPluginCall call = new JsonPluginCall(InterPluginCall.CDV, "warnings");
            return call.call(event.toJSON());
          }
          
        });
        
        try {
          toRun.run();
          JSONObject result = toRun.get(TIMEOUT, TimeUnit.SECONDS);
          logger.info("pushed event " + result);//TODO
        } catch (Exception e){
          logger.error("push failed:", e);
        }

      }
    };
    publishThread.start();
    
  }
  
}

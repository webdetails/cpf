/*!
* Copyright 2002 - 2017 Webdetails, a Hitachi Vantara company.  All rights reserved.
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

package pt.webdetails.cpf.messaging;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import pt.webdetails.cpf.*;
import pt.webdetails.cpf.plugin.CorePlugin;

import java.util.concurrent.*;


/**
 * Inefficient but thread-safe..
 */
public class EventPublisher implements IEventPublisher {
  
  protected static final long TIMEOUT = CpfProperties.getInstance().getLongProperty("messaging.publishTimeout", 11); 
  private static final boolean LOG_PUBLISH = false;//CpfProperties.getInstance().getBooleanProperty("messaging.logPublish", false);
  private static Log logger = LogFactory.getLog(EventPublisher.class);
  private static ThreadPoolExecutor executor =
      new ThreadPoolExecutor(0, CpfProperties.getInstance().getIntProperty("messaging.maxThreads", 1), 
          CpfProperties.getInstance().getLongProperty("messaging.publishTimeout", 11), TimeUnit.SECONDS, 
          new ArrayBlockingQueue<Runnable>(CpfProperties.getInstance().getIntProperty("messaging.queueSize", 3), true) ,
          new ThreadPoolExecutor.DiscardOldestPolicy()); 
      
//  static {
//    executor.allowCoreThreadTimeOut(true);
//  }
  
  private static Boolean cdvExists;
  
  public synchronized static boolean canPush(){
    if(cdvExists == null){
      cdvExists = PluginEnvironment.env().getPluginCall( CorePlugin.CDV.getId(), null, "whatever" ).exists();
      //cdvExists = new InterPluginCall(CorePlugin.CDV.getId(), "", "whatever").exists();
      //cdvExists = new PentahoInterPluginCall(CorePlugin.CDV, "whatever").pluginExists();
    }
    return cdvExists;
  }

  public EventPublisher() {}
  
  
  public static EventPublisher getPublisher(){
    return new EventPublisher();
  }
  
  public void publish(final PluginEvent event){
    
    if(!canPush()){
      logger.warn("publishToCDV: plugin not available, ignoring request");
      return;
    }
    
    Runnable toRun = LOG_PUBLISH ? getPublishAndLogTask(event) :  getPublishTask(event);
    executor.execute(toRun);
  }
  
  private Runnable getPublishAndLogTask(final PluginEvent event){
    Runnable publishAndLog = new Runnable(){
      @Override
      public void run() {
        
      FutureTask<Result> toRun = getPublishTask(event);
        
        try {
          executor.execute(toRun);
          Result result = toRun.get(TIMEOUT, TimeUnit.SECONDS);
          String msg = "[" + event.getPlugin() + "] pushed event " + result;
          switch(result.getStatus()){
            case OK:
              logger.info(msg);
              break;
            case ERROR:
              logger.error(msg);
              break;
          }
        } catch (Exception e){
          toRun.cancel(true);
          logger.error("push failed: timeout reached: " + TIMEOUT + " seconds");
        }

      }
    };
    return publishAndLog;
  }

  private FutureTask<Result> getPublishTask(final PluginEvent event){
    return new FutureTask<Result>(new Callable<Result>(){

      @Override
      public Result call() throws Exception {
        JsonPluginCall call = new JsonPluginCall(InterPluginCall.CDV, "warnings");
        return new Result(call.call(event.toJSON()));
      }
      
    });
  }
  
}

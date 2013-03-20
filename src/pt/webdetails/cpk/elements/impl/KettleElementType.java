
/* This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this file,
 * You can obtain one at http://mozilla.org/MPL/2.0/. */
package pt.webdetails.cpk.elements.impl;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.JOptionPane;
import net.sf.saxon.functions.Trace;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.pentaho.di.core.exception.KettleTransException;
import org.pentaho.di.core.exception.KettleXMLException;
import org.pentaho.platform.api.engine.IParameterProvider;
import pt.webdetails.cpf.SimpleContentGenerator;
import pt.webdetails.cpk.elements.AbstractElementType;
import pt.webdetails.cpk.elements.ElementInfo;
import pt.webdetails.cpk.elements.IElement;
import org.pentaho.di.core.Result;
import org.pentaho.di.core.exception.KettleException;
import org.pentaho.di.core.exception.KettleJobException;
import org.pentaho.di.core.util.EnvUtil;
import org.pentaho.di.job.Job;
import org.pentaho.di.job.JobMeta;
import org.pentaho.di.trans.Trans;
import org.pentaho.di.trans.TransMeta;
import org.pentaho.platform.engine.core.system.PentahoSystem;
import pt.webdetails.cpf.utils.PluginUtils;
/**
 *
 * @author Pedro Alves<pedro.alves@webdetails.pt>
 */
public class KettleElementType extends AbstractElementType {
    

    protected Log logger = LogFactory.getLog(this.getClass());

    public KettleElementType() {
    }

    @Override
    public String getType() {
        return "Kettle";
    }


    @Override
    public void processRequest(Map<String, IParameterProvider> parameterProviders, IElement element) {
        
        
        String kettlePath = element.getLocation();
        String kettleFilename = element.getName();
        String extension = new String();
        String operation = new String();
        String stepName = "OUTPUT";//Value by default
        
        Result result = new Result();
        
        logger.info("Kettle file path: "+kettlePath);
        
        //This gets all the params inserted in the URL
        Iterator getCustomParams = parameterProviders.get("request").getParameterNames();
        HashMap<String,String> customParams = new HashMap<String,String>();
        String key = new String();
        String value = new String();
        String paramPrefix = "param";
        
        while(getCustomParams.hasNext()){
            key = getCustomParams.next().toString();
            if(key.startsWith(paramPrefix)){
                value = parameterProviders.get("request").getParameter(key).toString();
                customParams.put(key.substring(5), value);
                logger.info("Argument '"+key.substring(5) +"' with value '"+value+"' stored on the map.");
            }else{
                logger.info("The parameter provided does not have a valid prefix. Try 'param"+key+"'.\n"
                        + "The '"+key+" parameter will be stored without the 'param' prefix as it is just a signature.");
            }
        }
        
        
        String fileTypeInfo = "Kettle file type is: ";
        if(kettlePath.endsWith(".ktr")){
            operation = "Transformation";
            logger.info(fileTypeInfo+operation);
            extension = ".ktr";
        }else if(kettlePath.endsWith(".kjb")){
            operation = "Job";
            logger.info(fileTypeInfo+operation);
            extension = ".kjb";
        }else{
            logger.warn("File extension unknown!");
        }
       
        //These conditions will treat the different types of kettle operations
        if(operation.equalsIgnoreCase("transformation")){
            try {
                logger.info("Starting Kettle "+operation.toLowerCase()+"...");

                TransMeta transformationMeta = new TransMeta(kettlePath);
                Trans transformation = new Trans(transformationMeta);

                /*
                 * Loading parameters, if there are any.
                 */
                boolean parametersExist = false;
                for(String arg : customParams.keySet()){
                    parametersExist = true;
                    transformation.setParameterValue(arg, customParams.get(arg));
                    logger.info("'"+arg+"' with value '"+customParams.get(arg)+"' loaded into "+operation+" parameters.");
                }
                if(parametersExist){
                    transformation.activateParameters();
                    logger.info(operation+" parameters loaded with success!");
                }else{
                    logger.info("No parameters found for "+operation+".");
                }
                
                
                transformation.execute(null);
                transformation.waitUntilFinished();
                result = transformation.getResult();



                logger.info(operation+" complete");
              }
              catch ( KettleException e ) {
                // TODO Put your exception-handling code here.
                logger.warn(""+e);

                if(e.toString().contains("Premature end of file")){
                    result.setLogText("The file ended prematurely. Please check the "+kettleFilename+extension+" file.");
                }else{
                    throw new UnsupportedOperationException(e.toString());
                }
              }
        }else if (operation.equalsIgnoreCase("job")){
            try {
                logger.info("Starting Kettle "+operation.toLowerCase()+"...");
                
                JobMeta jobMeta = new JobMeta(kettlePath, null);
                Job job = new Job(null, jobMeta);
                
                /*
                 * Loading parameters, if there are any.
                 */
                boolean parametersExist = false;
                for(String arg : customParams.keySet()){
                    parametersExist = true;
                    job.setParameterValue(arg, customParams.get(arg));
                    logger.info("'"+arg+"' with value '"+customParams.get(arg)+"' loaded into "+operation+" parameters.");
                }
                if(parametersExist){
                    job.activateParameters();
                    logger.info(operation+" parameters loaded with success!");
                }else{
                    logger.info("No parameters found for "+operation+".");
                }
                

                job.start();
                job.waitUntilFinished();
                result = job.getResult();
                
                logger.info(operation+" complete");
            } catch (KettleException ex) {
                Logger.getLogger(KettleElementType.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
        
        //Will use this to show the result (Tests)
        String resultMessage = new String();
        
        resultMessage = operation+" status:"+result;
        
        throw new UnsupportedOperationException(resultMessage);
        
    }

    @Override
    protected ElementInfo createElementInfo() {
        return new ElementInfo(SimpleContentGenerator.MimeType.JSON, 0);
    }
}

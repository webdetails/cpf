
/* This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this file,
 * You can obtain one at http://mozilla.org/MPL/2.0/. */
package pt.webdetails.cpk.elements.impl;

import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.JOptionPane;
import net.sf.saxon.functions.Trace;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.pentaho.di.core.exception.KettleTransException;
import org.pentaho.platform.api.engine.IParameterProvider;
import pt.webdetails.cpf.SimpleContentGenerator;
import pt.webdetails.cpk.elements.AbstractElementType;
import pt.webdetails.cpk.elements.ElementInfo;
import pt.webdetails.cpk.elements.IElement;
import org.pentaho.di.core.Result;
import org.pentaho.di.core.exception.KettleException;
import org.pentaho.di.core.util.EnvUtil;
import org.pentaho.di.job.Job;
import org.pentaho.di.job.JobMeta;
import org.pentaho.di.trans.Trans;
import org.pentaho.di.trans.TransMeta;
import org.pentaho.platform.engine.core.system.PentahoSystem;
/**
 *
 * @author Pedro Alves<pedro.alves@webdetails.pt>
 */
public class KettleElementType extends AbstractElementType {
    
    final String PLUGIN_NAME = "cvb";

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
        
        Result result = new Result();
        
        logger.info("Kettle file path: "+kettlePath);
        
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
        
        //Just going to return a default transformation result for now (if there is one!)
       
        
        if(operation.equalsIgnoreCase("tranformation")){
            try {
                logger.info("Starting Kettle "+operation.toLowerCase()+"...");

                TransMeta transformationMeta = new TransMeta(kettlePath);
                Trans transformation = new Trans(transformationMeta);


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
        }
        
        //Will use this to show the logText for now (Test purposes)
        throw new UnsupportedOperationException("Result "+result);
        
    }

    @Override
    protected ElementInfo createElementInfo() {
        return new ElementInfo(SimpleContentGenerator.MimeType.JSON, 0);
    }
}

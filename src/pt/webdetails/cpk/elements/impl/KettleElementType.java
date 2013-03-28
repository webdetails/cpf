
/* This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this file,
 * You can obtain one at http://mozilla.org/MPL/2.0/. */
package pt.webdetails.cpk.elements.impl;

import java.io.IOException;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.codehaus.jackson.JsonGenerationException;
import org.codehaus.jackson.JsonNode;
import org.codehaus.jackson.JsonProcessingException;
import org.codehaus.jackson.map.JsonMappingException;
import org.json.JSONException;
import org.pentaho.platform.api.engine.IParameterProvider;
import pt.webdetails.cpf.SimpleContentGenerator;
import pt.webdetails.cpk.elements.AbstractElementType;
import pt.webdetails.cpk.elements.ElementInfo;
import pt.webdetails.cpk.elements.IElement;
import org.pentaho.di.core.Result;
import org.pentaho.di.core.exception.KettleException;
import org.pentaho.di.core.exception.KettleStepException;
import org.pentaho.di.core.exception.KettleXMLException;
import org.pentaho.di.core.parameters.UnknownParamException;
import org.pentaho.di.core.row.RowMetaInterface;
import org.pentaho.di.job.Job;
import org.pentaho.di.job.JobMeta;
import org.pentaho.di.trans.Trans;
import org.pentaho.di.trans.TransMeta;
import org.pentaho.di.trans.step.RowAdapter;
import org.pentaho.di.trans.step.StepInterface;
import pt.webdetails.cpf.Util;
import org.codehaus.jackson.map.ObjectMapper;
import org.json.simple.JSONObject;
import pt.webdetails.cpf.utils.PluginUtils;

/**
 *
 * @author Pedro Alves<pedro.alves@webdetails.pt>
 */
public class KettleElementType extends AbstractElementType {

    protected Log logger = LogFactory.getLog(this.getClass());
    private static final String PARAM_PREFIX = "param";
    private String stepName;
    private ConcurrentHashMap<String, TransMeta> transMetaStorage = new ConcurrentHashMap<String, TransMeta>();//Stores the metadata of the ktr files. [Key=path]&[Value=transMeta]
    private ConcurrentHashMap<String, JobMeta> jobMetaStorage = new ConcurrentHashMap<String, JobMeta>();//Stores the metadata of the kjb files. [Key=path]&[Value=jobMeta]

    public KettleElementType() {
        transMetaStorage = new ConcurrentHashMap<String, TransMeta>();//Stores the metadata of the ktr files. [Key=path]&[Value=transMeta]
        jobMetaStorage = new ConcurrentHashMap<String, JobMeta>();//Stores the metadata of the kjb files. [Key=path]&[Value=jobMeta]
    }


    @Override
    public String getType() {
        return "Kettle";
    }

    @Override
    public void processRequest(Map<String, IParameterProvider> parameterProviders, IElement element) {


        String kettlePath = element.getLocation();
        String kettleFilename = element.getName();
        String extension = null;
        String operation = null;
        stepName = "OUTPUT";



        logger.info("Processing request for: " + kettlePath);

        //This gets all the params inserted in the URL
        Iterator getParams = parameterProviders.get("request").getParameterNames();
        HashMap<String, String> customParams = new HashMap<String, String>();
        String key;
        String value;

        while (getParams.hasNext()) {
            key = getParams.next().toString();
            if (key.startsWith(PARAM_PREFIX)) {
                value = parameterProviders.get("request").getParameter(key).toString();
                customParams.put(key.substring(5), value);
                logger.debug("Argument '" + key.substring(5) + "' with value '" + value + "' stored on the map.");
            
            } else if(key.startsWith("stepname")){
                value = parameterProviders.get("request").getParameter(key).toString();
                logger.debug("Step name changed from '"+stepName+"' to '"+value+"'");
                stepName = value;
            } else {
                // skip
            }
        }


        if (kettlePath.endsWith(".ktr")) {
            operation = "Transformation";
            extension = ".ktr";
        } else if (kettlePath.endsWith(".kjb")) {
            operation = "Job";
            extension = ".kjb";
        } else {
            logger.warn("File extension unknown: " + kettlePath);
        }


        //These conditions will treat the different types of kettle operations
        KettleOutput kettleOutput = null;
        try {
            if (operation.equalsIgnoreCase("transformation")) {
                kettleOutput = new KettleOutput(parameterProviders);
                kettleOutput.setResult(executeTransformation(kettlePath, customParams, kettleOutput));
            } else if(operation.equalsIgnoreCase("job")) {
                kettleOutput = new KettleOutput(parameterProviders);
                kettleOutput.setResult(executeJob(kettlePath, customParams));
            }

        } catch (KettleException e) {

            logger.error(" Error executing kettle file " + kettleFilename + ": " + Util.getExceptionDescription(e));

        }


        logger.info(operation + " " + kettlePath + " complete: " + kettleOutput.getResult());



        if (kettleOutput != null) {
                if(operation.equalsIgnoreCase("Transformation")){
                    kettleOutput.RowsToStream();
                }else if(operation.equalsIgnoreCase("Job")){
                    kettleOutput.ResultJson();
                }
        }
        
        
    }

    /**
     * Executes a transformation
     *
     * @param kettlePath Path to the ktr
     * @param customParams parameters to be passed to the transformation
     * @return Result
     * @throws KettleXMLException
     * @throws UnknownParamException
     * @throws KettleException
     */
    private Result executeTransformation(final String kettlePath, HashMap<String, String> customParams, final KettleOutput kettleOutput) throws KettleXMLException, UnknownParamException, KettleException {


       


        TransMeta transformationMeta = new TransMeta();

        if (transMetaStorage.containsKey(kettlePath)) {
            logger.debug("Existent metadata found for " + kettlePath);
            transformationMeta = transMetaStorage.get(kettlePath);
        } else {
            logger.debug("No existent metadata found for " + kettlePath);
            transformationMeta = new TransMeta(kettlePath);
            transMetaStorage.put(kettlePath, transformationMeta);
            logger.debug("Added metadata to the storage.");
        }

        Trans transformation = new Trans(transformationMeta);


        /*
         * Loading parameters, if there are any.
         */
        if (customParams.size() > 0) {
            for (String arg : customParams.keySet()) {
                
                transformation.setParameterValue(arg, customParams.get(arg));
                
            }
            transformation.activateParameters();

        }
        transformation.prepareExecution(null);

        StepInterface step = transformation.findRunThread(stepName);
        transformation.startThreads();


        step.addRowListener(new RowAdapter() {

            @Override
            public void rowReadEvent(RowMetaInterface rowMeta, Object[] row) throws KettleStepException {
                kettleOutput.storeRow(row,rowMeta);
            }

            @Override
            public void rowWrittenEvent(RowMetaInterface rowMeta, Object[] row) throws KettleStepException {
                // TODO
            }
        });

        transformation.waitUntilFinished();
        
        
        
        return transformation.getResult();
    }

    /**
     *Executes a Job
     * 
     * @param kettlePath Path to the kjb
     * @param customParams parameters to be passed to the job
     * @return
     * @throws UnknownParamException
     * @throws KettleException
     * @throws KettleXMLException
     */
    private Result executeJob(String kettlePath, HashMap<String, String> customParams) throws UnknownParamException, KettleException, KettleXMLException {


        JobMeta jobMeta = new JobMeta();


        if (jobMetaStorage.containsKey(kettlePath)) {
            logger.debug("Existent metadata found for " + kettlePath);
            jobMeta = jobMetaStorage.get(kettlePath);
        } else {
            logger.debug("No existent metadata found for " + kettlePath);
            jobMeta = new JobMeta(kettlePath, null);
            jobMetaStorage.put(kettlePath, jobMeta);
            logger.debug("Added metadata to the storage.");
        }

        Job job = new Job(null, jobMeta);

        /*
         * Loading parameters, if there are any.
         */
        if (customParams.size() > 0) {
            for (String arg : customParams.keySet()) {
                job.setParameterValue(arg, customParams.get(arg));
            }
            job.activateParameters();

        }
        job.start();
        job.waitUntilFinished();
        return job.getResult();
    }

    @Override
    protected ElementInfo createElementInfo() {
        return new ElementInfo(SimpleContentGenerator.MimeType.JSON, 0);
    }

    private class KettleOutput {

        ArrayList<Object[]> rows;
        ArrayList<RowMetaInterface> rowsMeta;
        Result result = null;
        ObjectMapper mapper;
        OutputStream out;

        KettleOutput(Map<String, IParameterProvider> parameterProviders) {
            init(parameterProviders);
        }

        private void init(Map<String, IParameterProvider> parameterProviders) {
            rows = new ArrayList<Object[]>();
            rowsMeta = new ArrayList<RowMetaInterface>();
            mapper = new ObjectMapper();
            try {
                out = PluginUtils.getInstance().getResponseOutputStream(parameterProviders);
            } catch (IOException ex) {
                Logger.getLogger("Something went wrong on the KettleOutput class initialization.");
            }
        }

        public void storeRow(Object[] row) {
            rows.add(row);
        }

        public void storeRow(Object[] row, RowMetaInterface rowMeta) {
            rows.add(row);
            rowsMeta.add(rowMeta);
        }

        public ArrayList<Object[]> getRows() {
            return rows;
        }

        public ArrayList<RowMetaInterface> getRowsMeta() {
            return rowsMeta;
        }
        
        public void setResult(Result r){
            this.result = r;
        }
        
        public Result getResult(){
            return this.result;
        }
        

        public void RowsToStream(){
            try {
                for(Object row : rows) {
                    Object[] rowAsArray = (Object[])row;
                    for (int i=0; i < rowAsArray.length; i++) {
                        if(rowAsArray!=null){
                            Object cell = rowAsArray[i];
                            if(cell!=null){
                                out.write(cell.toString().getBytes("UTF-8"));
                            }
                        }
                    }
                }
            } catch (IOException ex) {
                Logger.getLogger(KettleElementType.class.getName()).log(Level.SEVERE, null, ex);
            }

        }
        
        public void ResultJson(){
            try {
                mapper.writeValue(out, result);
            } catch (IOException ex) {
                Logger.getLogger(KettleElementType.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
        

        
        /*
         * To implement later on...
         * public void removeRow(){}
         */
    }
}

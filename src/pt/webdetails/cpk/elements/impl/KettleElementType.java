
/* This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this file,
 * You can obtain one at http://mozilla.org/MPL/2.0/. */
package pt.webdetails.cpk.elements.impl;

import java.lang.reflect.Constructor;
import pt.webdetails.cpk.elements.impl.kettleOutputs.KettleOutput;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
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
import pt.webdetails.cpf.utils.PluginUtils;
import pt.webdetails.cpk.elements.impl.kettleOutputs.IKettleOutput;

/**
 *
 * @author Pedro Alves<pedro.alves@webdetails.pt>
 */
public class KettleElementType extends AbstractElementType {

    public static enum KettleType {

        JOB, TRANSFORMATION
    };
    protected Log logger = LogFactory.getLog(this.getClass());
    private static final String PARAM_PREFIX = "param";
    private ConcurrentHashMap<String, TransMeta> transMetaStorage = new ConcurrentHashMap<String, TransMeta>();//Stores the metadata of the ktr files. [Key=path]&[Value=transMeta]
    private ConcurrentHashMap<String, JobMeta> jobMetaStorage = new ConcurrentHashMap<String, JobMeta>();//Stores the metadata of the kjb files. [Key=path]&[Value=jobMeta]
    private String stepName = "OUTPUT";
    
    
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
        

        logger.debug("Processing request for: " + kettlePath);

        //This gets all the params inserted in the URL
        Iterator customParamsIter = PluginUtils.getInstance().getRequestParameters(parameterProviders).getParameterNames();
        HashMap<String, String> customParams = new HashMap<String, String>();
        String key, value;

        while (customParamsIter.hasNext()) {
            key = customParamsIter.next().toString();
            if (key.startsWith(PARAM_PREFIX)) {
                value = parameterProviders.get("request").getParameter(key).toString();
                customParams.put(key.substring(5), value);
                logger.debug("Argument '" + key.substring(5) + "' with value '" + value + "' stored on the map.");
            }
        }

        /*
         *  There are a few different types of kettle output processing. 
         *  They can be infered or specified from a request parameter: kettleOutput
         * 
         *  1. ResultOnly - we'll discard the output and print statistics only
         *  2. ResultFiles - Download the files we have as result filenames
         *  3. Json - Json output of the resultset
         *  4. csv - CSV output of the resultset
         *  5. SingleCell - We'll get the first line, first row
         *  6. Infered - Infering
         * 
         *  If nothing specified, the behavior will be:
         *  * Jobs and Transformations with result filenames: ResultFiles
         *  * Without filenames:
         *      * Jobs: ResultOnly
         *      * Transformations:
         *          * Just one cell: SingleCell
         *          * Regular resultset: Json
         * 
         *  By complexity:
         *      These don't require rowListener:
         *  1. ResultOnly
         *  2. ResultFiles
         *  
         *      These do:
         *  3. SingleCell
         *  4. Json
         *  5. CSV
         *  6. Infered
         */

        //These conditions will treat the different types of kettle operations

        IKettleOutput kettleOutput = null;
        String clazz = PluginUtils.getInstance().getRequestParameters(parameterProviders).getStringParameter("kettleOutput", "Infered") + "KettleOutput";

        try {
            // Get defined kettleOutput class name

            Constructor constructor = Class.forName("pt.webdetails.cpk.elements.impl.kettleOutputs." + clazz).getConstructor(Map.class);
            kettleOutput = (IKettleOutput) constructor.newInstance(parameterProviders);

        } catch (Exception ex) {
            logger.error("Error initializing Kettle output type " + clazz + ", reverting to KettleOutput: " + Util.getExceptionDescription(ex));
            kettleOutput = new KettleOutput(parameterProviders);
        }

        // Are we specifying a stepname?
        kettleOutput.setOutputStepName(PluginUtils.getInstance().getRequestParameters(parameterProviders).getStringParameter("stepName", stepName));

        Result result = null;

        try {

            if (kettlePath.endsWith(".ktr")) {
                kettleOutput.setKettleType(KettleType.TRANSFORMATION);
                result = executeTransformation(kettlePath, customParams, kettleOutput);
            } else if (kettlePath.endsWith(".kjb")) {
                kettleOutput.setKettleType(KettleType.JOB);
                result = executeJob(kettlePath, customParams);
            } else {
                logger.warn("File extension unknown: " + kettlePath);
            }

            kettleOutput.setResult(result);

        } catch (KettleException e) {

            logger.error(" Error executing kettle file " + kettleFilename + ": " + Util.getExceptionDescription(e));

        }

        logger.info(" Kettle " + kettlePath + " execution complete: " + kettleOutput.getResult());
        kettleOutput.processResult();


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
    private Result executeTransformation(final String kettlePath, HashMap<String, String> customParams, final IKettleOutput kettleOutput) throws KettleXMLException, UnknownParamException, KettleException {



        TransMeta transformationMeta = new TransMeta();

        if (transMetaStorage.containsKey(kettlePath)) {
            logger.debug("Existent metadata found for " + kettlePath);
            transformationMeta = transMetaStorage.get(kettlePath);
        } else {
            logger.debug("No existent metadata found for " + kettlePath);
            transformationMeta = new TransMeta(kettlePath);
            transMetaStorage.put(kettlePath, transformationMeta);
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

        if (kettleOutput.needsRowListener()) {

            step.addRowListener(new RowAdapter() {
                @Override
                public void rowReadEvent(RowMetaInterface rowMeta, Object[] row) throws KettleStepException {
                    kettleOutput.storeRow(row, rowMeta);
                }

                @Override
                public void rowWrittenEvent(RowMetaInterface rowMeta, Object[] row) throws KettleStepException {
                    // TODO
                }
            });
        }

        transformation.waitUntilFinished();
        return transformation.getResult();
    }

    /**
     * Executes a Job
     *
     * @param kettlePath Path to the kjb
     * @param customParams parameters to be passed to the job
     * @return
     * @throws UnknownParamException
     * @throws KettleException
     * @throws KettleXMLException
     */
    private Result executeJob(String kettlePath, HashMap<String, String> customParams) throws UnknownParamException, KettleException, KettleXMLException {


        JobMeta jobMeta;


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
        
        /*
         * Loading variables, if there are any.
         */
        
        job.start();
        job.waitUntilFinished();
        return job.getResult();

    }

    @Override
    protected ElementInfo createElementInfo() {
        return new ElementInfo(SimpleContentGenerator.MimeType.JSON, 0);
    }
}


/* This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this file,
 * You can obtain one at http://mozilla.org/MPL/2.0/. */
package pt.webdetails.cpk.elements.impl;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.pentaho.platform.api.engine.IParameterProvider;
import pt.webdetails.cpf.SimpleContentGenerator;
import pt.webdetails.cpk.elements.AbstractElementType;
import pt.webdetails.cpk.elements.ElementInfo;
import pt.webdetails.cpk.elements.IElement;
import org.pentaho.di.core.Result;
import org.pentaho.di.core.exception.KettleException;
import org.pentaho.di.core.exception.KettleXMLException;
import org.pentaho.di.core.parameters.UnknownParamException;
import org.pentaho.di.job.Job;
import org.pentaho.di.job.JobMeta;
import org.pentaho.di.trans.Trans;
import org.pentaho.di.trans.TransMeta;
import pt.webdetails.cpf.Util;

/**
 *
 * @author Pedro Alves<pedro.alves@webdetails.pt>
 */
public class KettleElementType extends AbstractElementType {

    protected Log logger = LogFactory.getLog(this.getClass());
    private static final String PARAM_PREFIX = "param";

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
        String extension;
        String operation;
        String stepName = "OUTPUT";//Value by default



        logger.info("Processing request for: " + kettlePath);

        //This gets all the params inserted in the URL
        Iterator getCustomParams = parameterProviders.get("request").getParameterNames();
        HashMap<String, String> customParams = new HashMap<String, String>();
        String key;
        String value;

        while (getCustomParams.hasNext()) {
            key = getCustomParams.next().toString();
            if (key.startsWith(PARAM_PREFIX)) {
                value = parameterProviders.get("request").getParameter(key).toString();
                customParams.put(key.substring(5), value);
                logger.debug("Argument '" + key.substring(5) + "' with value '" + value + "' stored on the map.");
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
            return;
        }

        Result result = null;

        //These conditions will treat the different types of kettle operations

        try {
            if (operation.equalsIgnoreCase("transformation")) {
                result = executeTransformation(kettlePath, customParams);
            } else {
                result = executeJob(kettlePath, customParams);
            }

        } catch (KettleException e) {

            logger.warn(" Error executing kettle file " + kettleFilename + ": " + Util.getExceptionDescription(e));

            if (e.toString().contains("Premature end of file")) {
                result.setLogText("The file ended prematurely. Please check the " + kettleFilename + extension + " file.");
            } else {
                throw new UnsupportedOperationException(e.toString());
            }
        }


        logger.info(operation + " " + kettlePath + " complete: " + result);


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
    private Result executeTransformation(String kettlePath, HashMap<String, String> customParams) throws KettleXMLException, UnknownParamException, KettleException {

        TransMeta transformationMeta = new TransMeta(kettlePath);
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
        transformation.execute(null);
        transformation.waitUntilFinished();
        return transformation.getResult();
    }

    /**
     *
     * @param kettlePath Path to the kjb
     * @param customParams parameters to be passed to the job
     * @return
     * @throws UnknownParamException
     * @throws KettleException
     * @throws KettleXMLException
     */
    private Result executeJob(String kettlePath, HashMap<String, String> customParams) throws UnknownParamException, KettleException, KettleXMLException {


        JobMeta jobMeta = new JobMeta(kettlePath, null);
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
}

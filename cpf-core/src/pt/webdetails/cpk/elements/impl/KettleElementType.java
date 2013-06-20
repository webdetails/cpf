
/* This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this file,
 * You can obtain one at http://mozilla.org/MPL/2.0/. */
package pt.webdetails.cpk.elements.impl;

import java.io.File;
import java.lang.reflect.Constructor;
import java.util.ArrayList;
import java.util.Collections;
import pt.webdetails.cpk.elements.impl.kettleOutputs.KettleOutput;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;
import java.util.concurrent.ConcurrentHashMap;
import org.apache.commons.lang.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import pt.webdetails.cpk.elements.AbstractElementType;
import pt.webdetails.cpk.elements.ElementInfo;
import pt.webdetails.cpk.elements.IElement;
import org.pentaho.di.core.Result;
import org.pentaho.di.core.ResultFile;
import org.pentaho.di.core.RowMetaAndData;
import org.pentaho.di.core.exception.KettleException;
import org.pentaho.di.core.exception.KettleStepException;
import org.pentaho.di.core.exception.KettleXMLException;
import org.pentaho.di.core.parameters.UnknownParamException;
import org.pentaho.di.core.row.RowMetaInterface;
import org.pentaho.di.job.Job;
import org.pentaho.di.job.JobEntryResult;
import org.pentaho.di.job.JobMeta;
import org.pentaho.di.trans.Trans;
import org.pentaho.di.trans.TransMeta;
import org.pentaho.di.trans.step.RowAdapter;
import org.pentaho.di.trans.step.StepInterface;
import pt.webdetails.cpf.Util;
import pt.webdetails.cpf.http.ICommonParameterProvider;
import pt.webdetails.cpf.session.IUserSession;
import pt.webdetails.cpf.utils.IPluginUtils;
import pt.webdetails.cpf.utils.MimeTypes;
import pt.webdetails.cpk.CpkEngine;
import pt.webdetails.cpk.elements.impl.kettleOutputs.IKettleOutput;
import pt.webdetails.cpk.elements.impl.kettleOutputs.cache.ResultCache;
import pt.webdetails.cpk.elements.impl.kettleOutputs.cache.ResultCacheKey;
import pt.webdetails.cpk.elements.impl.kettleOutputs.cache.ResultCacheManager;

/**
 *
 * @author Pedro Alves<pedro.alves@webdetails.pt>
 */
public class KettleElementType extends AbstractElementType {

    public static enum KettleType {

        JOB, TRANSFORMATION
    };
    protected Log logger = LogFactory.getLog(this.getClass());
    private static final String PARAM_PREFIX = "param",MIMETYPE = "MIMETYPE",CACHE = "CACHE", CACHEDURATION = "CACHEDURATION";
    private ConcurrentHashMap<String, TransMeta> transMetaStorage = new ConcurrentHashMap<String, TransMeta>();//Stores the metadata of the ktr files. [Key=path]&[Value=transMeta]
    private ConcurrentHashMap<String, JobMeta> jobMetaStorage = new ConcurrentHashMap<String, JobMeta>();//Stores the metadata of the kjb files. [Key=path]&[Value=jobMeta]
    private String stepName = "OUTPUT";
    private String mimeType = null;
    private String cpkSolutionSystemDir = null, cpkSolutionDir = null, cpkPluginDir = null, cpkPluginId = null, cpkPluginSystemDir = null;
    private final String CPK_SOLUTION_SYSTEM_DIR = "cpk.solution.system.dir",
            CPK_SOLUTION_DIR = "cpk.solution.dir",
            CPK_PLUGIN_DIR = "cpk.plugin.dir",
            CPK_PLUGIN_ID = "cpk.plugin.id",
            CPK_PLUGIN_SYSTEM_DIR = "cpk.plugin.system.dir",
            CPK_SESSION_USERNAME = "cpk.session.username",
            CPK_SESSION_ROLES = "cpk.session.roles";
    
    public KettleElementType(IPluginUtils plug) {

        super(plug);
        init(plug);
        transMetaStorage = new ConcurrentHashMap<String, TransMeta>();//Stores the metadata of the ktr files. [Key=path]&[Value=transMeta]
        jobMetaStorage = new ConcurrentHashMap<String, JobMeta>();//Stores the metadata of the kjb files. [Key=path]&[Value=jobMeta]
    }
    
    private void init(IPluginUtils pluginUtils){
        File pluginDirFile = pluginUtils.getPluginDirectory();
        
        cpkPluginDir = pluginDirFile.getAbsolutePath();
        cpkPluginSystemDir = pluginDirFile.getAbsolutePath()+File.separator+"system";
        cpkPluginId = pluginDirFile.getName();
        try{cpkSolutionDir = CpkEngine.getInstance().getEnvironment().getRepositoryAccess().getSolutionPath("");}catch(Exception e){}
        cpkSolutionSystemDir = pluginDirFile.getParentFile().getAbsolutePath();
        
        
    }

    @Override
    public String getType() {
        return "Kettle";
    }

    @Override
    public void processRequest(Map<String, ICommonParameterProvider> parameterProviders, IElement element) {


        String kettlePath = element.getLocation();
        String kettleFilename = element.getName();


        logger.debug("Processing request for: " + kettlePath);

        //This gets all the params inserted in the URL
        Iterator customParamsIter = pluginUtils.getRequestParameters(parameterProviders).getParameterNames();
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
        String clazz = pluginUtils.getRequestParameters(parameterProviders).getStringParameter("kettleOutput", "Infered") + "KettleOutput";

        try {
            // Get defined kettleOutput class name

            Constructor constructor = Class.forName("pt.webdetails.cpk.elements.impl.kettleOutputs." + clazz).getConstructor(Map.class, IPluginUtils.class);
            kettleOutput = (IKettleOutput) constructor.newInstance(parameterProviders, pluginUtils);

        } catch (Exception ex) {
            logger.error("Error initializing Kettle output type " + clazz + ", reverting to KettleOutput: " + Util.getExceptionDescription(ex));
            kettleOutput = new KettleOutput(parameterProviders, pluginUtils);
        }

        // Are we specifying a stepname?
        kettleOutput.setOutputStepName(pluginUtils.getRequestParameters(parameterProviders).getStringParameter("stepName", stepName));

        Result result = null;
        TreeMap<String,String> customParamsTreeMap = new TreeMap<String,String>();
        customParamsTreeMap.putAll(customParams);
        boolean bypassCache = false;
        
        if(parameterProviders.get("request").hasParameter("bypassCache")){
            if(parameterProviders.get("request").getParameter("bypassCache").equals("true")){
                bypassCache = true;
            }
        }
        
        
        KettleType kettleType = (kettlePath.endsWith(".ktr")) ? KettleType.TRANSFORMATION : KettleType.JOB;
        ResultCacheKey cacheKey = new ResultCacheKey(kettlePath ,customParamsTreeMap , kettleOutput.getOutputStepName());
        
        if(ResultCacheManager.getInstance().getResultCache(cacheKey) == null || bypassCache){
            try {

                if (kettlePath.endsWith(".ktr")) {
                    result = executeTransformation(kettlePath, customParams, kettleOutput);
                } else if (kettlePath.endsWith(".kjb")) {
                    result = executeJob(kettlePath, customParams, kettleOutput);
                } else {
                    logger.warn("File extension unknown: " + kettlePath);
                }

            } catch (KettleException e) {
                logger.error(" Error executing kettle file " + kettleFilename + ": " + Util.getExceptionDescription(e));
            }
        }else{
            logger.debug("Cache found for element \""+element.getName()+"\" with key \""+cacheKey.toString()+"\".\n\tUsing cache instead of running the "+ (kettlePath.endsWith(".ktr") ? KettleType.TRANSFORMATION: KettleType.JOB));
            ResultCache resultCache = ResultCacheManager.getInstance().getResultCache(cacheKey);
            result = resultCache.getResult();
            kettleOutput.setRows((ArrayList)resultCache.getRows());
            kettleOutput.setRowMeta(resultCache.getRowMeta());
            
        }
        
        kettleOutput.setKettleType(kettleType);
        kettleOutput.setResult(result);

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


        Result result = null;
        TransMeta transformationMeta = new TransMeta();
        ResultCacheKey cacheKey = null;

        if (transMetaStorage.containsKey(kettlePath)) {
            logger.debug("Existent metadata found for " + kettlePath);
            transformationMeta = transMetaStorage.get(kettlePath);
            transformationMeta.setResultRows(new ArrayList<RowMetaAndData>());
            transformationMeta.setResultFiles(new ArrayList<ResultFile>());
        } else {
            logger.debug("No existent metadata found for " + kettlePath);
            transformationMeta = new TransMeta(kettlePath);
            transMetaStorage.put(kettlePath, transformationMeta);
        }

        Trans transformation = new Trans(transformationMeta);
        IUserSession userSession = CpkEngine.getInstance().getEnvironment().getSessionUtils().getCurrentSession();
        if (userSession.getUserName() != null) {
            transformation.getTransMeta().setParameterValue(CPK_SESSION_USERNAME, userSession.getUserName());
            transformation.getTransMeta().setVariable(CPK_SESSION_USERNAME, userSession.getUserName());
        }

        String[] authorities = userSession.getAuthorities();
        if (authorities != null && authorities.length > 0) {
            transformation.getTransMeta().setParameterValue(CPK_SESSION_ROLES, StringUtils.join(authorities, ","));
            transformation.getTransMeta().setVariable(CPK_SESSION_ROLES, StringUtils.join(authorities, ","));
        }
        transformation.getTransMeta().setParameterValue(CPK_SOLUTION_SYSTEM_DIR, cpkSolutionSystemDir); // eg: project-X/solution/system
        transformation.getTransMeta().setParameterValue(CPK_SOLUTION_DIR, cpkSolutionDir); // eg: project-X/solution
        transformation.getTransMeta().setParameterValue(CPK_PLUGIN_DIR, cpkPluginDir); // eg: project-X/solution/system/cpk
        transformation.getTransMeta().setParameterValue(CPK_PLUGIN_ID, cpkPluginId); // eg: "cpk"
        transformation.getTransMeta().setParameterValue(CPK_PLUGIN_SYSTEM_DIR, cpkPluginSystemDir); //eg: project-X/solution/system/cpk/system        
        transformation.getTransMeta().setVariable(CPK_SOLUTION_SYSTEM_DIR, cpkSolutionSystemDir); // eg: project-X/solution/system
        transformation.getTransMeta().setVariable(CPK_SOLUTION_DIR, cpkSolutionDir); // eg: project-X/solution
        transformation.getTransMeta().setVariable(CPK_PLUGIN_DIR, cpkPluginDir); // eg: project-X/solution/system/cpk
        transformation.getTransMeta().setVariable(CPK_PLUGIN_ID, cpkPluginId); // eg: "cpk"
        transformation.getTransMeta().setVariable(CPK_PLUGIN_SYSTEM_DIR, cpkPluginSystemDir); //eg: project-X/solution/system/cpk/system
        
        /*
         * Loading parameters, if there are any.
         */
        if (customParams.size() > 0) {
            for (String arg : customParams.keySet()) {
                transformation.getTransMeta().setParameterValue(arg, customParams.get(arg));
                transformation.getTransMeta().setVariable(arg, customParams.get(arg));
            }    
        }
        
        transformation.copyParametersFrom(transformation.getTransMeta());
        transformation.copyVariablesFrom(transformation.getTransMeta());
        transformation.activateParameters();
               

        transformation.prepareExecution(null); //Get the step threads after this line
        StepInterface step = transformation.findRunThread(kettleOutput.getOutputStepName());

        if (kettleOutput.needsRowListener() && step != null) {

            step.addRowListener(new RowAdapter() {
                @Override
                public void rowWrittenEvent(RowMetaInterface rowMeta, Object[] row) throws KettleStepException {
                    kettleOutput.storeRow(row, rowMeta);
                }
            });

            transformation.startThreads(); // All the operations to get stepNames are suposed to be placed above this line
            transformation.waitUntilFinished();


            result = step.getTrans().getResult();


        } else {
            if(step == null){
                logger.error("The stepname \""+kettleOutput.getOutputStepName()+"\" does not exist!");
            }
            result = null;
        }

        

        setMimeType(transformation.getVariable(MIMETYPE), transformation.getParameterValue(MIMETYPE));
        
        if((transformation.getParameterValue(CACHE) != null && transformation.getParameterValue(CACHE).equalsIgnoreCase("true")) || (transformation.getVariable(CACHE) != null && transformation.getVariable(CACHE).equalsIgnoreCase("true"))){
            TreeMap<String,String> customParamsTreeMap = new TreeMap<String,String>();
            customParamsTreeMap.putAll(customParams);
            cacheKey = new ResultCacheKey(kettlePath, customParamsTreeMap, kettleOutput.getOutputStepName());
            
            int cacheDuration = 3600; //Default value
            String kettleCacheDuration = transformation.getParameterValue(CACHEDURATION);
            
            for(int i = 0 ; i < 2; i++){
                if(kettleCacheDuration != null && !kettleCacheDuration.isEmpty()){
                    cacheDuration = Integer.parseInt(kettleCacheDuration);
                }else{
                    kettleCacheDuration = transformation.getVariable(CACHEDURATION);
                }
            }
            logger.debug("Result stored in cache for "+cacheDuration+" seconds");
            ResultCacheManager.getInstance().putResult(cacheKey, result.clone(), kettleOutput.getRows(), kettleOutput.getRowMeta() , cacheDuration);
        }

        return result;
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
    private Result executeJob(String kettlePath, HashMap<String, String> customParams, IKettleOutput kettleOutput) throws UnknownParamException, KettleException, KettleXMLException {

        ResultCacheKey cacheKey = null;
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
        
        IUserSession userSession = CpkEngine.getInstance().getEnvironment().getSessionUtils().getCurrentSession();

        if (userSession.getUserName() != null) {
            job.getJobMeta().setParameterValue(CPK_SESSION_USERNAME, userSession.getUserName());
            job.getJobMeta().setVariable(CPK_SESSION_USERNAME, userSession.getUserName());
        }
        String[] authorities = userSession.getAuthorities();

        if (authorities != null && authorities.length > 0) {
            job.getJobMeta().setParameterValue(CPK_SESSION_ROLES, StringUtils.join(authorities, ","));
            job.getJobMeta().setVariable(CPK_SESSION_ROLES, StringUtils.join(authorities, ","));
        }
        
        job.getJobMeta().setParameterValue(CPK_SOLUTION_SYSTEM_DIR, cpkSolutionSystemDir); // eg: project-X/solution/system
        job.getJobMeta().setParameterValue(CPK_SOLUTION_DIR, cpkSolutionDir); // eg: project-X/solution
        job.getJobMeta().setParameterValue(CPK_PLUGIN_DIR, cpkPluginDir); // eg: project-X/solution/system/cpk
        job.getJobMeta().setParameterValue(CPK_PLUGIN_ID, cpkPluginId); // eg: "cpk"
        job.getJobMeta().setParameterValue(CPK_PLUGIN_SYSTEM_DIR, cpkPluginSystemDir); //eg: project-X/solution/system/cpk/system
        job.getJobMeta().setVariable(CPK_SOLUTION_SYSTEM_DIR, cpkSolutionSystemDir); // eg: project-X/solution/system
        job.getJobMeta().setVariable(CPK_SOLUTION_DIR, cpkSolutionDir); // eg: project-X/solution
        job.getJobMeta().setVariable(CPK_PLUGIN_DIR, cpkPluginDir); // eg: project-X/solution/system/cpk
        job.getJobMeta().setVariable(CPK_PLUGIN_ID, cpkPluginId); // eg: "cpk"
        job.getJobMeta().setVariable(CPK_PLUGIN_SYSTEM_DIR, cpkPluginSystemDir); //eg: project-X/solution/system/cpk/system

        /*
         * Loading parameters, if there are any. We'll pass them also as variables
         */
        if (customParams.size() > 0) {
            for (String arg : customParams.keySet()) {
                job.getJobMeta().setParameterValue(arg, customParams.get(arg));
                job.getJobMeta().setVariable(arg, customParams.get(arg));
            }
        }
        
        job.copyParametersFrom(jobMeta);
        job.copyVariablesFrom(job.getJobMeta());
        job.activateParameters();

        job.start();
        setMimeType(job.getVariable(MIMETYPE), job.getParameterValue(MIMETYPE));
        job.waitUntilFinished();
        Result result = job.getResult();
        JobEntryResult entryResult = null;

        List<JobEntryResult> jobEntryResultList = job.getJobEntryResults();
        if (jobEntryResultList.size() > 0) {
            for (int i = 0; i < jobEntryResultList.size(); i++) {
                entryResult = jobEntryResultList.get(i);
                if (entryResult != null) {
                    if (entryResult.getJobEntryName().equals(kettleOutput.getOutputStepName())) {
                        result = entryResult.getResult();
                        break;
                    }
                }
            }
        }
        result.setRows(new ArrayList<RowMetaAndData>());
        
        if((job.getParameterValue(CACHE) != null &&job.getParameterValue(CACHE).equalsIgnoreCase("true")) || (job.getVariable(CACHE) != null &&job.getVariable(CACHE).equalsIgnoreCase("true"))){
            TreeMap<String,String> customParamsTreeMap = new TreeMap<String,String>();
            customParamsTreeMap.putAll(customParams);
            cacheKey = new ResultCacheKey(kettlePath, customParamsTreeMap, kettleOutput.getOutputStepName());
            
            int cacheDuration = 3600; //Default value
            String kettleCacheDuration = job.getParameterValue(CACHEDURATION);
            
            for(int i = 0 ; i < 2; i++){
                if(kettleCacheDuration != null && !kettleCacheDuration.isEmpty()){
                    cacheDuration = Integer.parseInt(kettleCacheDuration);
                }else{
                    kettleCacheDuration = job.getVariable(CACHEDURATION);
                }
            }
            logger.debug("Result stored in cache for "+cacheDuration+" seconds");
            ResultCacheManager.getInstance().putResult(cacheKey, result.clone(), kettleOutput.getRows(), kettleOutput.getRowMeta(), cacheDuration);
        }

        return result;

    }

    private void setMimeType(String varValue, String paramValue) {
        this.mimeType = varValue;
        if (varValue == null || varValue.equals("")) {
            this.mimeType = paramValue;
        }
    }

    @Override
    protected ElementInfo createElementInfo() {
        return new ElementInfo(MimeTypes.JSON, 0);
    }

    @Override
    public boolean isShowInSitemap() {
        return false;
    }
}
/* This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this file,
 * You can obtain one at http://mozilla.org/MPL/2.0/. */
package pt.webdetails.cpk.elements.impl.kettleOutputs;

import java.io.BufferedOutputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.ObjectOutput;
import java.io.ObjectOutputStream;
import java.io.OutputStream;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.jar.JarOutputStream;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;
import org.apache.commons.io.IOUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.commons.vfs.FileContent;
import org.apache.commons.vfs.FileObject;
import org.codehaus.jackson.map.ObjectMapper;
import org.pentaho.di.core.Result;
import org.pentaho.di.core.ResultFile;
import org.pentaho.di.core.row.RowMetaInterface;
import org.pentaho.di.core.vfs.KettleVFS;
import org.pentaho.platform.api.engine.IParameterProvider;
import pt.webdetails.cpf.Util;
import pt.webdetails.cpf.utils.MimeTypes;
import pt.webdetails.cpf.utils.PluginUtils;
import pt.webdetails.cpk.elements.impl.KettleElementType;
import pt.webdetails.cpk.elements.impl.KettleElementType.KettleType;

/**
 *
 * @author Pedro Alves<pedro.alves@webdetails.pt>
 */
public class KettleOutput implements IKettleOutput {

    protected Log logger = LogFactory.getLog(this.getClass());
    ArrayList<Object[]> rows;
    ArrayList<RowMetaInterface> rowsMeta;
    Result result = null;
    OutputStream out;
    protected KettleType kettleType;
    private String outputStepName = "OUTPUT";
    private Map<String, IParameterProvider> parameterProviders;

    public KettleOutput(Map<String, IParameterProvider> parameterProviders) {
        init(parameterProviders);
    }

    protected void init(Map<String, IParameterProvider> parameterProviders) {

        this.parameterProviders = parameterProviders;
        rows = new ArrayList<Object[]>();
        rowsMeta = new ArrayList<RowMetaInterface>();


        try {
            out = PluginUtils.getInstance().getResponseOutputStream(parameterProviders);
        } catch (IOException ex) {
            Logger.getLogger("Something went wrong on the KettleOutput class initialization.");
        }

    }

    @Override
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

    @Override
    public void setResult(Result r) {
        this.result = r;
    }

    @Override
    public Result getResult() {
        return this.result;
    }

    @Override
    public KettleType getKettleType() {
        return kettleType;
    }

    @Override
    public void setKettleType(KettleType kettleType) {
        this.kettleType = kettleType;
    }

    public void resultJson() {
    }

    /*
     *  1. ResultOnly
     *  2. ResultFiles
     *  
     *      These do:
     *  3. SingleCell
     *  4. Json
     *  5. Infered
     */
    public void processResultOnly() {

        ObjectMapper mapper = new ObjectMapper();

        try {
            mapper.writeValue(out, result);
        } catch (IOException ex) {
            Logger.getLogger(KettleElementType.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    public void processResultFiles() {

        logger.debug("Process Result Files");

        // Singe file? Just write it to the outputstream
        List<ResultFile> filesList = getResult().getResultFilesList();

        if (filesList.isEmpty()) {
            logger.warn("Processing result files but no files found");
            return;
        } else if (filesList.size() == 1) {
            ResultFile file = filesList.get(0);

            // Do we know the mime type?
            String mimeType = MimeTypes.getMimeType(file.getFile().getName().getExtension());

            if (Boolean.parseBoolean(PluginUtils.getInstance().getRequestParameters(parameterProviders).getStringParameter("download", "false"))) {
                PluginUtils.getInstance().setResponseHeaders(parameterProviders, mimeType, file.getFile().getName().getBaseName());
            }
            else{
                // set Mimetype only
                PluginUtils.getInstance().setResponseHeaders(parameterProviders, mimeType);
            }
            
            
            try {
                IOUtils.copy(KettleVFS.getInputStream(file.getFile()), PluginUtils.getInstance().getResponseOutputStream(parameterProviders));
            } catch (Exception ex) {
                logger.warn("Failed to copy file to outputstream: " + Util.getExceptionDescription(ex));
            }

        } else {
            // Build a zip / tar and ship it over!
            
            try {
                String zipPath = "/tmp/";
                String zipName = filesList.get(0).getFile().getParent().getName().getBaseName() +".zip";
                String zipFullPath = zipPath+zipName;
                logger.info("Building '"+zipFullPath+"'");
                FileOutputStream fos = new FileOutputStream(zipFullPath);
                ZipOutputStream zipOut = new ZipOutputStream(new BufferedOutputStream(fos)); 
                InputStreamReader isr = null;
                FileInputStream fis = null;
                
                for(ResultFile resFile: filesList){
                    FileObject myFile = resFile.getFile();

                    ZipEntry zip = new ZipEntry(myFile.getName().getParent().getBaseName()+"/"+myFile.getName().getBaseName());
                    zipOut.putNextEntry(zip);

                    isr = new InputStreamReader(myFile.getContent().getInputStream());

                    byte [] bytes = IOUtils.toByteArray(isr);

                    zipOut.write(bytes);
                }
                
                zipOut.close();
                logger.info("'"+zipName+"' built. Sending to client...");
                
                
                PluginUtils.getInstance().setResponseHeaders(parameterProviders, "ZIP", zipName);
                
                fis = new FileInputStream(zipFullPath);
                
                IOUtils.copy(fis, out);
                                
            } catch (Exception ex) {
                Logger.getLogger(KettleOutput.class.getName()).log(Level.SEVERE, null, ex);
            }
            
            
        }
    }

    public void processSingleCell() {


        logger.debug("Process Single Cell - print it");

        // TODO - make sure this is correct

        try {
            PluginUtils.getInstance().getResponseOutputStream(parameterProviders).write(getRows().get(0)[0].toString().getBytes("UTF-8"));
        } catch (UnsupportedEncodingException ex) {
            Logger.getLogger(KettleOutput.class.getName()).log(Level.SEVERE, null, ex);
        } catch (IOException ex) {
            Logger.getLogger(KettleOutput.class.getName()).log(Level.SEVERE, null, ex);
        }

    }

    public void processJson() {
        ObjectMapper mapper = new ObjectMapper();
        try {
            mapper.writeValue(out, rows);
        } catch (IOException ex) {
            Logger.getLogger(KettleOutput.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    public void processInfered() {

        logger.debug("Process Infered");

        /*
         *  If nothing specified, the behavior will be:
         *  Jobs and Transformations with result filenames: ResultFiles
         *   Without filenames:
         *    * Jobs: ResultOnly
         *    * Transformations:
         *      * Just one cell: SingleCell
         *      * Regular resultset: Json
         */
        Result result = getResult();

        if (result.getResultFilesList().size() > 0) {

            processResultFiles();
        } else {

            if (getKettleType() == KettleType.JOB) {
                processResultOnly();
            } else {

                if (getRows().size() == 1 && getRowsMeta().size() == 1) {
                    processSingleCell();
                } else {
                    processJson();
                }

            }

        }
    }

    @Override
    public boolean needsRowListener() {
        return true;
    }

    @Override
    public void processResult() {
        processInfered();
    }

    public String getOutputStepName() {
        return outputStepName;
    }

    @Override
    public void setOutputStepName(String outputStepName) {
        this.outputStepName = outputStepName;
    }
}

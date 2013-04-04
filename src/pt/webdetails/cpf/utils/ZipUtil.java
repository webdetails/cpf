/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package pt.webdetails.cpf.utils;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.zip.ZipEntry;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import java.util.zip.ZipOutputStream;
import org.apache.commons.io.IOUtils;
import org.apache.commons.vfs.FileObject;
import org.pentaho.di.core.ResultFile;


/**
 *
 * @author bandjalah
 */
public class ZipUtil {
    private String zipName;
    private String zipPath;
    private String zipFullPath;
    private ZipOutputStream zipOut;
    private InputStreamReader isr;
    private FileInputStream fis;
    ArrayList<String> fileListing = new ArrayList<String>();
    
    protected Log logger = LogFactory.getLog(this.getClass());
    
    public ZipUtil(List<ResultFile> filesList){
        init(filesList);
        
    }
    
    private void init(List<ResultFile> filesList){
        try {
            
            zipName = filesList.get(0).getFile().getParent().getName().getBaseName();
            File tempZip = File.createTempFile(zipName, ".tmp");
            zipFullPath = zipPath+zipName;
            
            FileOutputStream fos = new FileOutputStream(tempZip);
            zipOut = new ZipOutputStream(new BufferedOutputStream(fos)); 
            
            isr = null;
            fis = null;

            logger.info("Building '"+zipFullPath+"'...");

            writeEntriesToZip(filesList);
            logger.info("'"+zipName+"' built."+" Sending to client...");
            zipOut.close();

            fis = new FileInputStream(tempZip);
                
                
        } catch (Exception ex) {
            Logger.getLogger(ZipUtil.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
    
    private void writeEntriesToZip(List<ResultFile> filesList){
        int i=0;
        try {
            for (ResultFile resFile : filesList) {
                i++;
                logger.info("Files to process:"+filesList.size());
                logger.info("Files processed: "+i);
                logger.info("Files remaining: "+(filesList.size()-i));
                logger.debug(resFile.getFile().getName().getPath());
                FileObject myFile = resFile.getFile();
                
                fileListing.add(myFile.getName().getPath());

                ZipEntry zip = new ZipEntry(myFile.getName().getPath());
                zipOut.putNextEntry(zip);

                isr = new InputStreamReader(myFile.getContent().getInputStream());

                byte[] bytes = IOUtils.toByteArray(isr);

                zipOut.write(bytes);
            }
            
        } catch (Exception exception) {
            logger.error(exception);
        }
    }

    public FileInputStream getZipInputStream(){
        return fis;
    }
    
    
    public String getZipNameToDownload(){
        return getZipName().replaceAll(" ", "-")+".zip"; //Firefox and Opera don't interpret blank spaces and cut the string there causing the files to be "bin".
    }
    
    public String getZipName(){
        return zipName;
    }
    
    public int getZipSize(){
        return 0;
    }
    
    
    
}

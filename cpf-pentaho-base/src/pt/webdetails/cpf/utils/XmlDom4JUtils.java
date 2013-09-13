package pt.webdetails.cpf.utils;

import java.io.BufferedInputStream;
import java.io.IOException;
import java.io.InputStream;

import org.apache.commons.io.IOUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.dom4j.Document;
import org.pentaho.platform.util.xml.dom4j.XmlDom4JHelper;

import pt.webdetails.cpf.repository.api.IBasicFile;
import pt.webdetails.cpf.repository.api.IReadAccess;


public class XmlDom4JUtils {
	
	protected static Log logger = LogFactory.getLog(XmlDom4JUtils.class);
	
	public static Document getDocumentFromStream(InputStream is){
		
		if(is == null){ return null; }
		
		BufferedInputStream bis = null;
		
        try {
        	// passing inputStream directly to getDocFromStream() causes a 
        	// "org.dom4j.DocumentException: Error on line 1 of document: Content is not allowed in prolog"
        	// so we wrap it in a BufferedInputStream
        	bis = new BufferedInputStream(is);
        	
            return XmlDom4JHelper.getDocFromStream(bis);
        } catch (Exception ex) {
        	logger.error(ex);
        } finally {
          IOUtils.closeQuietly(bis);
        }
        return null;
	}
	
	public static Document getDocumentFromFile(final IBasicFile file) throws IOException {
	    return (file != null) ? getDocumentFromStream(file.getContents()) : null;
	}
	
	public static Document getDocumentFromFile(final IReadAccess access, final String filePath) throws IOException {    
		  if(access != null && filePath != null && access.fileExists(filePath)){
			  return getDocumentFromFile(access.fetchFile(filePath));
		  }
		  return null;
	}
}

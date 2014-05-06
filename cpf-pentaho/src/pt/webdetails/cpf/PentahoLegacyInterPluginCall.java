package pt.webdetails.cpf;

import org.pentaho.platform.api.engine.IContentGenerator;
import org.pentaho.platform.api.engine.ObjectFactoryException;

import pt.webdetails.cpf.PentahoInterPluginCall;

public class PentahoLegacyInterPluginCall extends PentahoInterPluginCall {
	
	public boolean pluginExists(){
	    try {
	      return getPluginManager().getContentGenerator(plugin.getName(), getSession()) != null;
	    } catch (ObjectFactoryException e) {
	      return false;
	    }
	}
	
	protected IContentGenerator getContentGenerator(){
	    try {
	      IContentGenerator contentGenerator = getPluginManager().getContentGenerator(plugin.getName(), getSession());
	      if(contentGenerator == null){
	        logger.error("ContentGenerator for " + plugin.getName() + " could not be fetched.");
	      }
	      return contentGenerator;
	    } catch (Exception e) {
	      logger.error("Failed to acquire " + plugin.getName() + " plugin: " + e.toString(), e);
	      return null;
	    }
	}
}

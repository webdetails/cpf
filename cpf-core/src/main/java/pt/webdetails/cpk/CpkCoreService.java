/* This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this file,
 * You can obtain one at http://mozilla.org/MPL/2.0/. */
package pt.webdetails.cpk;
import java.io.IOException;
import java.io.OutputStream;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.Map;
import org.dom4j.DocumentException;
import pt.webdetails.cpf.RestRequestHandler;
import pt.webdetails.cpf.Router;
import pt.webdetails.cpf.http.ICommonParameterProvider;
import pt.webdetails.cpf.repository.IRepositoryAccess;
import pt.webdetails.cpf.utils.IPluginUtils;
import pt.webdetails.cpk.elements.IElement;
import pt.webdetails.cpk.security.IAccessControl;


/**
 *
 * @author joao
 */
public class CpkCoreService {
    

    private static final long serialVersionUID = 1L;
    public static final String CDW_EXTENSION = ".cdw";
    public static final String PLUGIN_NAME = "cpk";
    private static final String ENCODING = "UTF-8";
    protected CpkEngine cpkEngine;
    private IRepositoryAccess repAccess;
    private static final Logger logger = Logger.getLogger(CpkCoreService.class.getName());
    protected ICpkEnvironment cpkEnvironment;

    public CpkCoreService(ICpkEnvironment environment){
        this.cpkEnvironment = environment;
    }
    //public CpkCoreService(){}
    
    public void createContent(Map<String,ICommonParameterProvider> parameterProviders) throws Exception {

        //Make sure the instance is first set so we have pluginUtils
        cpkEngine = CpkEngine.getInstanceWithEnv(cpkEnvironment);
        
        

        
        IAccessControl accessControl = cpkEnvironment.getAccessControl();

        
        logger.log(Level.WARNING,"Creating content");//switched from debug("Creating content")

        // Get the path, remove leading slash
        IPluginUtils pluginUtils = cpkEnvironment.getPluginUtils();
        String path = pluginUtils.getPathParameters(parameterProviders).getStringParameter("path", null);
        IElement element = null;

        if (path == null || path.equals("/")) {

            String url = cpkEngine.getDefaultElement().getId().toLowerCase();
            if (path == null) {
                // We need to put the http redirection on the right level
                url = pluginUtils.getPluginName() + "/" + url;
            }
            pluginUtils.redirect(parameterProviders, url);
        }

        element = cpkEngine.getElement(path.substring(1).toLowerCase());
        if (element != null) {
            if (accessControl.isAllowed(element)) {
                element.processRequest(parameterProviders);
            } else {
                accessControl.throwAccessDenied(parameterProviders);
            }

        } else {
            Logger.getLogger(CpkCoreService.class.getName()).log(Level.SEVERE, "Unable to get element!");
            //XXX confirm error message
        }


    }


    public void reload(OutputStream out,Map<String,ICommonParameterProvider> parameterProviders) throws DocumentException, IOException {

        // alias to refresh
        refresh(out,parameterProviders); 
    }


    public void refresh(OutputStream out, Map<String,ICommonParameterProvider> parameterProviders) throws DocumentException, IOException {
        IAccessControl accessControl = cpkEnvironment.getAccessControl();
        if(accessControl.isAdmin()){
            logger.info("Refreshing CPK plugin " + getPluginName());
            cpkEngine.reload();
            status(out,parameterProviders); 
        }else{
            accessControl.throwAccessDenied(parameterProviders);
        }


    }


    public void status(OutputStream out, Map<String,ICommonParameterProvider> parameterProviders) throws DocumentException, IOException {

        logger.info("Showing status for CPK plugin " + getPluginName());

        cpkEnvironment.getPluginUtils().setResponseHeaders(parameterProviders, "text/plain");
        out.write(cpkEngine.getStatus().getBytes("UTF-8"));

    }


    
    public IElement[] getElements() {
        return cpkEngine.getElementsMap().values().toArray(new IElement[0]);      
    }
    
    public void getElementsList(OutputStream out){
        try {
            out.write(cpkEngine.getElementsJson().getBytes(ENCODING));
        } catch (IOException ex) {
            Logger.getLogger(CpkCoreService.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
    
    
    public String getPluginName() {

      return cpkEnvironment.getPluginName();
    }
    
    private void writeMessage(OutputStream out, String message){
        try {
            out.write(message.getBytes(ENCODING));
        } catch (IOException ex) {
            Logger.getLogger(CpkCoreService.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    public RestRequestHandler getRequestHandler() {
        return Router.getBaseRouter();
    }
}

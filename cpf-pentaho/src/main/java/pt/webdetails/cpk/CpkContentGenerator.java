/* This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this file,
 * You can obtain one at http://mozilla.org/MPL/2.0/. */
package pt.webdetails.cpk;

import java.io.IOException;
import java.io.OutputStream;
import java.util.AbstractMap;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Map.Entry;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.codehaus.jackson.JsonNode;
import org.codehaus.jackson.map.ObjectMapper;
import org.dom4j.DocumentException;
import pt.webdetails.cpf.RestContentGenerator;
import pt.webdetails.cpf.RestRequestHandler;
import pt.webdetails.cpf.Router;
import pt.webdetails.cpf.annotations.AccessLevel;
import pt.webdetails.cpf.annotations.Exposed;
import pt.webdetails.cpf.http.CommonParameterProvider;
import pt.webdetails.cpf.http.ICommonParameterProvider;
import pt.webdetails.cpk.security.AccessControl;
import pt.webdetails.cpf.utils.IPluginUtils;
import pt.webdetails.cpf.utils.PluginUtils;
import pt.webdetails.cpk.elements.IElement;
import pt.webdetails.cpk.plugins.PluginBuilder;
import org.pentaho.platform.api.engine.IParameterProvider;
import pt.webdetails.cpf.repository.IRepositoryAccess;
import pt.webdetails.cpf.repository.PentahoRepositoryAccess;

public class CpkContentGenerator extends RestContentGenerator {

    private static final long serialVersionUID = 1L;
    public static final String CDW_EXTENSION = ".cdw";
    public static final String PLUGIN_NAME = "cpk";
    //private CpkEngine cpkEngine;
    private CpkPentahoEngine cpkPentahoEngine;
    private ICommonParameterProvider commonParameterProvider;
    private Map<String, ICommonParameterProvider> map;
    private IPluginUtils pluginUtils;
    private IRepositoryAccess repAccess;
    @Override
    public void initParams(){
        
            //XXX review
        repAccess = new PentahoRepositoryAccess();
        pluginUtils=new PluginUtils();
        cpkPentahoEngine = CpkPentahoEngine.getInstanceWithPluginUtils(pluginUtils, repAccess);
        Iterator it =  parameterProviders.entrySet().iterator();
        map = new HashMap<String, ICommonParameterProvider>();
        while(it.hasNext()){
            Entry<String,IParameterProvider> e = (Entry<String,IParameterProvider>) it.next();
            commonParameterProvider=new CommonParameterProvider();
           commonParameterProvider.put(e.getKey(), e.getValue());
           map.put(e.getKey(), commonParameterProvider);
        }
        
    }

    @Override
    public void createContent() throws Exception {

        // Make sure we have the engine running
        cpkPentahoEngine = CpkPentahoEngine.getInstance();
        
        
        AccessControl accessControl = new AccessControl(pluginUtils);
        
        debug("Creating content");

        // Get the path, remove leading slash
        String path = pluginUtils.getPathParameters(map).getStringParameter("path", null);
        IElement element = null;


        if (path == null || path.equals("/")) {

            String url = cpkPentahoEngine.getDefaultElement().getId().toLowerCase();
            if (path == null) {
                // We need to put the http redirection on the right level
                url = pluginUtils.getPluginName() + "/" + url;
            }
            pluginUtils.redirect(map, url);
        }

        element = cpkPentahoEngine.getElement(path.substring(1));
        if (element != null) {
            if (accessControl.isAllowed(element)) {
                element.processRequest(map);
            } else {
                accessControl.throwAccessDenied(map);
            }

        } else {
            super.createContent();
        }


    }

    @Exposed(accessLevel = AccessLevel.PUBLIC)
    public void reload(OutputStream out) throws DocumentException, IOException {

        // alias to refresh
        refresh(out);
    }

    @Exposed(accessLevel = AccessLevel.PUBLIC)
    public void refresh(OutputStream out) throws DocumentException, IOException {
        AccessControl accessControl = new AccessControl(pluginUtils);
        if(accessControl.isAdmin()){
            logger.info("Refreshing CPK plugin " + getPluginName());
            cpkPentahoEngine.reload();
            status(out);
        }else{
            accessControl.throwAccessDenied(map);//XXX changed from accessControl.throwAccessDenied(parameterProviders);
        }


    }

    @Exposed(accessLevel = AccessLevel.PUBLIC)
    public void status(OutputStream out) throws DocumentException, IOException {

        logger.info("Showing status for CPK plugin " + getPluginName());

        pluginUtils.setResponseHeaders(map, "text/plain");
        out.write(cpkPentahoEngine.getStatus().getBytes("UTF-8"));

    }

    @Exposed(accessLevel = AccessLevel.PUBLIC)
    public void getSitemapJson(OutputStream out) throws IOException {
        ObjectMapper mapper = new ObjectMapper();
        mapper.writeValue(out, cpkPentahoEngine.getSitemapJson());
    }
    
    @Exposed(accessLevel = AccessLevel.PUBLIC)
    public void pluginsList(OutputStream out){
        
        
        ObjectMapper mapper = new ObjectMapper();
        
        try {
            String json = mapper.writeValueAsString(cpkPentahoEngine.getPluginsList());
            writeMessage(out, json);
        } catch (IOException ex) {
            try {
                out.write("Error getting JSON".getBytes(ENCODING));
            } catch (IOException ex1) {
                Logger.getLogger(CpkContentGenerator.class.getName()).log(Level.SEVERE, null, ex1);
            }
        }
        
    }
    
    @Exposed(accessLevel = AccessLevel.PUBLIC)
    public void getElementsList(OutputStream out){
        try {
            out.write(cpkPentahoEngine.getElementsJson().getBytes(ENCODING));
        } catch (IOException ex) {
            Logger.getLogger(CpkContentGenerator.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
    
    @Exposed(accessLevel = AccessLevel.PUBLIC)
    public void createPlugin(OutputStream out){
        String json = parameterProviders.get("request").getStringParameter("plugin", null);
        
        ObjectMapper mapper = new ObjectMapper();
        try {
            JsonNode node = mapper.readTree(json);
            PluginBuilder pluginMaker = new PluginBuilder(node);
            pluginMaker.writeFiles(true);
            writeMessage(out, "Plugin created successfully!");
            
        } catch (Exception ex) {
            writeMessage(out, "There seems to have occurred an error during the plugin creation. Sorry!");
            Logger.getLogger(CpkContentGenerator.class.getName()).log(Level.SEVERE, null, ex);
        }
        
    }

    @Override
    public String getPluginName() {

        return pluginUtils.getPluginName();
    }
    
    private void writeMessage(OutputStream out, String message){
        try {
            out.write(message.getBytes(ENCODING));
        } catch (IOException ex) {
            Logger.getLogger(CpkContentGenerator.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    @Override
    public RestRequestHandler getRequestHandler() {
        return Router.getBaseRouter();
    }
}

/* This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this file,
 * You can obtain one at http://mozilla.org/MPL/2.0/. */
package pt.webdetails.cpk;

import java.io.IOException;
import java.io.OutputStream;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.codehaus.jackson.map.ObjectMapper;
import org.dom4j.DocumentException;
import pt.webdetails.cpf.RestContentGenerator;
import pt.webdetails.cpf.RestRequestHandler;
import pt.webdetails.cpf.Router;
import pt.webdetails.cpf.annotations.AccessLevel;
import pt.webdetails.cpf.annotations.Exposed;
import pt.webdetails.cpf.plugins.IPluginFilter;
import pt.webdetails.cpf.plugins.Plugin;
import pt.webdetails.cpf.plugins.PluginsAnalyzer;
import pt.webdetails.cpk.security.AccessControl;
import pt.webdetails.cpf.utils.PluginUtils;
import pt.webdetails.cpk.elements.IElement;

public class CpkContentGenerator extends RestContentGenerator {

    private static final long serialVersionUID = 1L;
    public static final String CDW_EXTENSION = ".cdw";
    public static final String PLUGIN_NAME = "cpk";
    private CpkEngine cpkEngine;
    

    @Override
    public void createContent() throws Exception {

        // Make sure we have the engine running
        cpkEngine = CpkEngine.getInstance();
        PluginUtils pluginUtils = PluginUtils.getInstance();
        
        AccessControl accessControl = new AccessControl();
        
        debug("Creating content");

        // Get the path, remove leading slash
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

        element = cpkEngine.getElement(path.substring(1));
        if (element != null) {
            if (accessControl.isAllowed(element)) {
                element.processRequest(parameterProviders);
            } else {
                accessControl.throwAccessDenied(parameterProviders);
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
        AccessControl accessControl = new AccessControl();
        if(accessControl.isAdmin()){
            logger.info("Refreshing CPK plugin " + getPluginName());
            cpkEngine.reload();
            status(out);
        }else{
            accessControl.throwAccessDenied(parameterProviders);
        }


    }
    
    @Exposed(accessLevel = AccessLevel.PUBLIC)
    public void version(OutputStream out){        

        PluginsAnalyzer pluginsAnalyzer = new PluginsAnalyzer();
        pluginsAnalyzer.refresh();
        
        String version = null;
        
        IPluginFilter thisPlugin = new IPluginFilter() {

            @Override
            public boolean include(Plugin plugin) {
                return plugin.getId().equalsIgnoreCase(PluginUtils.getInstance().getPluginName());
            }
        };
        
        List<Plugin> plugins = pluginsAnalyzer.getPlugins(thisPlugin);
        

        version = plugins.get(0).getVersion().toString();
        writeMessage(out, version);
    }

    @Exposed(accessLevel = AccessLevel.PUBLIC)
    public void status(OutputStream out) throws DocumentException, IOException {

        logger.info("Showing status for CPK plugin " + getPluginName());

        PluginUtils.getInstance().setResponseHeaders(parameterProviders, "text/plain");
        out.write(cpkEngine.getStatus().getBytes("UTF-8"));

    }

    @Exposed(accessLevel = AccessLevel.PUBLIC)
    public void getSitemapJson(OutputStream out) throws IOException {
        ObjectMapper mapper = new ObjectMapper();
        mapper.writeValue(out, cpkEngine.getSitemapJson());
    }
    
    @Exposed(accessLevel = AccessLevel.PUBLIC)
    public void pluginsList(OutputStream out){
        
        
        ObjectMapper mapper = new ObjectMapper();
        
        try {
            String json = mapper.writeValueAsString(cpkEngine.getPluginsList());
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
            out.write(cpkEngine.getElementsJson().getBytes(ENCODING));
        } catch (IOException ex) {
            Logger.getLogger(CpkContentGenerator.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    @Override
    public String getPluginName() {
        return PluginUtils.getInstance().getPluginName();
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

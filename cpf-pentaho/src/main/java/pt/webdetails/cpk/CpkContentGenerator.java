/* This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this file,
 * You can obtain one at http://mozilla.org/MPL/2.0/. */
package pt.webdetails.cpk;

import java.io.IOException;
import java.io.OutputStream;
import java.util.AbstractMap;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
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
import pt.webdetails.cpk.security.IAccessControl;
import pt.webdetails.cpf.utils.IPluginUtils;
import pt.webdetails.cpf.utils.PluginUtils;
import pt.webdetails.cpk.elements.IElement;
import org.pentaho.platform.api.engine.IParameterProvider;
import pt.webdetails.cpf.WrapperUtils;
import pt.webdetails.cpf.plugins.IPluginFilter;
import pt.webdetails.cpf.plugins.Plugin;
import pt.webdetails.cpf.plugins.PluginsAnalyzer;
import pt.webdetails.cpf.repository.IRepositoryAccess;
import pt.webdetails.cpf.repository.PentahoRepositoryAccess;
import pt.webdetails.cpk.security.AccessControl;
import pt.webdetails.cpk.CpkCoreService;

public class CpkContentGenerator extends RestContentGenerator {

    private static final long serialVersionUID = 1L;
    public static final String CDW_EXTENSION = ".cdw";
    public static final String PLUGIN_NAME = "cpk";
    private CpkEngine cpkEngine;
    private ICommonParameterProvider commonParameterProvider;
    private IRepositoryAccess repAccess;
    private ICpkEnvironment cpkEnv;
    private CpkCoreService coreService;
   
    
    public CpkContentGenerator(ICpkEnvironment cpkEnv){
        super(cpkEnv.getPluginUtils());
        //super.initParams();
        this.cpkEnv=cpkEnv;
        cpkEngine = CpkEngine.getInstanceWithEnv(cpkEnv);
        this.coreService=new CpkCoreService(cpkEnv);
    }

    @Override
    public void createContent() throws Exception {
        coreService.createContent(map);
       /* // Make sure we have the engine running
        //cpkPentahoEngine = CpkPentahoEngine.getInstance();
        cpkPentahoEngine = CpkPentahoEngine.getInstanceWithEnv(cpkEnv);
        
        //AccessControl accessControl = new AccessControl(pluginUtils);
        
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
            if (cpkEnv.getAccessControl().isAllowed(element)) {
                element.processRequest(map);
            } else {
                cpkEnv.getAccessControl().throwAccessDenied(map);
            }

        } else {
            super.createContent(); //XXX this will not be called due to coreService
        }

*/
    }

    @Exposed(accessLevel = AccessLevel.PUBLIC)
    public void reload(OutputStream out) throws DocumentException, IOException {
        coreService.reload(out, map);
    }
    
    @Exposed(accessLevel = AccessLevel.PUBLIC)
    public void version(OutputStream out){        

        PluginsAnalyzer pluginsAnalyzer = new PluginsAnalyzer();
        pluginsAnalyzer.refresh();
        
        String version = null;
        
        IPluginFilter thisPlugin = new IPluginFilter() {

            @Override
            public boolean include(Plugin plugin) {
                return plugin.getId().equalsIgnoreCase(pluginUtils.getPluginName());
            }
        };
        
        List<Plugin> plugins = pluginsAnalyzer.getPlugins(thisPlugin);
        

        version = plugins.get(0).getVersion().toString();
        writeMessage(out, version);
    }

    @Exposed(accessLevel = AccessLevel.PUBLIC)
    public void status(OutputStream out) throws DocumentException, IOException {
        coreService.status(out, map);
    }

    @Exposed(accessLevel = AccessLevel.PUBLIC)
    public void getSitemapJson(OutputStream out) throws IOException {
        ObjectMapper mapper = new ObjectMapper();
        CpkPentahoEngine pentahoEngine = new CpkPentahoEngine(cpkEnv.getPluginUtils());
        pentahoEngine.setElementsMap(CpkEngine.getInstance().getElementsMap());
        
        mapper.writeValue(out, pentahoEngine.getSitemapJson());//XXX think of a better way to do this
    }
    
    @Exposed(accessLevel = AccessLevel.PUBLIC)
    public void pluginsList(OutputStream out){
        
        
        ObjectMapper mapper = new ObjectMapper();
        
        try {
            String json = mapper.writeValueAsString(CpkPentahoEngine.getPluginsList());
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
        coreService.getElementsList(out);
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

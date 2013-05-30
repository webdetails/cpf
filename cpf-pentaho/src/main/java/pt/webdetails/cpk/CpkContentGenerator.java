/* This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this file,
 * You can obtain one at http://mozilla.org/MPL/2.0/. */
package pt.webdetails.cpk;

import java.io.IOException;
import java.io.OutputStream;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.codehaus.jackson.JsonNode;
import org.codehaus.jackson.map.ObjectMapper;
import org.dom4j.DocumentException;
import org.pentaho.platform.api.engine.IParameterProvider;
import pt.webdetails.cpf.RestContentGenerator;
import pt.webdetails.cpf.RestRequestHandler;
import pt.webdetails.cpf.Router;
import pt.webdetails.cpf.WrapperUtils;
import pt.webdetails.cpf.annotations.AccessLevel;
import pt.webdetails.cpf.annotations.Exposed;
import pt.webdetails.cpf.http.ICommonParameterProvider;
import pt.webdetails.cpf.plugins.IPluginFilter;
import pt.webdetails.cpf.plugins.Plugin;
import pt.webdetails.cpf.plugins.PluginsAnalyzer;
import pt.webdetails.cpf.repository.IRepositoryAccess;
import pt.webdetails.cpf.repository.PentahoRepositoryAccess;
import pt.webdetails.cpf.utils.PluginUtils;
import pt.webdetails.cpk.elements.IElement;
import pt.webdetails.cpk.sitemap.LinkGenerator;

public class CpkContentGenerator extends RestContentGenerator {

    private static final long serialVersionUID = 1L;
    public static final String CDW_EXTENSION = ".cdw";
    public static final String PLUGIN_NAME = "cpk";
    protected CpkCoreService coreService;
    protected ICpkEnvironment cpkEnv;

    /*public CpkContentGenerator(ICpkEnvironment cpkEnv) {
     super(cpkEnv.getPluginUtils());
     CpkEngine.getInstanceWithEnv(cpkEnv);
     this.coreService = new CpkCoreService(cpkEnv);
     }//*/
    public CpkContentGenerator() {
        this.pluginUtils = new PluginUtils();
        this.cpkEnv = new CpkPentahoEnvironment(pluginUtils, new PentahoRepositoryAccess());
        this.coreService = new CpkCoreService(cpkEnv); 
    }

    @Override
    public void createContent() throws Exception {
        wrapParams();//XXX 
        try {
            coreService.createContent(map);
        } catch (NoElementException e) {
            super.createContent();
        }
    }

    @Exposed(accessLevel = AccessLevel.PUBLIC)
    public void reload(OutputStream out) throws DocumentException, IOException {
        coreService.reload(out, map);
    }
    
    @Exposed(accessLevel = AccessLevel.PUBLIC)
    public void refresh(OutputStream out) throws DocumentException, IOException {
        coreService.refresh(out, map);
    }

    @Exposed(accessLevel = AccessLevel.PUBLIC)
    public void version(OutputStream out) {

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

        TreeMap<String, IElement> elementsMap = CpkEngine.getInstance().getElementsMap();
        JsonNode sitemap = null;
        if (elementsMap != null) {
            LinkGenerator linkGen = new LinkGenerator(elementsMap, pluginUtils);
            sitemap = linkGen.getLinksJson();
        }
        ObjectMapper mapper = new ObjectMapper();
        mapper.writeValue(out, sitemap);
    }

    @Exposed(accessLevel = AccessLevel.PUBLIC)
    public void getElementsList(OutputStream out) {
        coreService.getElementsList(out);
    }

    @Override
    public String getPluginName() {

        return pluginUtils.getPluginName();
    }

    private void writeMessage(OutputStream out, String message) {
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

    private void wrapParams() {
        if (parameterProviders != null) {
            Iterator it = parameterProviders.entrySet().iterator();
            map = new HashMap<String, ICommonParameterProvider>();
            while (it.hasNext()) {
                Map.Entry<String, IParameterProvider> e = (Map.Entry<String, IParameterProvider>) it.next();
                map.put(e.getKey(), WrapperUtils.wrapParamProvider(e.getValue()));
            }
        }
    }
}

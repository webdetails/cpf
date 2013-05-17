/* This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this file,
 * You can obtain one at http://mozilla.org/MPL/2.0/. */
package pt.webdetails.cpk;

import java.io.IOException;
import java.util.List;
import java.util.TreeMap;
import org.codehaus.jackson.JsonNode;
import pt.webdetails.cpf.plugins.IPluginFilter;
import pt.webdetails.cpf.plugins.Plugin;
import pt.webdetails.cpf.plugins.PluginsAnalyzer;
import pt.webdetails.cpk.sitemap.LinkGenerator;
import pt.webdetails.cpk.elements.IElement;


/**
 *
 * @author Pedro Alves<pedro.alves@webdetails.pt>
 */
public class CpkPentahoEngine  {//XXX needs more attention

    private ICpkEnvironment cpkEnv;
   
    public CpkPentahoEngine(ICpkEnvironment cpkEnv){
   
        this.cpkEnv=cpkEnv;
    }

    public  JsonNode getSitemapJson() throws IOException {//XXX get elementsMap somehow
        LinkGenerator linkGen = new LinkGenerator(new TreeMap<String, IElement>(),cpkEnv.getPluginUtils());
        return linkGen.getLinksJson();
    }

 
    public static List<Plugin> getPluginsList(){
        PluginsAnalyzer pluginsAnalyzer = new PluginsAnalyzer();
        pluginsAnalyzer.refresh();
        
        List<Plugin> plugins = pluginsAnalyzer.getInstalledPlugins();
        
        IPluginFilter pluginFilter = new IPluginFilter() {

            @Override
            public boolean include(Plugin plugin) {
                boolean is = false;
                String xmlValue = plugin.getXmlValue("/plugin/content-generator/@class", "plugin.xml");
                String className = "pt.webdetails.cpk.CpkContentGenerator";
                
                if(xmlValue.equals(className)){
                    is = true;
                }
                
                return is;
            }
        };
        
        plugins = pluginsAnalyzer.getPlugins(pluginFilter);
        
        return plugins;
    }

}
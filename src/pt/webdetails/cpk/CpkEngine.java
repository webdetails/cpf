/* This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this file,
 * You can obtain one at http://mozilla.org/MPL/2.0/. */
package pt.webdetails.cpk;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.TreeMap;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.codehaus.jackson.JsonNode;
import org.dom4j.Document;
import org.dom4j.DocumentException;
import org.dom4j.Node;
import org.pentaho.platform.api.engine.IPluginResourceLoader;
import org.pentaho.platform.engine.core.system.PentahoSystem;
import org.pentaho.platform.util.xml.dom4j.XmlDom4JHelper;
import pt.webdetails.cpf.Util;
import pt.webdetails.cpf.plugins.Plugin;
import pt.webdetails.cpf.plugins.PluginsAnalyzer;
import pt.webdetails.cpk.security.AccessControl;
import pt.webdetails.cpk.sitemap.LinkGenerator;
import pt.webdetails.cpf.utils.PluginUtils;
import pt.webdetails.cpk.elements.IElement;
import pt.webdetails.cpk.elements.IElementType;

/**
 *
 * @author Pedro Alves<pedro.alves@webdetails.pt>
 */
public class CpkEngine {

    private static CpkEngine instance;
    protected Log logger = LogFactory.getLog(this.getClass());
    private Document cpkDoc;
    private TreeMap<String, IElement> elementsMap;
    private HashMap<String, IElementType> elementTypesMap;
    private static List reserverdWords = Arrays.asList("refresh", "status", "reload");
    private String defaultElementName = null;

    public CpkEngine() {

        // Starting elementEngine
        logger.debug("Starting ElementEngine");
        elementsMap = new TreeMap<String, IElement>();
        elementTypesMap = new HashMap<String, IElementType>();

        try {
            this.initialize();
        } catch (Exception ex) {
            logger.fatal("Error initializing CpkEngine: " + Util.getExceptionDescription(ex));
        }

    }

    public static CpkEngine getInstance() {

        if (instance == null) {

            instance = new CpkEngine();

        }

        return instance;

    }

    private synchronized void initialize() throws DocumentException, IOException {

        // Start by forcing initialization of PluginUtils
        PluginUtils.getInstance();

        logger.info("Initializing CPK Plugin " + PluginUtils.getInstance().getPluginName().toUpperCase());
        reload();


    }

    /**
     *
     * Reloads or initializes the ElementManager
     *
     */
    public void reload() throws DocumentException, IOException {

        // Clean the types
        elementsMap.clear();
        elementTypesMap.clear();
        
        PluginsAnalyzer pluginsAnalyzer = new PluginsAnalyzer();
        
        List<Plugin> plugins = pluginsAnalyzer.getInstalledPlugins();
        String pluginName = PluginUtils.getInstance().getPluginName();
        Plugin plugin = null;
        
        for(Plugin plgn : plugins){
            if(plgn.getName().equalsIgnoreCase(pluginName) || plgn.getId().equalsIgnoreCase(pluginName)){
                plugin = plgn;
                break;
            }
        }
        
        String fileName = "cpk.xml";
        File xmlFile = new File(plugin.getPluginSolutionPath()+fileName);
        FileInputStream fis = null;
        BufferedInputStream bis = null;
        InputStream is = null;
        
        
        if(!xmlFile.exists()){
            xmlFile = new File(plugin.getPath()+fileName);
            try{
                fis = new FileInputStream(xmlFile);
                bis = new BufferedInputStream(fis);
            }catch(Exception e){}
            
            if(!xmlFile.exists()){
                IPluginResourceLoader resLoader = PentahoSystem.get(IPluginResourceLoader.class, null);
                is = resLoader.getResourceAsStream(this.getClass(), fileName);
                bis = new BufferedInputStream(is);

            }
        }else{
            fis = new FileInputStream(xmlFile);
            bis = new BufferedInputStream(fis);
        }
        
        
        // Buffer the is
        
        
        Document cpkDoc = XmlDom4JHelper.getDocFromStream(bis, null);
        setCpkDoc(cpkDoc);

        List<Node> elementTypeNodes = cpkDoc.selectNodes("/cpk/elementTypes/elementType");
        defaultElementName = cpkDoc.selectSingleNode("/cpk/elementTypes").valueOf("@defaultElement").toLowerCase();

        for (Node node : elementTypeNodes) {

            // Loop and instantiate the element types
            String clazz = node.valueOf("./@class");
            logger.debug("Found elementType: " + clazz);

            IElementType elementType;
            try {
                elementType = (IElementType) Class.forName(clazz).newInstance();

                // Store it
                elementTypesMap.put(elementType.getType(), elementType);

            } catch (Exception ex) {
                logger.error("Error initializing element type " + clazz + ": " + Util.getExceptionDescription(ex));
                continue;
            }

            // Now that we have the class, scan the elements
            List<IElement> elements = elementType.scanElements(getCpkDoc().selectSingleNode("/cpk/elementTypes/elementType[@class='" + clazz + "']"));

            // Register them in the map. We don't support duplicates, and we don't allow some reserved names
            for (IElement element : elements) {

                String key = element.getId().toLowerCase();

                if (reserverdWords.contains(key)) {

                    logger.warn("Element with reserved work '" + key + "' can't be registred: " + element.toString());

                } else {
                    // All ok
                    if( !element.getName().startsWith("_")){
                        elementsMap.put(element.getId().toLowerCase(), element);
                    }
                }

            }


            logger.debug("Initialization for " + elementType.getType() + " successfull. Registred " + elements.size() + " elements");

        }


        // List<Url> urls = resLoader.findResources(this.getClass(), ".");



    }

    public Document getCpkDoc() {
        return cpkDoc;
    }

    public void setCpkDoc(Document cpkDoc) {
        this.cpkDoc = cpkDoc;
    }

    public TreeMap<String, IElement> getElementsMap() {
        return elementsMap;
    }

    public IElementType getElementType(String type) {
        return elementTypesMap.get(type);
    }

    /**
     * Gets the element corresponding to the registred key
     *
     * @param key
     * @return
     */
    public IElement getElement(String key) {
        return this.elementsMap.get(key);
    }

    /**
     * Gets the element corresponding to the registred key
     *
     * @param key
     * @return
     */
    public IElement getDefaultElement() {
        IElement element = elementsMap.get(defaultElementName);
        if(element==null){
            for (IElement e : this.elementsMap.values()) {
                if (CpkEngine.getInstance().getElementType(e.getElementType()).isShowInSitemap()) {
                    element = e;
                    break;
                }
            }
        }
        return element;
    }

    /**
     *
     * @return
     */
    public String getStatus() {

        AccessControl accessControl = new AccessControl();
        StringBuffer out = new StringBuffer();

        out.append("--------------------------------\n");
        out.append("   " + PluginUtils.getInstance().getPluginName() + " Status\n");
        out.append("--------------------------------\n");
        out.append("\n");

        // Show the different entities

        out.append(elementTypesMap.size() + " registered entity types\nDefault element: ["+defaultElementName+"]\n");
        out.append("\n");
        out.append("End Points\n");

        for (String key : elementsMap.keySet()) {
            
            IElement myElement = elementsMap.get(key);
            if(accessControl.isAllowed(myElement)){
                out.append("   [" + key + "]: \t" + myElement.toString() + "\n\n");
            }

        }


        return out.toString();



    }

    public JsonNode getSitemapJson() throws IOException {
        LinkGenerator linkGen = new LinkGenerator(elementsMap);
        return linkGen.getLinksJson();
    }

}

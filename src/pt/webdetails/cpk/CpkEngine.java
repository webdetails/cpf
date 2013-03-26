/* This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this file,
 * You can obtain one at http://mozilla.org/MPL/2.0/. */
package pt.webdetails.cpk;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.codehaus.jackson.JsonNode;
import org.codehaus.jackson.map.ObjectMapper;
import org.dom4j.Document;
import org.dom4j.DocumentException;
import org.dom4j.Node;
import org.dom4j.io.SAXReader;
import org.json.simple.JSONObject;
import org.pentaho.platform.api.engine.IPluginResourceLoader;
import org.pentaho.platform.engine.core.system.PentahoSystem;
import org.pentaho.platform.util.xml.dom4j.XmlDom4JHelper;
import pt.webdetails.cpf.Util;
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
    private HashMap<String, IElement> elementsMap;
    private HashMap<String, IElementType> elementTypesMap;

    private static List reserverdWords = Arrays.asList("refresh", "status","reload");
    
    
    public CpkEngine() {

        // Starting elementEngine
        logger.debug("Starting ElementEngine");
        elementsMap = new HashMap<String, IElement>();
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

        IPluginResourceLoader resLoader = PentahoSystem.get(IPluginResourceLoader.class, null);
        InputStream is = resLoader.getResourceAsStream(this.getClass(), "cpk.xml");
        // Buffer the is
        BufferedInputStream bis = new BufferedInputStream(is);
        Document cpkDoc = XmlDom4JHelper.getDocFromStream(bis, null);
        setCpkDoc(cpkDoc);

        List<Node> elementTypeNodes = cpkDoc.selectNodes("/cpk/elementTypes/elementType");

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

                if (elementsMap.containsKey(key)) {
                    
                    logger.warn("Found duplicate key " + key + " in element " + element.toString());
                    
                } else if (reserverdWords.contains(key)) {

                    logger.warn("Element with reserved work '" + key + "' can't be registred: " + element.toString());

                } else {
                    // All ok

                    elementsMap.put(element.getId().toLowerCase(), element);
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

    public HashMap<String, IElement> getElementsMap() {
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
    IElement getElement(String key) {
        return this.elementsMap.get(key);
    }

    /**
     *
     * @return
     */
    public String getStatus() {

        StringBuffer out = new StringBuffer();

        out.append("--------------------------------\n");
        out.append("   " + PluginUtils.getInstance().getPluginName() + " Status\n");
        out.append("--------------------------------\n");
        out.append("\n");

        // Show the different entities

        out.append(elementTypesMap.size() + " registred entity types\n");
        out.append("\n");
        out.append("End Points\n");

        for (String key : elementsMap.keySet()) {

            IElement iElement = elementsMap.get(key);
            out.append("   " + key + ": \t" + iElement.toString() + " \n");

        }


        return out.toString();



    }
    
    public JsonNode getSitemapJson() throws IOException{
        ObjectMapper mapper = new ObjectMapper();
        ArrayList<String> json = new ArrayList<String>();
        JSONObject jsonObj = null;
        
        JsonNode jnode=null;
        
        for(String key : elementsMap.keySet()){
            IElement e = elementsMap.get(key);
            
            
            String id = e.getLocation().split("/")[e.getLocation().split("/").length-1];
            jsonObj = new JSONObject();
            jsonObj.put("id", id);
            
            
            jsonObj.put("link", "/pentaho/content/"+PluginUtils.getInstance().getPluginName()+"/"+e.getId());
            
            jsonObj.put("sublink", "");
            
            
            if(e.getElementType().equalsIgnoreCase("dashboard")){
                jsonObj.put("name",getTextFromWcdf(e.getLocation(), "description"));
            }else if(e.getElementType().equalsIgnoreCase("kettle")){
                jsonObj.put("name", e.getName());
            }
            
            json.add(jsonObj.toJSONString());

            
        }
        
        try {
            jnode = mapper.readTree(json.toString());
        } catch (IOException ex) {
            
        } 
        
        return jnode;
    }
    
    public String getTextFromWcdf(String path,String text) throws IOException{
        File xml = new File(path);
        SAXReader reader = new SAXReader();
        Document doc = null;
        try {
            doc = reader.read(xml);
        } catch (DocumentException documentException) {
        }
        
        org.dom4j.Element root = doc.getRootElement();

       return root.elementText(text);
    }

}

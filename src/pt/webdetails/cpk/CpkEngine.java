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
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.codehaus.jackson.JsonGenerationException;
import org.codehaus.jackson.JsonNode;
import org.codehaus.jackson.JsonProcessingException;
import org.codehaus.jackson.annotate.JsonIgnore;
import org.codehaus.jackson.map.JsonMappingException;
import org.codehaus.jackson.map.ObjectMapper;
import org.dom4j.Document;
import org.dom4j.DocumentException;
import org.dom4j.Node;
import org.dom4j.io.SAXReader;
import org.json.simple.JSONObject;
import org.pentaho.platform.api.engine.IPluginResourceLoader;
import org.pentaho.platform.engine.core.system.PentahoSystem;
import org.pentaho.platform.plugin.services.pluginmgr.PluginUtil;
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
        LinkGenerator linkGen = new LinkGenerator(elementsMap.values());
        return linkGen.getLinksJson();
    }
    
    
    private class LinkGenerator{
        private ArrayList<Link> dashboardLinks;
        private ArrayList<Link> kettleLinks;
        private Collection<IElement> elements;
        private ObjectMapper mapper ;

        public LinkGenerator(Collection<IElement> e) {
            elements = e;
            generateLinks();
        }
        
        private void generateLinks(){
            dashboardLinks = new ArrayList<Link>();
            kettleLinks = new ArrayList<Link>();
            mapper = new ObjectMapper();
            
            for(IElement e : elements){
                Link link = new Link(e,false);
                if(isDashboard(e)){
                    if(!dashboardLinks.isEmpty()){
                        if(!linkExists(dashboardLinks, link) && link.getName() != null){
                            dashboardLinks.add(link);
                            logger.info(link.getName()+"-------------------------");
                            
                        }else if(link.getName() == null){
                            logger.error("There was a problem with a link creation. It's name is 'null'");
                        }
                        
                    }else if(dashboardLinks.isEmpty() && link.getName() != null){
                        dashboardLinks.add(link);
                    }
                }else if(isKettle(e)){
                    kettleLinks.add(link);
                }
            }
            
        }
        
        private boolean linkExists(ArrayList<Link> lnks, Link lnk){
            boolean exists = false;
            
            for(Link l : lnks){
                try{
                    if(l.getName() == null){
                    }else if(l.getName().equals(lnk.getName())){
                        exists=true;
                    }
                }catch(Exception e){
                    exists = true;
                }
            }
            
            return exists;
        }
        
        private ArrayList<IElement> getAllElementsOnFolder(String folderName){
            
            ArrayList<IElement> sublinkElements = new ArrayList<IElement>();
            boolean has = false;
            String primaryFolder = folderName;
            String [] dirs;
            int nrDirs;
                
            for(IElement e: elements){
                dirs = e.getLocation().split("/");
                nrDirs = dirs.length;
               
                if(dirs[nrDirs-2].equals(primaryFolder)){
                    sublinkElements.add(e);
                }
            }
            
            return sublinkElements;
        }
        
        public JsonNode getLinksJson(){
            
            JsonNode jnode = null;
            ArrayList<String> json = new ArrayList<String>();
            
            for(Link l: dashboardLinks){
                json.add(l.getLinkJson());
            }
            try {
                jnode = mapper.readTree(json.toString());
            } catch (IOException ex) {
                Logger.getLogger(CpkEngine.class.getName()).log(Level.SEVERE, null, ex);
            }
            
            return jnode;
        }
        
        public boolean isDashboard(IElement e){
            boolean is=false;
            
            if(e.getElementType().equalsIgnoreCase("dashboard")){
                is = true;
            }
            
            return is;
        }
        
        public boolean isKettle(IElement e){
            boolean is=false;
            
            if(e.getElementType().equalsIgnoreCase("kettle")){
                is = true;
            }
            
            return is;
        }
        
        private class Link{
            private String name, id, link;
            private IElement element;
            private ObjectMapper mapper;
            private boolean isSublink;
            private List<Link> subLinks;

            Link(IElement e, boolean sublnk){
                init(e,sublnk);
            }

            private void init(IElement e, boolean sublnk){
                this.element = e;
                this.isSublink = sublnk;
                mapper = new ObjectMapper();
                subLinks = new ArrayList<Link>();
                buildLink();

            }

            public void buildLink(){
                if(isDashboard(element) && !isSublink){
                    if(hasSublink()){
                        createSublinks();
                    }else{
                        this.name = getTextFromWcdf(element.getLocation(),"description");
                        this.link = "/pentaho/content/"+PluginUtils.getInstance().getPluginName()+"/"+element.getId();
                        this.id = element.getLocation().split("/")[element.getLocation().split("/").length-1];
                        //this.sublink = null;
                    }
                    
                }else if(isKettle(element) && !isSublink){
                    if(hasSublink()){
                        createSublinks();
                    }else{
                        this.name = element.getName();
                        this.id = element.getLocation().split("/")[element.getLocation().split("/").length-1];
                        //this.sublink = null;
                        this.link = "/pentaho/content/"+PluginUtils.getInstance().getPluginName()+"/"+element.getId();
                    }
                    
                    
                }else if(isSublink){
                    if(isDashboard(element)){
                        this.name = getTextFromWcdf(element.getLocation(),"description");
                        this.link = "/pentaho/content/"+PluginUtils.getInstance().getPluginName()+"/"+element.getId();
                        this.id = element.getLocation().split("/")[element.getLocation().split("/").length-1];
                        //this.sublink = null;
                    }
                    
                }
            }

            public String getName(){
                String n;
                if(this.name==null){n="null";}
                else {n = this.name;}
                return n;
            }
            
            public String getId(){
                String i;
                if(this.id==null){i="null";}
                else {i = this.id;}
                return i;
            }
            
            public List<Link> getSublinks() {
                return subLinks;
            }
            
            public String getLink(){
                String l;
                if(this.link==null){l="null";}
                else {l = this.link;}
                return l;
            }
            
            private String getTextFromWcdf(String path,String text){
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
            
            private boolean hasSublink(){
                boolean has = false;
                String primaryDir = null;
                String [] dirs = element.getLocation().split("/");
                int nrDirs = dirs.length;
                
                if(isDashboard(element)){
                    primaryDir = "dashboards";
                    if(!dirs[nrDirs-2].equals(primaryDir)){
                        has = true;
                    }
                    
                }else if(isKettle(element)){
                    primaryDir = "kettle";
                    if(!dirs[nrDirs-2].equals(primaryDir)){
                        has = true;
                    }
                    
                }
                return has;
            }
            
            private void createSublinks(){
                String [] dirs = element.getLocation().split("/");
                int nrDirs = dirs.length;
                               
                
                this.name = dirs[nrDirs-2];
                this.link = "";
                ArrayList<IElement> elements = getAllElementsOnFolder(name);
                
                for(IElement e: elements){
                    Link l = new Link(e, true);
                    subLinks.add(l);
                }
                
            }
            
            @JsonIgnore
            public String getLinkJson(){
                try {
                    logger.info(mapper.writeValueAsString(this));
                    return mapper.writeValueAsString(this);
                } catch (IOException ex) {
                    Logger.getLogger(CpkEngine.class.getName()).log(Level.SEVERE, null, ex);
                }
                return null;
            }
            
            
        }
    }
    

}

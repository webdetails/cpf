/* This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this file,
 * You can obtain one at http://mozilla.org/MPL/2.0/. */
package pt.webdetails.cpk;

import java.io.BufferedInputStream;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.codehaus.jackson.JsonNode;
import org.codehaus.jackson.map.ObjectMapper;
import org.dom4j.Document;
import org.dom4j.DocumentException;
import org.dom4j.Node;
import org.dom4j.io.SAXReader;
import pt.webdetails.cpf.repository.BaseRepositoryAccess;
import pt.webdetails.cpf.repository.IRepositoryAccess;
import pt.webdetails.cpf.repository.IRepositoryFile;
import pt.webdetails.cpf.utils.IPluginUtils;
import pt.webdetails.cpk.security.AccessControl;
import pt.webdetails.cpk.elements.IElement;
import pt.webdetails.cpk.elements.IElementType;
import pt.webdetails.cpk.testUtils.PluginUtils;
import pt.webdetails.cpf.Util;
import pt.webdetails.cpf.repository.VfsRepositoryAccess;
import pt.webdetails.cpk.CpkEngine;

/**
 *
 * @author Pedro Alves<pedro.alves@webdetails.pt>
 */
public class CpkEngineTest extends CpkEngine {

    private static CpkEngineTest instance;
    protected static Log logger = LogFactory.getLog(CpkEngineTest.class);
    private Document cpkDoc;
    private TreeMap<String, IElement> elementsMap;
    private HashMap<String, IElementType> elementTypesMap;
    private static List reserverdWords = Arrays.asList("refresh", "status", "reload");
    private String defaultElementName = null;
    protected IPluginUtils pluginUtils;
    protected IRepositoryAccess repAccess;

    public CpkEngineTest(IPluginUtils pluginUtils, IRepositoryAccess repAccess) {
        // Starting elementEngine
        logger.debug("Starting ElementEngine");
        elementsMap = new TreeMap<String, IElement>();
        elementTypesMap = new HashMap<String, IElementType>();

        this.pluginUtils = pluginUtils;
        this.repAccess=repAccess;
        try {
            this.initialize();
        } catch (Exception ex) {
            logger.fatal("Error initializing CpkEngine: " + Util.getExceptionDescription(ex));
            
        }
    }

    public CpkEngineTest() {
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
    public static boolean isInitialized()
  {
    return instance != null;
  }
    public static void init(IPluginUtils pluginUtils,IRepositoryAccess repoAccess)throws InitializationException, IOException{
        
        
        if (!isInitialized()) {

		  if (pluginUtils == null)
			  pluginUtils = new PluginUtils();

		  if (repoAccess == null)
			  repoAccess = new VfsRepositoryAccess();
		  
		  instance = new CpkEngineTest(pluginUtils,repoAccess);
	  }
    }
    private static void init() throws InitializationException, IOException{
        
        init(null,null);
        
    }
    //XXX lacking a better name
    public static CpkEngineTest getInstanceWithParams(IPluginUtils pluginUtils,IRepositoryAccess repAccess) {

        if (instance == null) {
            instance = new CpkEngineTest(pluginUtils,repAccess);
        }
        return instance;
    }

    public static CpkEngineTest getInstance() {

        if (instance == null) {
            try{
            init();
            }catch(InitializationException ie){
                logger.fatal("Initialization failed. CPK will NOT be available", ie);
            }catch(IOException e){
               
            }

        }

        return instance;
    }
 
    private synchronized void initialize() throws DocumentException, IOException {


        // Start by forcing initialization of PluginUtils
        if (pluginUtils == null) {
            logger.error("No Plugin Utils");
        }
        logger.info("Initializing CPK Plugin " + pluginUtils.getPluginName().toUpperCase());
        reload();

    }


   

    
}

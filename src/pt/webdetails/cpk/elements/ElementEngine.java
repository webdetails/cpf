/* This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this file,
 * You can obtain one at http://mozilla.org/MPL/2.0/. */
package pt.webdetails.cpk.elements;

import java.io.BufferedInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.List;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.dom4j.Document;
import org.dom4j.DocumentException;
import org.dom4j.Node;
import org.pentaho.platform.api.engine.IPluginResourceLoader;
import org.pentaho.platform.engine.core.system.PentahoSystem;
import org.pentaho.platform.util.xml.dom4j.XmlDom4JHelper;
import pt.webdetails.cpf.Util;

/**
 *
 * @author Pedro Alves<pedro.alves@webdetails.pt>
 */
public class ElementEngine {

    private static ElementEngine instance;
    protected Log logger = LogFactory.getLog(this.getClass());
    private Document cpkDoc;
    private HashMap<String, IElement> elementsMap;

    public ElementEngine() {

        // Starting elementEngine
        logger.debug("Starting ElementEngine");
        elementsMap = new HashMap<String, IElement>();


    }

    public static ElementEngine getInstance() {

        if (instance == null) {

            instance = new ElementEngine();

        }

        return instance;

    }

    public String getInfo() {

        return "TODO";

    }

    /**
     *
     * Reloads or initializes the ElementManager
     *
     */
    public void reload() throws DocumentException, IOException {

        // Clean the types
        elementsMap.clear();


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
            } catch (Exception ex) {
                logger.error("Error initializing element type " + clazz + ": " + Util.getExceptionDescription(ex));
                continue;
            }

            // Now that we have the class, scan the elements
            List<IElement> elements = elementType.scanElements(getCpkDoc().selectSingleNode("/cpk/elementTypes/elementType[@class='" + clazz + "']"));

            // Register them in the map
            for (IElement element : elements) {
                elementsMap.put(elementType.getType().toLowerCase()+"/"+element.getId().toLowerCase(), element);
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
}

/* This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this file,
 * You can obtain one at http://mozilla.org/MPL/2.0/. */
package pt.webdetails.cpk.elements;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.util.Collection;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.dom4j.DocumentException;
import org.dom4j.Node;
import org.pentaho.platform.api.engine.IPluginResourceLoader;
import org.pentaho.platform.engine.core.system.PentahoSystem;
import pt.webdetails.cpf.utils.PluginUtils;

/**
 *
 * @author Pedro Alves<pedro.alves@webdetails.pt>
 */
public abstract class AbstractElementType implements IElementType {

    protected Log logger = LogFactory.getLog(this.getClass());

    public abstract String getType();

    /**
     * Scans the location of the directory and returns a list of the content
     *
     * @param node
     */
    @Override
    public List<IElement> scanElements(Node node) {

        // Grab resource loader
        IPluginResourceLoader resLoader = PentahoSystem.get(IPluginResourceLoader.class, null);

        // Get list of files to process

        List<Node> elementLocations = node.selectNodes("elementLocations/elementLocation");

        for (Node elementLocation : elementLocations) {

            // Get the list of elements. We need to filter only the ones we want

            String elementPath = elementLocation.valueOf("@path");
            Boolean isRecursive = Boolean.parseBoolean(elementLocation.valueOf("@isRecursive"));
            String pattern = elementLocation.valueOf("@pattern");

            Collection<File> elements = PluginUtils.getInstance().getPluginResources(elementPath, isRecursive, pattern);

            // Found the list we need. Processing it!
            logger.debug("Found " + elements.size() + " elements. Filtering...");
            
            logger.warn("WORK IN PROGRESSSSSSS");

        }


        logger.info("TODO");
        return null;

    }

    @Override
    public abstract IElement registerElement(String elementLocation);

    @Override
    public abstract void processRequest();
}

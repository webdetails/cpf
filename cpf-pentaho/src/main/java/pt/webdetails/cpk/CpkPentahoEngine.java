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
import pt.webdetails.cpf.utils.IPluginUtils;
import pt.webdetails.cpk.sitemap.LinkGenerator;
import pt.webdetails.cpk.elements.IElement;
import pt.webdetails.cpk.CpkEngine;

/**
 *
 * @author Pedro Alves<pedro.alves@webdetails.pt>
 */
public class CpkPentahoEngine {

    private IPluginUtils pluginUtils;
    private TreeMap<String, IElement> elementsMap;

    public CpkPentahoEngine(IPluginUtils pluginUtils) {

        this.pluginUtils = pluginUtils;
    }

    public JsonNode getSitemapJson() throws IOException {
        if (elementsMap != null) {
            LinkGenerator linkGen = new LinkGenerator(elementsMap, pluginUtils);
            return linkGen.getLinksJson();
        }
        return null;
    }

    public void setElementsMap(TreeMap<String, IElement> map) {

        this.elementsMap = map;
    }
}
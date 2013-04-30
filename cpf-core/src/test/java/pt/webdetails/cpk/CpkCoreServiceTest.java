/* This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this file,
 * You can obtain one at http://mozilla.org/MPL/2.0/. */
package pt.webdetails.cpk;

import pt.webdetails.cpk.*;
import java.io.IOException;
import java.io.OutputStream;
import java.util.HashMap;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.Map;
import org.codehaus.jackson.JsonNode;
import org.codehaus.jackson.map.ObjectMapper;
import org.dom4j.DocumentException;
import pt.webdetails.cpf.RestRequestHandler;
import pt.webdetails.cpf.Router;
import pt.webdetails.cpf.http.ICommonParameterProvider;
import pt.webdetails.cpf.utils.IPluginUtils;
import pt.webdetails.cpk.elements.IElement;
import pt.webdetails.cpk.plugins.PluginBuilder;
import pt.webdetails.cpk.security.AccessControl;
import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.Test;
import pt.webdetails.cpk.testUtils.PluginUtils;

/**
 *
 * @author joao
 */
public class CpkCoreServiceTest {

    private static IPluginUtils pluginUtils;
    private static CpkCoreService cpkCore;
    private static Map<String,ICommonParameterProvider> map;
    
    
    @BeforeClass
    public static void setUp() {
        pluginUtils = new PluginUtils();
        cpkCore = new CpkCoreService(pluginUtils);
        map = new HashMap<String, ICommonParameterProvider>();

    }
    
    @Test
    public void testCreateContent() throws Exception {
        cpkCore.createContent(map);
        
        cpkCore.getElementsList(null);
        
        
    }
}

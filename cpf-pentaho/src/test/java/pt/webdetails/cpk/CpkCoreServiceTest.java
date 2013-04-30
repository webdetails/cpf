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
import org.pentaho.platform.api.engine.IApplicationContext;
import org.pentaho.platform.api.engine.IPentahoObjectFactory;
import org.pentaho.platform.api.engine.ISolutionEngine;
import org.pentaho.platform.engine.core.system.PentahoSystem;

import org.pentaho.platform.engine.core.system.StandaloneApplicationContext;
import org.pentaho.platform.engine.core.system.objfac.AbstractSpringPentahoObjectFactory;
import org.pentaho.platform.engine.core.system.objfac.StandaloneObjectFactory;
import org.pentaho.platform.engine.services.solution.SolutionEngine;
import pt.webdetails.cpf.utils.PluginUtils;


/**
 *
 * @author joao
 */
public class CpkCoreServiceTest {

    private static IPluginUtils pluginUtils;
    private static CpkCoreService cpkCore;
    private static Map<String,ICommonParameterProvider> map;
    
    
    @BeforeClass
    public static void setUp() {//XXX won't work, needs further looking into
        IPentahoObjectFactory objFact = new StandaloneObjectFactory();//XXX tried to force in some PentahoSystem related things to get it to work just to test
        IApplicationContext appContext= new StandaloneApplicationContext("", "");
        ISolutionEngine eng = new SolutionEngine();
        PentahoSystem p = new PentahoSystem();
        p.setObjectFactory(objFact);
        
        p.init(appContext);
        
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

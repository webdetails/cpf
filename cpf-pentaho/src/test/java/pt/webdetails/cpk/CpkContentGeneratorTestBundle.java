/* This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this file,
 * You can obtain one at http://mozilla.org/MPL/2.0/. */
package pt.webdetails.cpk;

//import java.util.HashMap;
//import java.util.Map;
//import pt.webdetails.cpf.http.ICommonParameterProvider;
//import pt.webdetails.cpf.utils.IPluginUtils;
//import org.junit.Assert;
//import org.junit.BeforeClass;
//import org.junit.Test;
//import org.pentaho.platform.api.engine.IApplicationContext;
//import org.pentaho.platform.api.engine.IPentahoObjectFactory;
//import org.pentaho.platform.api.engine.ISolutionEngine;
//import org.pentaho.platform.engine.core.system.PentahoSystem;
//import org.pentaho.platform.engine.core.system.StandaloneApplicationContext;
//import org.pentaho.platform.engine.core.system.objfac.StandaloneObjectFactory;
//import org.pentaho.platform.engine.services.solution.SolutionEngine;
//import pt.webdetails.cpf.repository.IRepositoryAccess;
//import pt.webdetails.cpf.repository.PentahoRepositoryAccess;
//import pt.webdetails.cpf.utils.PluginUtils;
//
//
///**
// *
// * @author joao
// */
//public class CpkCoreServiceTest {
//
//    private static IPluginUtils pluginUtils;
//    private static CpkCoreService cpkCore;
//    private static Map<String,ICommonParameterProvider> map;
//    private static IRepositoryAccess repAccess;
//    private static ICpkEnvironment cpkEnv;
//    
//    @BeforeClass
//    public static void setUp() {
//        IPentahoObjectFactory objFact = new StandaloneObjectFactory();
//        IApplicationContext appContext= new StandaloneApplicationContext("", "");
//        ISolutionEngine eng = new SolutionEngine();
//        PentahoSystem p = new PentahoSystem();
//        p.setObjectFactory(objFact);
//        
//        p.init(appContext);
//        
//        pluginUtils = new PluginUtils();
//        repAccess = new PentahoRepositoryAccess();
//        cpkEnv = new CpkPentahoEnvironment(pluginUtils, repAccess);
//        cpkCore = new CpkCoreService(cpkEnv);
//        map = new HashMap<String, ICommonParameterProvider>();
//        
//
//    }
//    
//    @Test
//    public void testCreateContent() throws Exception {
//       cpkCore.createContent(map);
//        
//        cpkCore.getElementsList(null);
//        
//        
//    }
//}

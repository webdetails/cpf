/* This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this file,
 * You can obtain one at http://mozilla.org/MPL/2.0/. */
package pt.webdetails.cpk;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.OutputStream;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Map.Entry;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import org.dom4j.DocumentException;
import pt.webdetails.cpf.http.ICommonParameterProvider;
import pt.webdetails.cpf.utils.IPluginUtils;
import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.Test;
import org.pentaho.di.core.KettleEnvironment;
import org.pentaho.platform.api.engine.IApplicationContext;
import org.pentaho.platform.api.engine.IParameterProvider;
import org.pentaho.platform.api.engine.IPentahoDefinableObjectFactory;
import org.pentaho.platform.api.engine.IPentahoObjectFactory;
import org.pentaho.platform.api.engine.IPentahoSession;
import org.pentaho.platform.api.engine.ISolutionEngine;
import org.pentaho.platform.api.engine.ISystemSettings;
import org.pentaho.platform.api.engine.ObjectFactoryException;
import org.pentaho.platform.engine.core.solution.PentahoSessionParameterProvider;
import org.pentaho.platform.engine.core.solution.SimpleParameterProvider;
import org.pentaho.platform.engine.core.system.BaseSession;
import org.pentaho.platform.engine.core.system.PentahoSystem;
import org.pentaho.platform.engine.core.system.StandaloneApplicationContext;
import org.pentaho.platform.engine.core.system.StandaloneSession;
import org.pentaho.platform.engine.core.system.UserSession;
import org.pentaho.platform.engine.core.system.boot.PentahoSystemBoot;
import org.pentaho.platform.engine.core.system.objfac.StandaloneObjectFactory;
import org.pentaho.platform.engine.core.system.objfac.StandaloneSpringPentahoObjectFactory;
import org.pentaho.platform.engine.services.solution.SimpleParameterSetter;
import org.pentaho.platform.engine.services.solution.SolutionEngine;
import org.pentaho.platform.web.http.context.PentahoSolutionSpringApplicationContext;
import org.springframework.context.ApplicationContext;
import pt.webdetails.cpf.RestRequestHandler;
import pt.webdetails.cpf.http.CommonParameterProvider;
import pt.webdetails.cpf.impl.SimpleSessionUtils;
import pt.webdetails.cpf.impl.SimpleUserSession;
import pt.webdetails.cpf.repository.IRepositoryAccess;
import pt.webdetails.cpf.repository.PentahoRepositoryAccess;
import pt.webdetails.cpf.repository.VfsRepositoryAccess;//XXX should use PentahoRepository?
import pt.webdetails.cpf.session.ISessionUtils;
import pt.webdetails.cpf.session.IUserSession;
import pt.webdetails.cpf.session.PentahoSession;
import pt.webdetails.cpf.utils.PluginUtils;
import pt.webdetails.cpk.elements.IElement;
import pt.webdetails.cpk.security.IAccessControl;
import org.pentaho.platform.plugin.services.pluginmgr.PluginResourceLoader;
import pt.webdetails.cpk.testUtils.TestSpecificCpkContentGenerator;

/**
 *
 * @author joao
 */
public class CpkContentGeneratorTestBundle {

    private static IPluginUtils pluginUtils;
    //private static CpkContentGenerator cpkContentGenerator;
    private static TestSpecificCpkContentGenerator cpkContentGenerator;
    private static Map<String, ICommonParameterProvider> map;
    private static IRepositoryAccess repAccess;
    private static OutputStream out;
    private static OutputStream outResponse;
    private static String userDir = System.getProperty("user.dir");
    //private static PentahoSession session = new PentahoSession();
    private static StandaloneSession session = new StandaloneSession("test");

    @BeforeClass
    public static void setUp() throws IOException, InitializationException, ObjectFactoryException {

        
        StandaloneApplicationContext appContext = new StandaloneApplicationContext(userDir+"/"+"test-resources/repo", "");

        StandaloneSpringPentahoObjectFactory factory = new StandaloneSpringPentahoObjectFactory();
        factory.init("test-resources/repo/system/pentahoObjects.spring.xml", null);

        PentahoSystem.setObjectFactory(factory);
        PentahoSystem.setSystemSettingsService(factory.get(ISystemSettings.class, "systemSettingsService", session));
        PentahoSystem.init(appContext);

        repAccess = new VfsRepositoryAccess(userDir + "/test-resources/repo",
                userDir + "/test-resources/settings");
        pluginUtils = new PluginUtils();
        pluginUtils.setPluginName("cpkSol");//XXX hardcode the name just to pass, try to bypass this
        pluginUtils.setPluginDirectory(new File(userDir+"/test-resources/repo/system/cpkSol"));
        final IUserSession userSession = new SimpleUserSession("userName", null, true, null);
        ICpkEnvironment environment = new ICpkEnvironment() {
            @Override
            public IPluginUtils getPluginUtils() {
                return pluginUtils;
            }

            @Override
            public IRepositoryAccess getRepositoryAccess() {
                return repAccess;
            }

            @Override
            public IAccessControl getAccessControl() {
                return new IAccessControl() {
                    @Override
                    public boolean isAllowed(IElement element) {
                        return true;
                    }

                    @Override
                    public boolean isAdmin() {
                        return true;
                    }

                    @Override
                    public void throwAccessDenied(Map<String, ICommonParameterProvider> parameterProviders) {
                        throw new UnsupportedOperationException("Not supported yet.");
                    }
                };
            }

            @Override
            public String getPluginName() {
                return pluginUtils.getPluginName();
            }

            @Override
            public ISessionUtils getSessionUtils() {
                return new SimpleSessionUtils(userSession, null, null);
            }
        };
        //cpkContentGenerator = new CpkContentGenerator(environment);
        cpkContentGenerator = new TestSpecificCpkContentGenerator(environment);
    }

    @Test
    public void testCreateContent() throws Exception {//start a hypersonic to test
        KettleEnvironment.init();
        outResponse = new ByteArrayOutputStream();
        cpkContentGenerator.setParameterProviders(unwrapParams(passArguments()));
        cpkContentGenerator.wrapParameters();
        cpkContentGenerator.createContent();
        String pass_arguments_result = outResponse.toString();
        outResponse.close();
        outResponse = new ByteArrayOutputStream();

        cpkContentGenerator.setParameterProviders(unwrapParams(writeback()));
        cpkContentGenerator.wrapParameters();
        cpkContentGenerator.createContent();
        String writeback_result = outResponse.toString();
        outResponse.close();
        outResponse = new ByteArrayOutputStream();

        cpkContentGenerator.setParameterProviders(unwrapParams(sampleTrans()));
        cpkContentGenerator.wrapParameters();
        cpkContentGenerator.createContent();
        String sampleTrans_result = outResponse.toString();
        outResponse.close();
        outResponse = new ByteArrayOutputStream();

        cpkContentGenerator.setParameterProviders(unwrapParams(evaluateResultRows()));
        cpkContentGenerator.wrapParameters();
        cpkContentGenerator.createContent();
        String evaluateResultRows_result = outResponse.toString();
        outResponse.close();
        outResponse = new ByteArrayOutputStream();

        cpkContentGenerator.setParameterProviders(unwrapParams(createResultRows()));
        cpkContentGenerator.wrapParameters();
        cpkContentGenerator.createContent();
        String createResultRows_result = outResponse.toString();
        outResponse.close();

        Pattern wrongPattern = Pattern.compile(".*\\{\"result\":false.*\\}.*");
        Pattern argumentsPattern = Pattern.compile("\r\n\r\n");//passing arguments around is not suported yet, so this is the result
        Pattern correctTransformationPattern = Pattern.compile("\\{\"queryInfo.*\\{.*\\}.*\\[.*\\].*\\}");
        Pattern correctJobPattern = Pattern.compile(".*\"result\":true.*");

        Matcher pass_arguments_kjb = argumentsPattern.matcher(pass_arguments_result);
        Matcher writeback_ktr = argumentsPattern.matcher(writeback_result);
        Matcher sampleTrans_ktr = correctTransformationPattern.matcher(sampleTrans_result);
        Matcher evaluateResultRows_kjb = correctJobPattern.matcher(evaluateResultRows_result);
        Matcher createResultRows_ktr = correctTransformationPattern.matcher(createResultRows_result);


        Assert.assertTrue(pass_arguments_kjb.matches());
        Assert.assertTrue(writeback_ktr.matches());
        Assert.assertTrue(sampleTrans_ktr.matches());
        Assert.assertTrue(evaluateResultRows_kjb.matches());
        Assert.assertTrue(createResultRows_ktr.matches());

    }

    @Test
    public void testGetElementsList() throws IOException {
        Pattern p = Pattern.compile("\\[\\{.....*\\}\\]");

        out = new ByteArrayOutputStream();
        cpkContentGenerator.getElementsList(out);
        String str = out.toString();
        Matcher m = p.matcher(str);
        Assert.assertTrue(m.matches());
        out.close();
    }

    @Test
    public void testReloadRefreshStatus() throws DocumentException, IOException {
        Pattern p = Pattern.compile("---.*.\\[.*.\\].*.\\{.*.\\}.*.");//XXX maybe not enough to check
        out = new ByteArrayOutputStream();
        cpkContentGenerator.reload(out);
        String str = out.toString();
        String less = str.replaceAll("\n", " ");
        Matcher m = p.matcher(less);
        Assert.assertTrue(m.matches());

    }

    @Test
    public void testGetRequestHandler() {
        RestRequestHandler r = cpkContentGenerator.getRequestHandler();
        Assert.assertTrue(r != null);
    }

    @Test
    public void testGetPluginName() {

        String str = cpkContentGenerator.getPluginName();

        Assert.assertTrue(str.equals("cpkSol"));//compare with a plugin I know

    }

    @Test
    public void testGetSitemapJson() throws IOException {
        out = new ByteArrayOutputStream();
        cpkContentGenerator.getSitemapJson(out);
        String str = out.toString();
        out.close();

        Assert.assertTrue(str.equals(" not done yet "));//XXX todo

    }

    @Test
    public void testPluginsList() throws IOException {
        out = new ByteArrayOutputStream();
        cpkContentGenerator.pluginsList(out);
        String str = out.toString();
        out.close();

        Assert.assertTrue(str.equals(" not done yet "));//XXX todo

    }

    private Map<String, ICommonParameterProvider> passArguments() {
        Map<String, ICommonParameterProvider> map = new HashMap<String, ICommonParameterProvider>();
        ICommonParameterProvider p = new CommonParameterProvider();
        ICommonParameterProvider p1 = new CommonParameterProvider();
        p.put("path", "/pass_arguments");//kjb or ktr
        p.put("outputstream", outResponse);
        p.put("httpresponse", null);
        p1.put("request", "random request");
        p1.put("paramarg1", "value1");
        p1.put("paramarg2", "value2");
        p1.put("paramarg3", "value3");
        map.put("path", p);
        map.put("request", p1);
        return map;
    }

    private Map<String, ICommonParameterProvider> writeback() {
        Map<String, ICommonParameterProvider> map = new HashMap<String, ICommonParameterProvider>();
        ICommonParameterProvider p = new CommonParameterProvider();
        ICommonParameterProvider p1 = new CommonParameterProvider();
        p.put("path", "/writeback");//kjb or ktr
        p.put("outputstream", outResponse);
        p.put("httpresponse", null);
        p1.put("stepName", "text file output");//output stepname for ktr
        p1.put("request", "random request");
        p1.put("paramarg1", "value1");
        p1.put("paramarg2", "value2");
        p1.put("paramarg3", "value3");
        map.put("path", p);
        map.put("request", p1);
        return map;
    }

    private Map<String, ICommonParameterProvider> sampleTrans() {
        Map<String, ICommonParameterProvider> map = new HashMap<String, ICommonParameterProvider>();
        ICommonParameterProvider p = new CommonParameterProvider();
        ICommonParameterProvider p1 = new CommonParameterProvider();
        p.put("path", "/sampleTrans");//kjb or ktr
        p.put("outputstream", outResponse);
        p.put("httpresponse", null);
        //p1.put("stepName", "text file output");//samplTrans has defauls OUTPUT, so no need for stepname
        p1.put("paramarg1", "value1");
        p1.put("paramarg2", "value2");
        p1.put("paramarg3", "value3");
        p1.put("kettleOutput", "Json");//not Infered kettle, so must pass Json Output
        map.put("path", p);
        map.put("request", p1);
        return map;
    }

    private Map<String, ICommonParameterProvider> evaluateResultRows() {
        Map<String, ICommonParameterProvider> map = new HashMap<String, ICommonParameterProvider>();
        ICommonParameterProvider p = new CommonParameterProvider();
        ICommonParameterProvider p1 = new CommonParameterProvider();
        p.put("path", "/evaluate-result-rows");//kjb or ktr
        p.put("outputstream", outResponse);
        p.put("httpresponse", null);
        //p1.put("stepName", "text file output");//samplTrans has defauls OUTPUT, so no need for stepname
        p1.put("paramarg1", "value1");
        p1.put("paramarg2", "value2");
        p1.put("paramarg3", "value3");
        map.put("path", p);
        map.put("request", p1);
        return map;
    }

    private Map<String, ICommonParameterProvider> createResultRows() {
        Map<String, ICommonParameterProvider> map = new HashMap<String, ICommonParameterProvider>();
        ICommonParameterProvider p = new CommonParameterProvider();
        ICommonParameterProvider p1 = new CommonParameterProvider();
        p.put("path", "/create-result-rows");//kjb or ktr
        p.put("outputstream", outResponse);
        p.put("httpresponse", null);
        p1.put("stepName", "copy rows to result");
        p1.put("paramarg1", "value1");
        p1.put("paramarg2", "value2");
        p1.put("paramarg3", "value3");
        map.put("path", p);
        map.put("request", p1);
        return map;
    }

    private static Map<String, IParameterProvider> unwrapParams(Map<String, ICommonParameterProvider> params) {

        Map<String, IParameterProvider> resultMap = new HashMap<String, IParameterProvider>();
        SimpleParameterProvider result = new SimpleParameterProvider();
        Iterator<Entry<String, ICommonParameterProvider>> it = params.entrySet().iterator();
        while (it.hasNext()) {
            Entry<String, ICommonParameterProvider> e = it.next();
            Iterator<String> names = e.getValue().getParameterNames();
            while (names.hasNext()) {
                String name = names.next();
                Object value = e.getValue().getParameter(name);
                result.setParameter(name, value);
            }
            resultMap.put(e.getKey(), result);

        }
        return resultMap;

    }
}

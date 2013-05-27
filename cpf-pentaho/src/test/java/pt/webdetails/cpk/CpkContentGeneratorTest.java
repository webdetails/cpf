/* This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this file,
 * You can obtain one at http://mozilla.org/MPL/2.0/. */
package pt.webdetails.cpk;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Map.Entry;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import org.codehaus.jettison.json.JSONArray;
import org.codehaus.jettison.json.JSONException;
import org.codehaus.jettison.json.JSONObject;
import org.dom4j.DocumentException;
import pt.webdetails.cpf.http.ICommonParameterProvider;
import pt.webdetails.cpf.utils.IPluginUtils;
import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.Test;
import org.pentaho.di.core.KettleEnvironment;
import org.pentaho.platform.api.engine.IParameterProvider;
import org.pentaho.platform.api.engine.ISystemSettings;
import org.pentaho.platform.api.engine.ObjectFactoryException;
import org.pentaho.platform.engine.core.solution.SimpleParameterProvider;
import org.pentaho.platform.engine.core.system.PentahoSessionHolder;
import org.pentaho.platform.engine.core.system.PentahoSystem;
import org.pentaho.platform.engine.core.system.StandaloneApplicationContext;
import org.pentaho.platform.engine.core.system.StandaloneSession;
import org.pentaho.platform.engine.core.system.objfac.StandaloneSpringPentahoObjectFactory;
import org.pentaho.platform.engine.security.SecurityHelper;
import pt.webdetails.cpf.RestRequestHandler;
import pt.webdetails.cpf.http.CommonParameterProvider;
import pt.webdetails.cpf.repository.IRepositoryAccess;
import pt.webdetails.cpf.utils.PluginUtils;
import org.springframework.security.Authentication;
import org.springframework.security.GrantedAuthority;
import org.springframework.security.GrantedAuthorityImpl;
import org.springframework.security.providers.UsernamePasswordAuthenticationToken;
import pt.webdetails.cpf.plugins.Plugin;
import pt.webdetails.cpk.testUtils.CpkContentGeneratorForTesting;
import pt.webdetails.cpf.plugin.CorePlugin;
import pt.webdetails.cpk.testUtils.PentahoRepositoryAccessForTesting;

/**
 *
 * @author joao
 */
public class CpkContentGeneratorTest {

    private static IPluginUtils pluginUtils;
    //private static CpkContentGenerator cpkContentGenerator;
    private static CpkContentGeneratorForTesting cpkContentGenerator;
    //private static Map<String, ICommonParameterProvider> map;
    private static IRepositoryAccess repAccess;
    private static OutputStream out;
    private static OutputStream outResponse;
    private static String userDir = System.getProperty("user.dir");
    //private static PentahoSession session = new PentahoSession();
    private static StandaloneSession session = new StandaloneSession("joe");

    @BeforeClass
    public static void setUp() throws IOException, InitializationException, ObjectFactoryException {


        StandaloneApplicationContext appContext = new StandaloneApplicationContext(userDir + "/" + "test-resources/repo", "");

        StandaloneSpringPentahoObjectFactory factory = new StandaloneSpringPentahoObjectFactory();
        factory.init("test-resources/repo/system/pentahoObjects.spring.xml", null);


        GrantedAuthority[] roles = new GrantedAuthority[2];
        roles[0] = new GrantedAuthorityImpl("Authenticated"); //$NON-NLS-1$
        roles[1] = new GrantedAuthorityImpl("Admin"); //$NON-NLS-1$
        Authentication auth = new UsernamePasswordAuthenticationToken("joe", "password", roles); //$NON-NLS-1$
        session.setAttribute(SecurityHelper.SESSION_PRINCIPAL, auth);

        
        PentahoSessionHolder.setSession(session);
        PentahoSystem.setObjectFactory(factory);
        PentahoSystem.setSystemSettingsService(factory.get(ISystemSettings.class, "systemSettingsService", session));
        PentahoSystem.init(appContext);

        pluginUtils = new PluginUtils();
        CorePlugin plugin = new Plugin(pluginUtils.getPluginDirectory().getPath());
        repAccess = new PentahoRepositoryAccessForTesting();
        repAccess.setPlugin(plugin);
        //final IUserSession userSession = new SimpleUserSession("userName", null, true, null);
        ICpkEnvironment environment = new CpkPentahoEnvironment(pluginUtils, repAccess);
        //cpkContentGenerator = new CpkContentGenerator(environment);
        cpkContentGenerator = new CpkContentGeneratorForTesting(environment);


    }

    @Test
    public void testCreateContent() throws Exception {
        KettleEnvironment.init();
        outResponse = new ByteArrayOutputStream();
        cpkContentGenerator.setParameterProviders(unwrapParams(passArguments()));
        //cpkContentGenerator.initParams();
        cpkContentGenerator.wrapParameters();
        cpkContentGenerator.createContent();
        String pass_arguments_result = outResponse.toString();
        outResponse.close();
        outResponse = new ByteArrayOutputStream();

        cpkContentGenerator.setParameterProviders(unwrapParams(writeback()));
        //cpkContentGenerator.initParams();
        cpkContentGenerator.wrapParameters();
        cpkContentGenerator.createContent();
        String writeback_result = outResponse.toString();
        outResponse.close();
        outResponse = new ByteArrayOutputStream();

        cpkContentGenerator.setParameterProviders(unwrapParams(sampleTrans()));
        //cpkContentGenerator.initParams();
        cpkContentGenerator.wrapParameters();
        cpkContentGenerator.createContent();
        String sampleTrans_result = outResponse.toString();
        outResponse.close();
        outResponse = new ByteArrayOutputStream();

        cpkContentGenerator.setParameterProviders(unwrapParams(evaluateResultRows()));
        //cpkContentGenerator.initParams();
        cpkContentGenerator.wrapParameters();
        cpkContentGenerator.createContent();
        String evaluateResultRows_result = outResponse.toString();
        outResponse.close();
        outResponse = new ByteArrayOutputStream();

        cpkContentGenerator.setParameterProviders(unwrapParams(createResultRows()));
        //cpkContentGenerator.initParams();
        cpkContentGenerator.wrapParameters();
        cpkContentGenerator.createContent();
        String createResultRows_result = outResponse.toString();
        outResponse.close();

        Pattern wrongPattern = Pattern.compile(".*\\{\"result\":false.*\\}.*");
        Pattern argumentsPattern = Pattern.compile("\r\n\r\n");//XXX probably wrong, check
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
    public void testGetSitemapJson() throws IOException, JSONException {

        boolean successful = true;
        boolean sublinksExist = false;
        out = new ByteArrayOutputStream();
        cpkContentGenerator.getSitemapJson(out);
        String str = out.toString();
        System.out.println(str);
        out.close();

        JSONArray json = null;
        try {
            json = new JSONArray(str);
        } catch (JSONException ex) {
            Logger.getLogger(CpkContentGeneratorTest.class.getName()).log(Level.SEVERE, null, ex);
            Assert.fail(" # - Error parsing the JSON string");
        }

        for (int i = 0; i < json.length(); i++) {
            JSONObject obj = json.getJSONObject(i);
            String name = obj.getString("name");
            String id = obj.getString("id");
            String link = obj.getString("link");
            JSONArray sublinks = obj.getJSONArray("sublinks");
            if (sublinks.length() > 0) {
                sublinksExist = true;
            }
            if (!"null".equals(name) && !" null".equals(id) && !"null".equals(link)) {
            } else {
                successful = false;
                break;
            }
        }
        Assert.assertTrue(successful && sublinksExist);


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
            result = new SimpleParameterProvider();

        }
        return resultMap;

    }
}

/* This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this file,
 * You can obtain one at http://mozilla.org/MPL/2.0/. */
package pt.webdetails.cpk;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.util.HashMap;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import org.dom4j.Document;
import org.dom4j.DocumentException;
import org.dom4j.io.SAXReader;
import pt.webdetails.cpf.http.ICommonParameterProvider;
import pt.webdetails.cpf.session.ISessionUtils;
import pt.webdetails.cpf.utils.IPluginUtils;
import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.Assert;
import pt.webdetails.cpf.RestRequestHandler;
import pt.webdetails.cpf.http.CommonParameterProvider;
import pt.webdetails.cpf.impl.SimpleSessionUtils;
import pt.webdetails.cpf.impl.SimpleUserSession;
import pt.webdetails.cpf.repository.IRepositoryAccess;
import pt.webdetails.cpk.elements.IElement;
import pt.webdetails.cpk.security.IAccessControl;
import pt.webdetails.cpk.testUtils.PluginUtils;
import pt.webdetails.cpf.repository.VfsRepositoryAccess;
import pt.webdetails.cpf.session.IUserSession;
import org.pentaho.di.core.KettleEnvironment;
import pt.webdetails.cpf.repository.BaseRepositoryAccess;
import pt.webdetails.cpf.repository.IRepositoryFile;

/**
 *
 * @author joao
 */
public class CpkCoreServiceTest {

    private static IPluginUtils pluginUtils;
    private static CpkCoreServiceForTesting cpkCore;
    private static Map<String, ICommonParameterProvider> map;
    private static IRepositoryAccess repAccess;
    private static OutputStream out;
    private static OutputStream outResponse;
    private static String userDir = System.getProperty("user.dir");

    @BeforeClass
    public static void setUp() throws IOException, InitializationException {

        repAccess = new VfsRepositoryAccess(userDir + "/test-resources/repo",
                userDir + "/test-resources/settings");
        pluginUtils = new PluginUtils();
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

            @Override
            public void reload() {
               
            }
        };
        cpkCore = new CpkCoreServiceForTesting(environment);
        map = new HashMap<String, ICommonParameterProvider>();
        ICommonParameterProvider p = new CommonParameterProvider();
        ICommonParameterProvider p1 = new CommonParameterProvider();
        outResponse = new ByteArrayOutputStream();
        p.put("path", "/pass_arguments");//kjb or ktr
        p.put("outputstream", outResponse);
        p.put("httpresponse", null);
        p1.put("request", "unnecessary value?");
        p1.put("paramarg1", "value1");
        p1.put("paramarg2", "value2");
        p1.put("paramarg3", "value3");
        map.put("path", p);
        map.put("request", p1);

    }

    @Test
    public void testCreateContent() throws Exception {//start a hypersonic to test
        KettleEnvironment.init();
        outResponse = new ByteArrayOutputStream();

        cpkCore.createContent(passArguments());
        String pass_arguments_result = outResponse.toString();
        outResponse.close();
        outResponse = new ByteArrayOutputStream();

        cpkCore.createContent(writeback());
        String writeback_result = outResponse.toString();
        outResponse.close();
        outResponse = new ByteArrayOutputStream();

        cpkCore.createContent(sampleTrans());
        String sampleTrans_result = outResponse.toString();
        outResponse.close();
        outResponse = new ByteArrayOutputStream();

        cpkCore.createContent(evaluateResultRows());
        String evaluateResultRows_result = outResponse.toString();
        outResponse.close();
        outResponse = new ByteArrayOutputStream();

        cpkCore.createContent(createResultRows());
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
        cpkCore.getElementsList(out);
        String str = out.toString();
        Matcher m = p.matcher(str);
        Assert.assertTrue(m.matches());
        out.close();
    }

    @Test
    public void testReloadRefreshStatus() throws DocumentException, IOException {
        Pattern p = Pattern.compile("---.*.\\[.*.\\].*.\\{.*.\\}.*.");//XXX maybe not enough to check
        out = new ByteArrayOutputStream();
        cpkCore.reload(out, map);
        String str = out.toString();
        String less = str.replaceAll("\n", " ");
        Matcher m = p.matcher(less);
        Assert.assertTrue(m.matches());

    }

    @Test
    public void testGetRequestHandler() {
        RestRequestHandler r = cpkCore.getRequestHandler();
        Assert.assertTrue(r != null);
    }

    @Test
    public void testGetPluginName() {

        String str = cpkCore.getPluginName();

        Assert.assertTrue(str.equals("cpkSol"));//compare with a plugin I know

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
}

/* This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this file,
 * You can obtain one at http://mozilla.org/MPL/2.0/. */
package pt.webdetails.cpk;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.util.HashMap;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import org.dom4j.DocumentException;
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

/**
 *
 * @author joao
 */
public class CpkCoreServiceTestBundle {

    private static IPluginUtils pluginUtils;
    private static CpkCoreServiceTest cpkCore;
    private static Map<String,ICommonParameterProvider> map;
    private static IRepositoryAccess repAccess;
    private static OutputStream out;
    private static OutputStream outResponse;
    private static String userDir=System.getProperty("user.dir");
    @BeforeClass
    public static void setUp() throws IOException, InitializationException {
    
        repAccess= new VfsRepositoryAccess(userDir+"/test-resources/repo",
                                            userDir+"/test-resources/settings");
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
    };
        cpkCore = new CpkCoreServiceTest(environment);
        map = new HashMap<String, ICommonParameterProvider>();
        ICommonParameterProvider p = new CommonParameterProvider();
        ICommonParameterProvider p1 = new CommonParameterProvider();
        outResponse = new ByteArrayOutputStream();
        p.put("path", "/pass_arguments");//kjb or ktr
        p.put("outputstream", outResponse);
        p.put("httpresponse", null);
        p1.put("request","random request");
        p1.put("stepName", "text file output");//stepname for ktr
        map.put("path", p);
        map.put("request", p1);

    }
    
    @Test
    public void testCreateContent() throws Exception {
         KettleEnvironment.init();
         cpkCore.createContent(map);
         String str = outResponse.toString();
         Pattern wrongKjb = Pattern.compile("\\{\"result\":false.....*\\}");
         Pattern rightKjb = Pattern.compile("\\{\"result\":true.....*\\}");
         Pattern rightKtr = Pattern.compile("....");//XXX still to do
         Matcher wrongKjbMatch=wrongKjb.matcher(str);
         Matcher rightKjbMatch=rightKjb.matcher(str);
         Matcher rightKtrMatch=rightKtr.matcher(str);
         
         Assert.assertTrue(wrongKjbMatch.matches()||rightKtrMatch.matches());
        
    }
    
    @Test
    public void testGetElementsList() throws IOException{
        Pattern p = Pattern.compile("\\[\\{.....*\\}\\]");
        
        out = new ByteArrayOutputStream();
        cpkCore.getElementsList(out);
        String str = out.toString();
        Matcher m = p.matcher(str);
        Assert.assertTrue(m.matches());
        out.close();
    }
    
    @Test
    public void testReloadRefreshStatus() throws DocumentException, IOException{
        Pattern p = Pattern.compile("---.*.\\[.*.\\].*.\\{.*.\\}.*.");//XXX maybe not enough to check
        out = new ByteArrayOutputStream();
        cpkCore.reload(out, map);
        String str = out.toString();
        String less = str.replaceAll("\n", " ");
        Matcher m = p.matcher(less);
        Assert.assertTrue(m.matches());
        
    }
    
    @Test
    public void testGetRequestHandler(){
         RestRequestHandler r = cpkCore.getRequestHandler();
         Assert.assertTrue(r!=null);
     }
     @Test
    public void testGetPluginName() {
         
         String str = cpkCore.getPluginName();
         
         Assert.assertTrue(str.equals("cpkSol"));//compare with a plugin i know
         
     }

    
}

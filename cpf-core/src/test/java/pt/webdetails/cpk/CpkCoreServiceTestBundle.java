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
import pt.webdetails.cpf.utils.IPluginUtils;
import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.Assert;
import pt.webdetails.cpf.RestRequestHandler;
import pt.webdetails.cpf.http.CommonParameterProvider;
import pt.webdetails.cpf.repository.IRepositoryAccess;
import pt.webdetails.cpk.testUtils.PluginUtils;
import pt.webdetails.cpf.repository.VfsRepositoryAccess;

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
        cpkCore = new CpkCoreServiceTest(pluginUtils,repAccess);
        map = new HashMap<String, ICommonParameterProvider>();
        ICommonParameterProvider p = new CommonParameterProvider();
        ICommonParameterProvider p1 = new CommonParameterProvider();
        outResponse = new ByteArrayOutputStream();
        p.put("path", "/pass_arguments");//diferent .kjb files give diferent errors(pass_arguments,createPlugin)
        p.put("stepname", "OUTPUT");
        p.put("outputstream", outResponse);
        p.put("httpresponse", null);
        p1.put("request","random request");
        map.put("path", p);
        map.put("request", p1);

    }
    
    @Test
    public void testCreateContent() throws Exception {
        
        cpkCore.createContent(map);
        String str = outResponse.toString();//XXX the steps seem to be runing fine, check with a real .kjb file
        
        System.out.println(str);
        
    }
    
    @Test
    public void testGetElementsList() throws IOException{
        Pattern p = Pattern.compile("\\[\\{.....*\\}\\]");
        
        out = new ByteArrayOutputStream();
        cpkCore.getElementsList(out);
        String str = out.toString();
        Matcher m = p.matcher(str);
        Assert.assertTrue(m.matches());
        System.out.println(str);
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
    
    /*@Test
    public void testPluginList(){//XXX pass to CpkContentGeneratorTestBundle
        out = new ByteArrayOutputStream();
        cpkCore.pluginsList(out);///XXX needs fine tuning on vfsRepositoryAccess getSolutionPath
        String str = out.toString();
        
        Assert.assertTrue(str!=null);
        
        
    }*/
    
  /*  @Test
   * public void testGetSitemapJson() throws IOException{//XXX pass to CpkContentGeneratorTestBundle
        out = new ByteArrayOutputStream();
        cpkCore.getSitemapJson(out);
        String str = out.toString();
        
        Assert.assertTrue(str.equals("null"));
        
    }*/
     @Test
    public void testGetRequestHandler(){
         RestRequestHandler r = cpkCore.getRequestHandler();//XXX Just testing if router requesthandler is null, other way of test?
         Assert.assertTrue(r!=null);
     }
     @Test
    public void testGetPluginName() {
         
         String str = cpkCore.getPluginName();
         
         Assert.assertTrue(str.equals("cpkSol"));//compare with a plugin i know
         
     }

    
}

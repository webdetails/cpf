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
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpServletResponseWrapper;
import org.dom4j.DocumentException;
import pt.webdetails.cpf.http.ICommonParameterProvider;
import pt.webdetails.cpf.utils.IPluginUtils;
import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.Assert;
import pt.webdetails.cpf.http.CommonParameterProvider;
import pt.webdetails.cpf.repository.IRepositoryAccess;
import pt.webdetails.cpk.testUtils.PluginUtils;
import pt.webdetails.cpk.testUtils.VfsRepositoryAccess;
import pt.webdetails.cpk.testUtils.VfsRepositoryFile;
/**
 *
 * @author joao
 */
public class CpkCoreServiceTest {

    private static IPluginUtils pluginUtils;
    private static CpkCoreService cpkCore;
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
        cpkCore = new CpkCoreService(pluginUtils,repAccess);
        map = new HashMap<String, ICommonParameterProvider>();
        ICommonParameterProvider p = new CommonParameterProvider();
        ICommonParameterProvider p1 = new CommonParameterProvider();
        outResponse = new ByteArrayOutputStream();
        p.put("path", "/createPlugin");
        p.put("stepname", "OUTPUT");
        p.put("outputstream", outResponse);
        p.put("httpresponse", null);
        p1.put("request","random request");
        map.put("path", p);
        map.put("request", p1);

    }
    
    @Test
    public void testCreateContent() throws Exception {
        //outResponse...
        
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
        out = new ByteArrayOutputStream();
        cpkCore.reload(out, map);
        
    }
}

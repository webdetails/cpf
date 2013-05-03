/* This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this file,
 * You can obtain one at http://mozilla.org/MPL/2.0/. */
package pt.webdetails.cpk;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.util.HashMap;
import java.util.Map;
import pt.webdetails.cpf.http.ICommonParameterProvider;
import pt.webdetails.cpf.utils.IPluginUtils;
import org.junit.BeforeClass;
import org.junit.Test;
import org.pentaho.di.core.util.Assert;
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
    private static String userDir=System.getProperty("user.dir");
    @BeforeClass
    public static void setUp() throws IOException {
    
        repAccess= new VfsRepositoryAccess(userDir+"/test-resources/repo",
                                            userDir+"/test-resources/settings");
        pluginUtils = new PluginUtils();
        cpkCore = new CpkCoreService(pluginUtils,repAccess);
        map = new HashMap<String, ICommonParameterProvider>();
        ICommonParameterProvider p = new CommonParameterProvider();
        
        p.put("path", "/elem1");//XXX change path to any other to test getElementList, createContent still broken
        map.put("path", p);

    }
    
    @Test
    public void testCreateContent() throws Exception {
        cpkCore.createContent(map);
        
        
        out = new ByteArrayOutputStream();
        cpkCore.getElementsList(out);
        String str = out.toString();
        Assert.assertTrue(str!=null);
        System.out.println(str);
        out.close();
        
        
    }
}

/* This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this file,
 * You can obtain one at http://mozilla.org/MPL/2.0/. */
package pt.webdetails.cpk;

import java.util.HashMap;
import java.util.Map;
import pt.webdetails.cpf.http.ICommonParameterProvider;
import pt.webdetails.cpf.utils.IPluginUtils;
import org.junit.BeforeClass;
import org.junit.Test;
import pt.webdetails.cpf.http.CommonParameterProvider;
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
        ICommonParameterProvider p = new CommonParameterProvider();
        p.put("path", "/home/joao/work/cpf/cpf-core/repo/plugin.xml");
        map.put("path", p);

    }
    
    @Test
    public void testCreateContent() throws Exception {
        cpkCore.createContent(map);
        
        cpkCore.getElementsList(null);
        
        
    }
}

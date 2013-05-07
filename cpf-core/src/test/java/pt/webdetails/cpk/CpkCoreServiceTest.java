/* This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this file,
 * You can obtain one at http://mozilla.org/MPL/2.0/. */
package pt.webdetails.cpk;
import java.io.IOException;
import java.io.OutputStream;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.Map;
import org.codehaus.jackson.JsonNode;
import org.codehaus.jackson.map.ObjectMapper;
import org.dom4j.DocumentException;
import pt.webdetails.cpf.RestRequestHandler;
import pt.webdetails.cpf.Router;
import pt.webdetails.cpf.http.ICommonParameterProvider;
import pt.webdetails.cpf.repository.IRepositoryAccess;
import pt.webdetails.cpf.utils.IPluginUtils;
import pt.webdetails.cpk.elements.IElement;
import pt.webdetails.cpk.security.AccessControl;


/**
 *
 * @author joao
 */
public class CpkCoreServiceTest extends CpkCoreService {
    

    private static final long serialVersionUID = 1L;
    public static final String CDW_EXTENSION = ".cdw";
    public static final String PLUGIN_NAME = "cpk";
    private static final String ENCODING = "UTF-8";
    private CpkEngineTest cpkEngine;
    private final String PLUGIN_UTILS = "PluginUtils";
    private IPluginUtils pluginUtils;
    private IRepositoryAccess repAccess;
    private static final Logger logger = Logger.getLogger(CpkCoreServiceTest.class.getName());
   
    public CpkCoreServiceTest(IPluginUtils pluginUtils,IRepositoryAccess repAccess) {
        
        // this.pluginUtils=pluginUtils;
        // this.repAccess=repAccess;
        super(pluginUtils,repAccess);
        try {
            CpkEngineTest.init(pluginUtils, repAccess);
        } catch (InitializationException ex) {
            Logger.getLogger(CpkCoreServiceTest.class.getName()).log(Level.SEVERE, null, ex);
        } catch (IOException ex) {
            Logger.getLogger(CpkCoreServiceTest.class.getName()).log(Level.SEVERE, null, ex);
        }
        cpkEngine=CpkEngineTest.getInstance();
        
    }

    

}

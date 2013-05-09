/* This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this file,
 * You can obtain one at http://mozilla.org/MPL/2.0/. */
package pt.webdetails.cpk;

import java.io.IOException;
import org.dom4j.DocumentException;

/**
 *
 * @author Pedro Alves<pedro.alves@webdetails.pt>
 */
public class CpkEngineTest extends CpkEngine {

    private static CpkEngineTest instance;

    public CpkEngineTest(ICpkEnvironment environment) {
      super(environment);
    }

    public static boolean isInitialized()
  {
    return instance != null;
  }
    public static void init(ICpkEnvironment environment)throws InitializationException, IOException{
        
        
        if (!isInitialized()) {

		  
		  instance = new CpkEngineTest(environment);
	  }
    }
    private static void init() throws InitializationException, IOException{
        
        init(null);
        
    }
    
    public static CpkEngineTest getInstanceWithEnvironment(ICpkEnvironment environment) {

        if (instance == null) {
            instance = new CpkEngineTest(environment);
        }
        return instance;
    }

    public static CpkEngineTest getInstance() {

        if (instance == null) {
            try{
            init();
            }catch(InitializationException ie){
                logger.fatal("Initialization failed. CPK will NOT be available", ie);
            }catch(IOException e){
               
            }

        }

        return instance;
    }
 
    private synchronized void initialize() throws DocumentException, IOException {


        // Start by forcing initialization of PluginUtils
        logger.info("Initializing CPK Plugin " + environment.getPluginName().toUpperCase());
        reload();

    }


   

    
}

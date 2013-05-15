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
public class CpkEngineForTesting extends CpkEngine {

    private static CpkEngineForTesting instance;

    public CpkEngineForTesting(ICpkEnvironment environment) {
      super(environment);
    }

    public static boolean isInitialized()
  {
    return instance != null;
  }
    public static void init(ICpkEnvironment environment)throws InitializationException, IOException{
        
        
        if (!isInitialized()) {

		  
		  instance = new CpkEngineForTesting(environment);
	  }
    }
    private static void init() throws InitializationException, IOException{
        
        init(null);
        
    }
    
    public static CpkEngineForTesting getInstanceWithEnvironment(ICpkEnvironment environment) {

        if (instance == null) {
            instance = new CpkEngineForTesting(environment);
        }
        return instance;
    }

    public static CpkEngineForTesting getInstance() {

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
 
    protected synchronized void initialize() throws DocumentException, IOException {


        // Start by forcing initialization of PluginUtils
        logger.info("Initializing CPK Plugin " + cpkEnv.getPluginName().toUpperCase());
        reload();

    }


   

    
}

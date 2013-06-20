/* This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this file,
 * You can obtain one at http://mozilla.org/MPL/2.0/. */
package pt.webdetails.cpk.testUtils;

import java.util.HashMap;
import java.util.Map;
import org.pentaho.platform.api.engine.IParameterProvider;
import pt.webdetails.cpf.WrapperUtils;
import pt.webdetails.cpf.http.ICommonParameterProvider;
import pt.webdetails.cpk.CpkContentGenerator;
import pt.webdetails.cpk.CpkCoreService;
import pt.webdetails.cpk.CpkPentahoEnvironment;


public class CpkContentGeneratorForTesting extends CpkContentGenerator {

    private static final long serialVersionUID = 1L;

    public CpkContentGeneratorForTesting( ) {
        super();
        this.cpkEnv = new CpkPentahoEnvironment(pluginUtils, new PentahoRepositoryAccessForTesting());
        this.coreService = new CpkCoreService(cpkEnv);
    }

    public void wrapParameters(){
        if (parameterProviders != null) {
            map = new HashMap<String, ICommonParameterProvider>();
            for (Map.Entry<String, IParameterProvider> entry : parameterProviders.entrySet()) {
                map.put(entry.getKey(), WrapperUtils.wrapParamProvider(entry.getValue()));
            }
        }

    }
}

/* This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this file,
 * You can obtain one at http://mozilla.org/MPL/2.0/. */
package pt.webdetails.cpf.datasources;

import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import pt.webdetails.cpf.InterPluginCall;

/**
 *
 * @author pdpi
 */
public class CdaDatasource implements Datasource {

    private Map<String, Object> requestMap = new HashMap<String, Object>();
    private static final Log logger = LogFactory.getLog(CdaDatasource.class);

    public CdaDatasource() {
    }

    private String getQueryData() {

        InterPluginCall pluginCall = new InterPluginCall(InterPluginCall.CDA, "doQuery", requestMap);
        //TODO:response
        return pluginCall.callInPluginClassLoader();
    }
    
    public String execute() {
        return getQueryData();
    }

    public void setParameter(String param, String val) {
        requestMap.put("param" + param, val);
    }

    public void setParameter(String param, String[] val) {
        requestMap.put("param" + param, val);
    }

    public void setParameter(String param, Date val) {
        requestMap.put("param" + param, val);
    }

    public void setParameter(String param, List<Object> val) {
        requestMap.put("param" + param, val.toArray());
    }

    public void setDataAccessId(String id) {
        requestMap.put("dataAccessId", id);
    }

    public void setDefinitionFile(String file) {
        requestMap.put("path", file);
    }
}
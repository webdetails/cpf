/*! ******************************************************************************
 *
 * Pentaho
 *
 * Copyright (C) 2024 by Hitachi Vantara, LLC : http://www.pentaho.com
 *
 * Use of this software is governed by the Business Source License included
 * in the LICENSE.TXT file.
 *
 * Change Date: 2028-08-13
 ******************************************************************************/


package pt.webdetails.cpf.datasources;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import pt.webdetails.cpf.InterPluginCall;

import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class CdaDatasource implements Datasource {

    private Map<String, Object> requestMap = new HashMap<String, Object>();
    private static final Log logger = LogFactory.getLog(CdaDatasource.class);

    public CdaDatasource() {
    }

    private String getQueryData() {

         InterPluginCall pluginCall = new InterPluginCall(InterPluginCall.CDA, null, "doQueryInterPlugin", requestMap);
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
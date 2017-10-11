/*!
* Copyright 2002 - 2017 Webdetails, a Hitachi Vantara company.  All rights reserved.
*
* This software was developed by Webdetails and is provided under the terms
* of the Mozilla Public License, Version 2.0, or any later version. You may not use
* this file except in compliance with the license. If you need a copy of the license,
* please go to  http://mozilla.org/MPL/2.0/. The Initial Developer is Webdetails.
*
* Software distributed under the Mozilla Public License is distributed on an "AS IS"
* basis, WITHOUT WARRANTY OF ANY KIND, either express or  implied. Please refer to
* the license for the specific language governing your rights and limitations.
*/

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
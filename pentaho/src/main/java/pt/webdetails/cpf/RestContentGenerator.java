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

package pt.webdetails.cpf;

import java.util.Map;
import javax.servlet.http.HttpServletRequest;
import pt.webdetails.cpf.RestRequestHandler.HttpMethod;
import pt.webdetails.cpf.http.ICommonParameterProvider;
import pt.webdetails.cpf.utils.IPluginUtils;
import pt.webdetails.cpf.utils.PluginUtils;

public abstract class RestContentGenerator extends SimpleContentGenerator {

    private static final long serialVersionUID = 1L;
    protected Map<String, ICommonParameterProvider> map;
    protected IPluginUtils pluginUtils;


    public abstract RestRequestHandler getRequestHandler();

    @Override
    public void createContent() throws Exception {

        RestRequestHandler router = getRequestHandler();


        String path = pluginUtils.getPathParameters(map).getStringParameter("path", null);
        if (router.canHandle(getHttpMethod(), path)) {
            router.route(getHttpMethod(), path, getResponseOutputStream(router.getResponseMimeType()),
                    pluginUtils.getPathParameters(map),
                    pluginUtils.getRequestParameters(map));
        } else {
            super.createContent();
        }
    }

    public HttpMethod getHttpMethod() {
        HttpServletRequest request = ((PluginUtils) pluginUtils).getRequest(map);
        String method = (request == null) ? null : request.getMethod();
        return (method != null) ? HttpMethod.valueOf(method) : HttpMethod.GET;
    }
}

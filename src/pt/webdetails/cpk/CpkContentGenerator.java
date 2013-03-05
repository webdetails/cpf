/* This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this file,
 * You can obtain one at http://mozilla.org/MPL/2.0/. */
package pt.webdetails.cpk;

import java.io.IOException;
import java.io.OutputStream;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import javax.servlet.ServletRequest;

import org.apache.commons.lang.StringUtils;
import org.dom4j.DocumentException;
import org.pentaho.platform.api.engine.IParameterProvider;


import pt.webdetails.cpf.InterPluginCall;
import pt.webdetails.cpf.RestContentGenerator;
import pt.webdetails.cpf.RestRequestHandler;
import pt.webdetails.cpf.Router;
import pt.webdetails.cpf.annotations.AccessLevel;
import pt.webdetails.cpf.annotations.Exposed;
import pt.webdetails.cpf.utils.PluginUtils;

/**
 *
 * @author pdpi
 */
public class CpkContentGenerator extends RestContentGenerator {

    private static final long serialVersionUID = 1L;
    public static final String CDW_EXTENSION = ".cdw";
    public static final String PLUGIN_NAME = "cpk";
    private CpkEngine cpkEngine;

    @Override
    public void createContent() throws Exception {

        // Make sure we have the engine running
        cpkEngine = CpkEngine.getInstance();


        debug("Creating content");

        super.createContent();
    }

    @Exposed(accessLevel = AccessLevel.PUBLIC)
    public void reload(OutputStream out) throws DocumentException, IOException {
        
        // alias to refresh
        return refresh(out);
    }

    @Exposed(accessLevel = AccessLevel.PUBLIC)
    public void refresh(OutputStream out) throws DocumentException, IOException {

        logger.info("Refreshing CPK plugin " + getPluginName());

        cpkEngine.reload();
        status(out);


    }

    @Exposed(accessLevel = AccessLevel.PUBLIC)
    public void status(OutputStream out) throws DocumentException, IOException {

        logger.info("Showing status for CPK plugin " + getPluginName());

        setResponseHeaders("text/plain");
        out.write(cpkEngine.getStatus().getBytes("UTF-8"));

    }

    @Exposed(accessLevel = AccessLevel.PUBLIC)
    public void home(OutputStream out) throws UnsupportedEncodingException, IOException {
        callCDE("validations.wcdf", out);
    }

    @Override
    public String getPluginName() {

        return PluginUtils.getInstance().getPluginName();
    }

    private void callCDE(String file, OutputStream out) throws UnsupportedEncodingException, IOException {

        ServletRequest wrapper = getRequest();
        String root = wrapper.getScheme() + "://" + wrapper.getServerName() + ":" + wrapper.getServerPort();

        Map<String, Object> params = new HashMap<String, Object>();
        params.put("solution", "system");
        params.put("path", "cdv/presentation/");
        params.put("file", file);
        params.put("absolute", "true");
        params.put("inferScheme", "false");
        params.put("root", root);
        IParameterProvider requestParams = getRequestParameters();
        copyParametersFromProvider(params, requestParams);

        if (requestParams.hasParameter("mode") && requestParams.getStringParameter("mode", "Render").equals("edit")) {
            redirectToCdeEditor(out, params);
            return;
        }

        InterPluginCall pluginCall = new InterPluginCall(InterPluginCall.CDE, "Render", params);
        pluginCall.setResponse(getResponse());
        pluginCall.setOutputStream(out);
        pluginCall.run();
    }

    private void redirectToCdeEditor(OutputStream out, Map<String, Object> params) throws IOException {

        StringBuilder urlBuilder = new StringBuilder();
        urlBuilder.append("../pentaho-cdf-dd/edit");
        if (params.size() > 0) {
            urlBuilder.append("?");
        }

        List<String> paramArray = new ArrayList<String>();
        for (String key : params.keySet()) {
            Object value = params.get(key);
            if (value instanceof String) {
                paramArray.add(key + "=" + URLEncoder.encode((String) value, getEncoding()));
            }
        }

        urlBuilder.append(StringUtils.join(paramArray, "&"));
        redirect(urlBuilder.toString());
    }

    @Override
    public RestRequestHandler getRequestHandler() {
        return Router.getBaseRouter();
    }
}

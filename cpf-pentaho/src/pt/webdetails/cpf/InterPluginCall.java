/*!
* Copyright 2002 - 2013 Webdetails, a Pentaho company.  All rights reserved.
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

import java.io.ByteArrayOutputStream;
import java.io.OutputStream;
import java.io.UnsupportedEncodingException;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.concurrent.Callable;

import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.pentaho.platform.api.engine.IContentGenerator;
import org.pentaho.platform.api.engine.IOutputHandler;
import org.pentaho.platform.api.engine.IParameterProvider;
import org.pentaho.platform.api.engine.IPentahoSession;
import org.pentaho.platform.api.engine.IPluginManager;
import org.pentaho.platform.api.engine.ObjectFactoryException;
import org.pentaho.platform.engine.core.output.SimpleOutputHandler;
import org.pentaho.platform.engine.core.solution.SimpleParameterProvider;
import org.pentaho.platform.engine.core.system.PentahoSessionHolder;
import org.pentaho.platform.engine.core.system.PentahoSystem;
import org.pentaho.platform.web.http.request.HttpRequestParameterProvider;

import pt.webdetails.cpf.utils.CharsetHelper;
import pt.webdetails.cpf.web.CpfHttpServletResponse;

/**
 * Call to another pentaho plugin through its content generator. Not thread
 * safe.
 */
public class InterPluginCall implements Runnable, Callable<String> {

    public final static Plugin CDA = new Plugin("cda");
    public final static Plugin CDB = new Plugin("cdb");
    public final static Plugin CDC = new Plugin("cdc");
    public final static Plugin CDE = new Plugin("pentaho-cdf-dd");
    public final static Plugin CDF = new Plugin("pentaho-cdf");
    public final static Plugin CDV = new Plugin("cdv");
    private final static String DEFAULT_ENCODING = CharsetHelper.getEncoding();

    public static class Plugin {

        private String name;
        private String title;

        public String getName() {
            return name;
        }

        public String getTitle() {
            return title;
        }

        public Plugin(String name, String title) {
            this.name = name;
            this.title = title;
        }

        public Plugin(String id) {
            this.name = id;
            this.title = id;
        }
    }
    private static final Log logger = LogFactory.getLog(InterPluginCall.class);
    private Plugin plugin;
    private String method;
    private Map<String, Object> requestParameters;
    private ServletResponse response;
    private HttpServletRequest request;
    private OutputStream output;
    private IPentahoSession session;
    private IPluginManager pluginManager;

    private InterPluginCall() {
    }

    /**
     * Creates a new call.
     *
     * @param plugin the plugin to call
     * @param method
     */
    public InterPluginCall(Plugin plugin, String method) {
        this();

        if (plugin == null) {
            throw new IllegalArgumentException("Plugin must be specified");
        }

        this.plugin = plugin;
        this.method = method;
        this.requestParameters = new HashMap<String, Object>();
    }

    public InterPluginCall(Plugin plugin, String method, Map<String, Object> params) {
        this(plugin, method);

        this.plugin = plugin;
        this.method = method;

        this.requestParameters.putAll(
                params != null
                ? params
                : new HashMap<String, Object>());
    }

    public String getMethod() {
        return method;
    }

    public void setMethod(String method) {
        this.method = method;
    }

    public HttpServletRequest getRequest() {
        return request;
    }

    public void setRequest(HttpServletRequest request) {
        this.request = request;
    }

    public boolean pluginExists() {
        try {
            return getPluginManager().getContentGenerator(plugin.getName(), getSession()) != null;
        } catch (ObjectFactoryException e) {
            return false;
        }
    }

    /**
     * Put a request parameter
     *
     * @param name
     * @param value
     * @return this
     */
    public InterPluginCall putParameter(String name, Object value) {
        requestParameters.put(name, value);
        return this;
    }

    public void run() {
        IOutputHandler outputHandler = new SimpleOutputHandler(getOutputStream(), false);
        IContentGenerator contentGenerator = getContentGenerator();

        if (contentGenerator != null) {
            try {
                contentGenerator.setSession(getSession());
                contentGenerator.setOutputHandler(outputHandler);
                contentGenerator.setParameterProviders(getParameterProviders());
                contentGenerator.createContent();

            } catch (Exception e) {
                logger.error("Failed to execute call to plugin: " + e.toString(), e);
            }
        } else {
            logger.error("No ContentGenerator.");
        }

    }

    public String call() {
        setOutputStream(new ByteArrayOutputStream());
        run();
        try {
            return ((ByteArrayOutputStream) getOutputStream()).toString(getEncoding());
        } catch (UnsupportedEncodingException uee) {
            logger.error("Charset " + getEncoding() + " not supported!!");
            return ((ByteArrayOutputStream) getOutputStream()).toString();
        }
    }

    public void runInPluginClassLoader() {
        getClassLoaderCaller().runInClassLoader(this);
    }

    public String callInPluginClassLoader() {
        try {
            return getClassLoaderCaller().callInClassLoader(this);
        } catch (Exception e) {
            logger.error(e);
            return null;
        }
    }

    public OutputStream getOutputStream() {
        if (output == null) {
            output = new ByteArrayOutputStream();
        }
        return output;
    }

    public ServletResponse getResponse() {
        if (response == null) {
            logger.debug("No response passed to method " + this.method + ", adding mock response.");
            createDefaultResponse();
        }

        return response;
    }

    private void createDefaultResponse() {
      if ( output instanceof ByteArrayOutputStream ) {
        response = new CpfHttpServletResponse( ( ByteArrayOutputStream ) output );
      }
      else {
        response = new CpfHttpServletResponse();
        logger.debug( "OutputStream from response will not be available." );
      }
    }

    public void setResponse(ServletResponse response) {
        this.response = response;
    }

    public void setSession(IPentahoSession session) {
        this.session = session;
    }

    public void setOutputStream(OutputStream outputStream) {
        this.output = outputStream;
    }

    public void setRequestParameters(Map<String, Object> parameters) {
        this.requestParameters = parameters;
    }

    public void setRequestParameters(IParameterProvider requestParameterProvider) {
        if (!requestParameters.isEmpty()) {
            requestParameters.clear();
        }

        for (@SuppressWarnings("unchecked") Iterator<String> params = requestParameterProvider.getParameterNames(); params.hasNext();) {
            String parameterName = params.next();
            requestParameters.put(parameterName, requestParameterProvider.getParameter(parameterName));
        }
    }

    protected IPentahoSession getSession() {
        if (session == null) {
            session = PentahoSessionHolder.getSession();
        }
        return session;
    }

    protected IParameterProvider getRequestParameterProvider() {
        SimpleParameterProvider provider = null;
        if (request != null) {
            provider = new HttpRequestParameterProvider(request);
            provider.setParameters(requestParameters);
        } else {
            provider = new SimpleParameterProvider(requestParameters);
        }
        return provider;
    }

    protected ClassLoaderAwareCaller getClassLoaderCaller() {
        return new ClassLoaderAwareCaller(getPluginManager().getClassLoader(plugin.getTitle()));
    }

    protected IPluginManager getPluginManager() {
        if (pluginManager == null) {
            pluginManager = PentahoSystem.get(IPluginManager.class, getSession());
        }
        return pluginManager;
    }

    protected IContentGenerator getContentGenerator() {
        try {
            IContentGenerator contentGenerator = getPluginManager().getContentGenerator(plugin.getName(), getSession());
            if (contentGenerator == null) {
                logger.error("ContentGenerator for " + plugin.getName() + " could not be fetched.");
            }
            return contentGenerator;
        } catch (Exception e) {
            logger.error("Failed to acquire " + plugin.getName() + " plugin: " + e.toString(), e);
            return null;
        }
    }

    protected IParameterProvider getPathParameterProvider() {
        Map<String, Object> pathMap = new HashMap<String, Object>();
        pathMap.put("path", "/" + method);
        pathMap.put("httpresponse", getResponse());
        if (getRequest() != null) {
            pathMap.put("httprequest", getRequest());
        }
        IParameterProvider pathParams = new SimpleParameterProvider(pathMap);
        return pathParams;
    }

    protected Map<String, IParameterProvider> getParameterProviders() {
        IParameterProvider requestParams = getRequestParameterProvider();
        IParameterProvider pathParams = getPathParameterProvider();
        Map<String, IParameterProvider> paramProvider = new HashMap<String, IParameterProvider>();
        paramProvider.put(IParameterProvider.SCOPE_REQUEST, requestParams);
        paramProvider.put("path", pathParams);
        return paramProvider;
    }

    protected String getEncoding() {
        return DEFAULT_ENCODING;
    }
}

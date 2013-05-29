/* This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this file,
 * You can obtain one at http://mozilla.org/MPL/2.0/. */
package pt.webdetails.cpk;
import java.io.IOException;
import java.io.OutputStream;
import java.util.Collection;
import java.util.Map;
import java.util.TreeMap;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import org.dom4j.DocumentException;
import pt.webdetails.cpf.RestRequestHandler;
import pt.webdetails.cpf.Router;
import pt.webdetails.cpf.http.ICommonParameterProvider;
import pt.webdetails.cpf.utils.IPluginUtils;
import pt.webdetails.cpk.elements.IElement;
import pt.webdetails.cpk.security.IAccessControl;

/**
 *
 * @author joao
 */
public class CpkCoreService {

    private static final Log logger = LogFactory.getLog(CpkCoreService.class);
    private static final String ENCODING = "UTF-8";

    protected CpkEngine cpkEngine;    
    protected ICpkEnvironment cpkEnvironment;

    public CpkCoreService(ICpkEnvironment environment){
        this.cpkEnvironment = environment;
    }

    private CpkEngine getCpkEngine() {
        if (cpkEngine == null) {
            cpkEngine = CpkEngine.getInstanceWithEnv(cpkEnvironment);
        }

        return cpkEngine;
    }

    public void createContent(Map<String,ICommonParameterProvider> parameterProviders) throws Exception {

        //Make sure the instance is first set so we have pluginUtils
        CpkEngine engine = getCpkEngine();
        IAccessControl accessControl = cpkEnvironment.getAccessControl();

        logger.debug("Creating content");

        // Get the path, remove leading slash
        IPluginUtils pluginUtils = cpkEnvironment.getPluginUtils();
        String path = pluginUtils.getPathParameters(parameterProviders).getStringParameter("path", null);
        IElement element = null;

        if (path == null || path.equals("/")) {
            String url = engine.getDefaultElement().getId().toLowerCase();
            if (path == null) {
                // We need to put the http redirection on the right level
                url = pluginUtils.getPluginName() + "/" + url;
            }
            pluginUtils.redirect(parameterProviders, url);
        }

        element = engine.getElement(path.substring(1).toLowerCase());
        if (element != null) {
            if (accessControl.isAllowed(element)) {
                element.processRequest(parameterProviders);
            } else {
                accessControl.throwAccessDenied(parameterProviders);
            }

        } else {
            logger.error( "Unable to get element!");
            throw new NoElementException("Unable to get element!");
        }
    }

    // alias to refresh
    public void reload(OutputStream out,Map<String,ICommonParameterProvider> parameterProviders) throws DocumentException, IOException {
        refresh(out, parameterProviders);
    }

    public void refresh(OutputStream out, Map<String,ICommonParameterProvider> parameterProviders) throws DocumentException, IOException {
        IAccessControl accessControl = cpkEnvironment.getAccessControl();
        if (accessControl.isAdmin()) {
            logger.info("Refreshing CPK plugin " + getPluginName());
            getCpkEngine().reload();
            status(out, parameterProviders);
        } else {
            accessControl.throwAccessDenied(parameterProviders);
        }
    }

    public void status(OutputStream out, Map<String,ICommonParameterProvider> parameterProviders) throws DocumentException, IOException {
        logger.info("Showing status for CPK plugin " + getPluginName());

        // Only set the headers if we have access to the response (via parameterProviders).
        if (parameterProviders != null) {
            cpkEnvironment.getPluginUtils().setResponseHeaders(parameterProviders, "text/plain");
        }
        out.write(getCpkEngine().getStatus().getBytes("UTF-8"));
    }

    public boolean hasElement(String elementId) {
        TreeMap<String, IElement> elementsMap = getCpkEngine().getElementsMap();
        return elementsMap.containsKey(elementId.toLowerCase());
    }

    public IElement[] getElements() {
        IElement[] elements = new IElement[]{};
        CpkEngine engine = getCpkEngine();

        if (engine != null) {
            TreeMap<String, IElement> elementsMap = engine.getElementsMap();
            if (elementsMap != null) {
                Collection<IElement> values = elementsMap.values();
                if (values != null) {
                    return values.toArray(elements);
                }
            }
        } else {
            logger.error("cpkEngine is null...");
        }

        return elements;
    }

    public void getElementsList(OutputStream out) {
        writeMessage(out, getCpkEngine().getElementsJson());
    }

    public String getPluginName() {
      return cpkEnvironment.getPluginName();
    }

    private void writeMessage(OutputStream out, String message) {
        try {
            out.write(message.getBytes(ENCODING));
        } catch (IOException ex) {
            logger.error( "Error writing message", ex);
        }
    }

    public RestRequestHandler getRequestHandler() {
        return Router.getBaseRouter();
    }
}

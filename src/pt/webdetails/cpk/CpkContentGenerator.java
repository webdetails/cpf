/* This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this file,
 * You can obtain one at http://mozilla.org/MPL/2.0/. */
package pt.webdetails.cpk;

import java.io.IOException;
import java.io.OutputStream;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.ResourceBundle.Control;
import java.util.Set;
import java.util.SortedMap;
import java.util.SortedSet;
import java.util.TreeMap;
import java.util.TreeSet;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletResponse;
import javax.xml.ws.http.HTTPException;
import net.sf.saxon.sort.SortedIterator;
import org.codehaus.jackson.map.ObjectMapper;
import org.dom4j.DocumentException;
import org.pentaho.platform.api.engine.IParameterProvider;
import org.pentaho.platform.api.engine.IPentahoSession;
import org.pentaho.platform.engine.core.system.PentahoSessionHolder;
import org.pentaho.platform.engine.security.SecurityHelper;
import org.pentaho.platform.web.http.PentahoHttpSessionHelper;
import org.pentaho.reporting.engine.classic.core.AttributeNames.Pentaho;
import pt.webdetails.cpf.RestContentGenerator;
import pt.webdetails.cpf.RestRequestHandler;
import pt.webdetails.cpf.Router;
import pt.webdetails.cpf.annotations.AccessLevel;
import pt.webdetails.cpf.annotations.Exposed;
import pt.webdetails.cpf.utils.AccessControl;
import pt.webdetails.cpf.utils.PluginUtils;
import pt.webdetails.cpk.elements.IElement;

public class CpkContentGenerator extends RestContentGenerator {

    private static final long serialVersionUID = 1L;
    public static final String CDW_EXTENSION = ".cdw";
    public static final String PLUGIN_NAME = "cpk";
    private CpkEngine cpkEngine;
    private AccessControl accessControl = new AccessControl();

    @Override
    public void createContent() throws Exception {

        // Make sure we have the engine running
        cpkEngine = CpkEngine.getInstance();
        PluginUtils pluginUtils = PluginUtils.getInstance();

        debug("Creating content");

        // Get the path, remove leading slash
        String path = pluginUtils.getPathParameters(parameterProviders).getStringParameter("path", null);
        IElement element = null;

        if (path == null || path.equals("/")) {

            String url = cpkEngine.getDefaultElement().getId();
            if (path == null) {
                // We need to put the http redirection on the right level
                url = pluginUtils.getPluginName() + "/" + url;
            }
            pluginUtils.redirect(parameterProviders, url);
        }

        element = cpkEngine.getElement(path.substring(1).toLowerCase());
        if (element != null) {
            if (accessControl.isAllowed(element.getLocation())) {
                element.processRequest(parameterProviders);
            } else {
                accessControl.throwAccessDenied(element);
            }

        } else {
            super.createContent();
        }


    }

    @Exposed(accessLevel = AccessLevel.PUBLIC)
    public void reload(OutputStream out) throws DocumentException, IOException {

        // alias to refresh
        refresh(out);
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

        PluginUtils.getInstance().setResponseHeaders(parameterProviders, "text/plain");
        out.write(cpkEngine.getStatus().getBytes("UTF-8"));

    }

    @Exposed(accessLevel = AccessLevel.PUBLIC)
    public void getSitemapJson(OutputStream out) throws IOException {
        ObjectMapper mapper = new ObjectMapper();
        mapper.writeValue(out, cpkEngine.getSitemapJson());
    }

    @Exposed(accessLevel = AccessLevel.PUBLIC)
    public void getStyle(OutputStream out) throws IOException {
        out.write("Here is your style!".getBytes("UTF-8"));
    }

    @Override
    public String getPluginName() {

        return PluginUtils.getInstance().getPluginName();
    }

    @Override
    public RestRequestHandler getRequestHandler() {
        return Router.getBaseRouter();
    }
}

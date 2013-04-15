/* This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this file,
 * You can obtain one at http://mozilla.org/MPL/2.0/. */
package pt.webdetails.cpf;

import java.io.IOException;
import java.io.OutputStream;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.EnumMap;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.TimeoutException;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import mondrian.olap.QueryTimeoutException;
import org.apache.axis2.transport.nhttp.PlainClientIOEventDispatch;

import org.apache.commons.io.IOUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import org.json.JSONException;
import org.pentaho.platform.api.engine.IMimeTypeListener;
import org.pentaho.platform.api.engine.IOutputHandler;
import org.pentaho.platform.api.engine.IParameterProvider;
import org.pentaho.platform.api.repository.IContentItem;
import org.pentaho.platform.engine.core.solution.SimpleParameterProvider;

import org.pentaho.platform.engine.core.system.PentahoSessionHolder;
import org.pentaho.platform.engine.security.SecurityHelper;
import org.pentaho.platform.engine.services.solution.BaseContentGenerator;
import org.springframework.security.GrantedAuthorityImpl;

import pt.webdetails.cpf.annotations.AccessLevel;
import pt.webdetails.cpf.annotations.Audited;
import pt.webdetails.cpf.annotations.Exposed;
import pt.webdetails.cpf.audit.CpfAuditHelper;
import pt.webdetails.cpf.repository.RepositoryAccess;
import pt.webdetails.cpf.utils.MimeTypes;
import pt.webdetails.cpf.utils.PluginUtils;

/**
 *
 * @author pdpi
 */
public abstract class SimpleContentGenerator extends BaseContentGenerator {

    private static final long serialVersionUID = 1L;
    protected Log logger = LogFactory.getLog(this.getClass());
    protected static final String ENCODING = PluginSettings.ENCODING;

    protected static String getEncoding() {
        return ENCODING;
    }

    @Override
    public void createContent() throws Exception {

        PluginUtils pluginUtils = PluginUtils.getInstance();
        IParameterProvider pathParams = pluginUtils.getPathParameters(parameterProviders);// parameterProviders.get("path");

        
        try {

            String path = pathParams.getStringParameter("path", null);
            String[] pathSections = StringUtils.split(path, "/");

            if (pathSections == null || pathSections.length == 0) {
                String method = getDefaultPath(path);
                if (!StringUtils.isEmpty(method)) {
                    logger.warn("No method supplied, redirecting.");
                    redirect(method);
                } else {
                    logger.error("No method supplied.");
                }
            } else {

                final String methodName = pathSections[0];

                try {

                    final Method method = getMethod(methodName);
                    invokeMethod(methodName, method);

                } catch (NoSuchMethodException e) {
                    String msg = "couldn't locate method: " + methodName;
                    logger.warn(msg);
                    pluginUtils.getResponse(parameterProviders).sendError(HttpServletResponse.SC_NOT_FOUND, msg);
                } catch (InvocationTargetException e) {
                    // get to the cause and log properly
                    Throwable cause = e.getCause();
                    if (cause == null) {
                        cause = e;
                    }
                    handleError(methodName, cause);
                } catch (Exception e) {
                    handleError(methodName, e);
                }

            }
        } catch (SecurityException e) {
            logger.warn(e.toString());
        }
    }

    private void handleError(final String methodName, Throwable e) throws IOException {

        logger.error(methodName + ": " + e.getMessage(), e);

        String msg = e.getLocalizedMessage();
        if (e instanceof QueryTimeoutException
                || e instanceof TimeoutException) //          ||
        //          (e instanceof RuntimeException &&
        //              StringUtils.containsIgnoreCase(e.getClass().getName(), "timeout"))) 
        {
            PluginUtils.getInstance().getResponse(parameterProviders).sendError(HttpServletResponse.SC_REQUEST_TIMEOUT, msg);
        } else {// default to 500
            PluginUtils.getInstance().getResponse(parameterProviders).sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, msg);
        }
    }

    /**
     * @param methodName
     * @return
     * @throws NoSuchMethodException
     */
    protected Method getMethod(final String methodName) throws NoSuchMethodException {
        final Class<?>[] params = getCGMethodParams();
        final Method method = this.getClass().getMethod(methodName, params);
        return method;
    }

    /**
     * @return this plugin's name
     */
    public abstract String getPluginName();

    /**
     * @return this plugin's path
     */
    public String getPluginPath() {
        return RepositoryAccess.getSystemDir() + "/" + getPluginName();
    }

    /**
     * Get a map of all public methods with the Exposed annotation. Map is not
     * thread-safe and should be used read-only.
     *
     * @param classe Class where to find methods
     * @param log classe's logger
     * @param lowerCase if keys should be in lower case.
     * @return map of all public methods with the Exposed annotation
     */
    protected static Map<String, Method> getExposedMethods(Class<?> classe, boolean lowerCase) {
        HashMap<String, Method> exposedMethods = new HashMap<String, Method>();
        Log log = LogFactory.getLog(classe);
        for (Method method : classe.getMethods()) {
            if (method.getAnnotation(Exposed.class) != null) {
                String methodKey = method.getName().toLowerCase();
                if (exposedMethods.containsKey(methodKey)) {
                    log.error("Method " + method + " differs from " + exposedMethods.get(methodKey) + " only in case and will override calls to it!!");
                }
                log.debug("registering " + classe.getSimpleName() + "." + method.getName());
                exposedMethods.put(methodKey, method);
            }
        }
        return exposedMethods;
    }

    /**
     * In case we need to use reflection with methods that don't just take the
     * OutputStream parameter.
     *
     * @return classes of exposed methods parameters
     */
    protected Class<?>[] getCGMethodParams() {
        return new Class<?>[]{OutputStream.class};
    }

    @SuppressWarnings("deprecation")
    protected OutputStream getResponseOutputStream(final String mimeType) throws IOException {
        IContentItem contentItem = outputHandler.getOutputContentItem(IOutputHandler.RESPONSE, IOutputHandler.CONTENT, "", instanceId, mimeType);
        return contentItem.getOutputStream(null);
    }

    protected String getDefaultPath(String path) {
        return null;
    }

    private boolean canAccessMethod(Method method, Exposed exposed) {
        if (exposed != null) {

            AccessLevel accessLevel = exposed.accessLevel();
            if (accessLevel != null) {

                boolean accessible = false;
                switch (accessLevel) {
                    case ADMIN:
                        accessible = SecurityHelper.isPentahoAdministrator(PentahoSessionHolder.getSession());
                        break;
                    case ROLE:
                        String role = exposed.role();
                        if (!StringUtils.isEmpty(role)) {
                            accessible = SecurityHelper.isGranted(PentahoSessionHolder.getSession(), new GrantedAuthorityImpl(role));
                        }
                        break;
                    case PUBLIC:
                        accessible = true;
                        break;
                    default:
                        logger.error("Unsupported AccessLevel " + accessLevel);
                }

                return accessible;
            }

        }
        return false;
    }

    protected boolean invokeMethod(final String methodName, final Method method)
            throws InvocationTargetException, IllegalArgumentException, IllegalAccessException, IOException {

        Exposed exposed = method.getAnnotation(Exposed.class);

        if (canAccessMethod(method, exposed)) {

            Audited audited = method.getAnnotation(Audited.class);
            UUID uuid = null;
            long start = System.currentTimeMillis();
            if (audited != null) {
                uuid = CpfAuditHelper.startAudit(getPluginName(), audited.action(), getObjectName(), userSession, this, PluginUtils.getInstance().getRequestParameters(parameterProviders));
            }
            final OutputStream out = getResponseOutputStream(exposed.outputType());
            PluginUtils.getInstance().setResponseHeaders(parameterProviders, exposed.outputType());
            try {
                method.invoke(this, out);
            } finally {
                if (audited != null) {
                    CpfAuditHelper.endAudit(getPluginName(), audited.action(), getObjectName(), userSession, this, start, uuid, System.currentTimeMillis());
                }
            }

            return true;
        }
        
        
        String msg = "Method " + methodName + " not exposed or user does not have required permissions.";
        logger.error(msg);
        PluginUtils.getInstance().getResponse(parameterProviders).sendError(HttpServletResponse.SC_FORBIDDEN, msg);
        return false;
    }

    protected void redirect(String method) {

        final HttpServletResponse response = (HttpServletResponse) parameterProviders.get("path").getParameter("httpresponse");

        if (response == null) {
            logger.error("response not found");
            return;
        }
        try {
            response.sendRedirect(method);
        } catch (IOException e) {
            logger.error("could not redirect", e);
        }
    }

    /**
     * Write to OutputStream using defined encoding.
     *
     * @param out
     * @param contents
     * @throws IOException
     */
    protected void writeOut(OutputStream out, String contents) throws IOException {
        IOUtils.write(contents, out, getEncoding());
    }

    protected void writeOut(OutputStream out, JsonSerializable contents) throws IOException, JSONException {
        IOUtils.write(contents.toJSON().toString(), out, getEncoding());
    }

    @Override
    public Log getLogger() {
        return logger;
    }
    
    protected HttpServletResponse getResponse(){
        return PluginUtils.getInstance().getResponse(parameterProviders);
    }
    
    protected HttpServletRequest getRequest(){
        return PluginUtils.getInstance().getRequest(parameterProviders);
    }
    
    protected IParameterProvider getRequestParameters(){
        return PluginUtils.getInstance().getRequestParameters(parameterProviders);
    }
    
    protected void setResponseHeaders(String mimeType, int cacheDuration, String attachmentName ){
        PluginUtils.getInstance().setResponseHeaders(parameterProviders, mimeType, cacheDuration, attachmentName);
    }
    
    protected IParameterProvider getPathParameters(){
        return PluginUtils.getInstance().getPathParameters(parameterProviders);
    }
    
    

}

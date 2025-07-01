/*! ******************************************************************************
 *
 * Pentaho
 *
 * Copyright (C) 2024 by Hitachi Vantara, LLC : http://www.pentaho.com
 *
 * Use of this software is governed by the Business Source License included
 * in the LICENSE.TXT file.
 *
 * Change Date: 2029-07-20
 ******************************************************************************/


package pt.webdetails.cpf.utils;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.OutputStream;
import java.io.UnsupportedEncodingException;
import java.net.URL;
import java.net.URLDecoder;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.apache.commons.io.FileUtils;
import org.apache.commons.io.FilenameUtils;
import org.apache.commons.io.filefilter.IOFileFilter;
import org.apache.commons.io.filefilter.RegexFileFilter;
import org.apache.commons.io.filefilter.TrueFileFilter;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.dom4j.DocumentException;
import org.pentaho.platform.api.engine.IPluginResourceLoader;
import org.pentaho.platform.engine.core.system.PentahoSystem;
import pt.webdetails.cpf.Util;
import pt.webdetails.cpf.http.ICommonParameterProvider;

/**
 * TODO: we generally use Utils for static helper classes, this needs a new name
 * @author Pedro Alves<pedro.alves@webdetails.pt>
 */
public class PluginUtils implements IPluginUtils {

    protected Log logger = LogFactory.getLog(this.getClass());
    private String pluginName;
    private File pluginDirectory;

    @Override
    public File getPluginDirectory() {
        return pluginDirectory;
    }

    @Override
    public void setPluginDirectory(File pluginDirectory) {
        this.pluginDirectory = pluginDirectory;
    }

    @Override
    public String getPluginName() {
        return pluginName;
    }

    @Override
    public void setPluginName(String pluginName) {
        this.pluginName = pluginName;
    }

    public PluginUtils() {
        try {
            initialize();
        } catch (Exception e) {
            logger.error("Can't initialize PluginUtils: " + Util.getExceptionDescription(e));
        }

    }

    @Override
    public void initialize() throws IOException, DocumentException {
        // We need to get the plugin name
        IPluginResourceLoader resLoader = PentahoSystem.get(IPluginResourceLoader.class, null);
        List<URL> pluginResource = resLoader.findResources(this.getClass(), "plugin.xml");
        if (pluginResource.isEmpty()) {
            throw new IOException("plugin.xml required but not found");
        }
        /*
         * Verify if the index 0 is actually the file we want!
         */
        URL pluginUrl = pluginResource.get(0);

        // Parent file holds the name
        File pluginDir = new File(pluginUrl.getFile()).getParentFile();
        setPluginName(pluginDir.getName());
        setPluginDirectory(pluginDir);

        logger.debug("Found resource? " + pluginResource.size());
    }

    /**
     * Calls out for resources in the plugin, on the specified path
     *
     * @param elementPath Relative to the plugin directory
     * @param recursive Do we want to enable recursivity?
     * @param pattern regular expression to filter the files
     * @return Files found
     */
    @Override
    public Collection<File> getPluginResources(String elementPath, Boolean recursive, String pattern) {

        IOFileFilter fileFilter = TrueFileFilter.TRUE;

        if (pattern != null && !pattern.equals("")) {
            fileFilter = new RegexFileFilter(pattern);
        }

        IOFileFilter dirFilter = recursive.equals(Boolean.TRUE) ? TrueFileFilter.TRUE : null;
        // TODO: why doesn't this use repository access?
        // Get directory name. We need to make sure we're not allowing this to fetch other resources
      String basePath = null;
      String elementFullPath = null;

      try{
        basePath = URLDecoder.decode(
          FilenameUtils.normalize(getPluginDirectory().getAbsolutePath()), CharsetHelper.getEncoding());
        elementFullPath = URLDecoder.decode(
          FilenameUtils.normalize(basePath + File.separator + elementPath), CharsetHelper.getEncoding());
      } catch ( UnsupportedEncodingException e) {
        //CharseHelper.getEncoding() returns a valid encoding
      }

        if (!elementFullPath.startsWith(basePath)) {
            logger.warn("PluginUtils.getPluginResources is trying to access a parent path - denied : " + elementFullPath);
            return null;
        }

        File dir = new File(elementFullPath);
        if (!dir.exists() || !dir.isDirectory()) {
            return null;
        }

        return FileUtils.listFiles(dir, fileFilter, dirFilter);


    }

    /**
     * From a full path, returns the relative path
     *
     * @param fullPath
     * @param includePluginDir
     * @return The relative path
     */
    @Override
    public String getPluginRelativeDirectory(String fullPath, boolean includePluginDir) throws FileNotFoundException {
        // Get directory name. We need to make sure we're not allowing this to fetch other resources
        File pluginDir = getPluginDirectory();
        if (includePluginDir) {
            pluginDir = pluginDir.getParentFile();
        }
        String basePath = null;
        String elementFullPath = null;
        try{
        basePath = URLDecoder.decode(
          FilenameUtils.normalize( pluginDir.getAbsolutePath()), CharsetHelper.getEncoding());
        elementFullPath = URLDecoder.decode(
          FilenameUtils.getFullPath( FilenameUtils.normalize( fullPath ) ), CharsetHelper.getEncoding() );
        } catch ( UnsupportedEncodingException e) {
          //CharseHelper.getEncoding() returns a valid encoding
        }

        if (elementFullPath.indexOf(basePath) < 0) {
            throw new FileNotFoundException("Can't extract relative path from file " + fullPath);
        }

        return elementFullPath.substring(basePath.length());


    }

    /**
     * Calls out for resources in the plugin, on the specified path
     *
     * @param elementPath Relative to the plugin directory
     * @param recursive Do we want to enable recursivity?
     * @return Files found
     */
    @Override
    public Collection<File> getPluginResources(String elementPath, Boolean recursive) {
        return getPluginResources(elementPath, recursive, null);
    }

    /**
     * Calls out for resources in the plugin, on the specified path. Not
     * recursive
     *
     * @param elementPath Relative to the plugin directory
     * @param pattern regular expression to filter the files
     * @return Files found
     */
    @Override
    public Collection<File> getPluginResources(String elementPath, String pattern) {
        return getPluginResources(elementPath, false, pattern);
    }

    @Override
    public void setResponseHeaders(Map<String, ICommonParameterProvider> parameterProviders, final String mimeType) {
        setResponseHeaders(parameterProviders, mimeType, 0, null, 0);
    }

    public void setResponseHeaders(Map<String, ICommonParameterProvider> parameterProviders, final String mimeType, final String attachmentName) {
        setResponseHeaders(parameterProviders, mimeType, 0, attachmentName, 0);
    }

    @Override
    public void setResponseHeaders(Map<String, ICommonParameterProvider> parameterProviders, final String mimeType, final String attachmentName, long attachmentSize) {
        setResponseHeaders(parameterProviders, mimeType, 0, attachmentName, attachmentSize);

    }

    public void setResponseHeaders(Map<String, ICommonParameterProvider> parameterProviders, final String mimeType, final int cacheDuration, final String attachmentName, long attachmentSize) {
        // Make sure we have the correct mime type

        /* 
         * This code is part of the content generator. Since we want to simplify,
         * I'll remove this from here and directly set the Content-Type header on the response
         * 
         final IMimeTypeListener mimeTypeListener = outputHandler.getMimeTypeListener();
         if (mimeTypeListener != null) {
         mimeTypeListener.setMimeType(mimeType);
         }
         */


        final HttpServletResponse response = getResponse(parameterProviders);

        if (response == null) {
            logger.warn("Parameter 'httpresponse' not found!");
            return;
        }

        if (mimeType != null) {
            response.setHeader("Content-Type", mimeType);
        }

        if (attachmentName != null) {
            response.setHeader("content-disposition", "attachment; filename=" + attachmentName);
        } // Cache?

        if (attachmentSize > 0) {
            response.setHeader("Content-Length", String.valueOf(attachmentSize));
        }

        if (cacheDuration > 0) {
            response.setHeader("Cache-Control", "max-age=" + cacheDuration);
        } else {
            response.setHeader("Cache-Control", "max-age=0, no-store");
        }
    }

    /**
     * Copies the parameters from the ICommonParameterProvider to a Map
     *
     * @param params
     * @param provider
     */
    public static void copyParametersFromProvider(Map<String, Object> params, ICommonParameterProvider provider) {
        Iterator<String> paramNames = provider.getParameterNames();
        while (paramNames.hasNext()) {
            String paramName = paramNames.next();
            params.put(paramName, provider.getParameter(paramName));
        }
    }

    @Override
    public void redirect(Map<String, ICommonParameterProvider> parameterProviders, String url) {

        final HttpServletResponse response = getResponse(parameterProviders);

        if (response == null) {
            logger.error("response not found");
            return;
        }
        try {
            response.sendRedirect(url);
        } catch (IOException e) {
            logger.error("could not redirect", e);
        }
    }

    public HttpServletRequest getRequest(Map<String, ICommonParameterProvider> parameterProviders) {
        return (HttpServletRequest) parameterProviders.get("path").getParameter("httprequest");
    }

    public HttpServletResponse getResponse(Map<String, ICommonParameterProvider> parameterProviders) {
        return (HttpServletResponse) parameterProviders.get("path").getParameter("httpresponse");
    }

    @Override
    public ICommonParameterProvider getRequestParameters(Map<String, ICommonParameterProvider> parameterProviders) {
        return parameterProviders.get("request");
    }

    @Override
    public ICommonParameterProvider getPathParameters(Map<String, ICommonParameterProvider> parameterProviders) {
        return parameterProviders.get("path");
    }

    public OutputStream getResponseOutputStream(Map<String, ICommonParameterProvider> parameterProviders) throws IOException {

        return getResponse(parameterProviders).getOutputStream();
    }

    @Override
    public OutputStream getOutputStream(Map<String, ICommonParameterProvider> map) throws IOException {
            return getResponseOutputStream(map);
    }
}

/* This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this file,
 * You can obtain one at http://mozilla.org/MPL/2.0/. */
package pt.webdetails.cpf.utils;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.util.Collection;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
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

/**
 *
 * @author Pedro Alves<pedro.alves@webdetails.pt>
 */
public class PluginUtils {

    private static PluginUtils _instance;
    protected Log logger = LogFactory.getLog(this.getClass());
    private String pluginName;
    private File pluginDirectory;

    public File getPluginDirectory() {
        return pluginDirectory;
    }

    public void setPluginDirectory(File pluginDirectory) {
        this.pluginDirectory = pluginDirectory;
    }

    public String getPluginName() {
        return pluginName;
    }

    public void setPluginName(String pluginName) {
        this.pluginName = pluginName;
    }

    public PluginUtils() {
        try {
            // init
            initialize();
        } catch (Exception e) {
            logger.error("Can't initialize PluginUtils: " + Util.getExceptionDescription(e));
        }

    }

    public static PluginUtils getInstance(){

        if (_instance == null) {
            _instance = new PluginUtils();
        }

        return _instance;
    }

    private void initialize() throws IOException, DocumentException {

        // We need to get the plugin name
        IPluginResourceLoader resLoader = PentahoSystem.get(IPluginResourceLoader.class, null);
        List<URL> pluginResource = resLoader.findResources(this.getClass(), "plugin.xml");

        if (pluginResource.size() != 1) {
            throw new IOException("plugin.xml required but not found");
        }

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
    public Collection<File> getPluginResources(String elementPath, Boolean recursive, String pattern) {

        IOFileFilter fileFilter = TrueFileFilter.TRUE;

        if (pattern != null && !pattern.equals("")) {
            fileFilter = new RegexFileFilter(pattern);


        }

        IOFileFilter dirFilter = recursive.equals(Boolean.TRUE) ? TrueFileFilter.TRUE : null;
        
        // Get directory name. We need to make sure we're not allowing this to fetch other resources
        String basePath = FilenameUtils.normalize(getPluginDirectory().getAbsolutePath());
        String elementFullPath = FilenameUtils.normalize(basePath + File.separator + elementPath);
        
        if(!elementFullPath.startsWith(basePath)){
            logger.warn("PluginUtils.getPluginResources is trying to access a parent path - denied : " + elementFullPath);
            return null;
        }
        
        return FileUtils.listFiles(new File(elementFullPath), fileFilter, dirFilter);


    }

    /**
     * Calls out for resources in the plugin, on the specified path
     *
     * @param elementPath Relative to the plugin directory
     * @param recursive Do we want to enable recursivity?
     * @return Files found
     */
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
    public Collection<File> getPluginResources(String elementPath, String pattern) {
        return getPluginResources(elementPath, false, pattern);
    }
}

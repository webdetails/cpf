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

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

import org.apache.commons.io.FileUtils;
import org.apache.commons.io.IOUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.pentaho.platform.plugin.services.pluginmgr.PluginClassLoader;

import pt.webdetails.cpf.repository.IRepositoryAccess;
import pt.webdetails.cpf.repository.IRepositoryAccess.FileAccess;
import pt.webdetails.cpf.repository.PentahoRepositoryAccess;

/**
 *
 * @author pdpi
 */
public class CpfProperties extends Properties {

    private static final long serialVersionUID = 1L;
    private static CpfProperties instance;
    private static final Log logger = LogFactory.getLog(CpfProperties.class);

    private CpfProperties() {

        loadGlobalSettings();
        loadPluginSettings();
    }

    private void loadGlobalSettings() {
        /* Load the in-jar settings */
        try {
            loadAndClose(getClass().getResourceAsStream("config.properties"));
        } catch (IOException ioe) {
            logger.warn("Failed to read CPF base settings");
        }

        if (this.getClass().getClassLoader() instanceof PluginClassLoader) {
            IRepositoryAccess repository = PentahoRepositoryAccess.getRepository();
            try {
                if (repository.resourceExists("/cpf/config.properties")) {
                    loadAndClose(repository.getResourceInputStream("/cpf/config.properties", FileAccess.NONE));
                } else {
                    logger.info("No global CPF settings.");
                }

            } catch (Exception e) {
                logger.error("Failed to read global CPF settings:" + e.toString());
            }
        } else {
            logger.warn(this.getClass().getClassLoader().toString() + "is not instanceof PluginClassLoader");
        }

    }

    private void loadPluginSettings() {
        if (this.getClass().getClassLoader() instanceof PluginClassLoader) {
            try {
                File pluginCpfSettings = new File(getPluginPath() + "/cpf.properties");
                if (pluginCpfSettings.exists()) {
                    loadAndClose(FileUtils.openInputStream(pluginCpfSettings));
                } else {
                    logger.info("No plugin-specific CPF settings.");
                }
            } catch (IOException ioe) {
                logger.error("Failed to read plugin-specific CPF base settings");
            }
        } else {
            logger.warn(this.getClass().getClassLoader().toString() + "is not instanceof PluginClassLoader");
        }
    }

    public static CpfProperties getInstance() {
        if (instance == null) {
            instance = new CpfProperties();
        }
        return instance;
    }

    public boolean getBooleanProperty(String property, boolean defaultValue) {
        String propertyValue = getProperty(property, null);
        if (!StringUtils.isEmpty(propertyValue)) {
            return Boolean.parseBoolean(propertyValue);
        }
        return defaultValue;
    }

    public int getIntProperty(String property, int defaultValue) {
        String propertyValue = getProperty(property, null);
        if (!StringUtils.isEmpty(propertyValue)) {
            try {
                return Integer.parseInt(propertyValue);
            } catch (NumberFormatException e) {
                logger.error("getIntProperty: " + property + " is not a valid int value.");
            }
        }
        return defaultValue;
    }

    public long getLongProperty(String property, long defaultValue) {
        String propertyValue = getProperty(property, null);
        if (!StringUtils.isEmpty(propertyValue)) {
            try {
                return Long.parseLong(propertyValue);
            } catch (NumberFormatException e) {
                logger.error("getLongProperty: " + property + " is not a valid long value.");
            }
        }
        return defaultValue;
    }

    private void loadAndClose(InputStream input) throws IOException {
        try {
            load(input);
        } finally {
            IOUtils.closeQuietly(input);
        }
    }

    private String getPluginPath() {
        ClassLoader cl = this.getClass().getClassLoader();
        return ((PluginClassLoader) cl).getPluginDir().getPath();
    }
}

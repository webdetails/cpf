/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package pt.webdetails.cpf;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.pentaho.platform.plugin.services.pluginmgr.PluginClassLoader;
import pt.webdetails.cpf.repository.RepositoryUtils;

/**
 *
 * @author pdpi
 */
public class Settings extends Properties {

    private static Settings instance;
    private static final Log logger = LogFactory.getLog(Settings.class);

    private Settings() {
        try {
            load(getClass().getResourceAsStream("config.properties"));
        } catch (Exception ioe) {
            logger.warn("Failed to read CPF base settings");
        }
        try {
            load(RepositoryUtils.readSolutionFileAsStream("solution/cpf/config.properties"));
        } catch (Exception ioe) {
            logger.info("Failed to read global CPF settings");
        }
        try {
            load(getSettingsStream());
        } catch (Exception ioe) {
            logger.info("Failed to read plugin-specific CPF base settings");
        }
    }

    public static Settings getInstance() {
        if (instance == null) {
            instance = new Settings();
        }
        return instance;
    }

    private InputStream getSettingsStream() {
        ClassLoader cl = this.getClass().getClassLoader();
        try {
            String settingsPath = ((PluginClassLoader) cl).getPluginDir().getPath() + "/cpf.properties";
            return new FileInputStream(settingsPath);

        } catch (Exception e) {
            return null;
        }
    }
}

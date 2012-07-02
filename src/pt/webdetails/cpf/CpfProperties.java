/* This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this file,
 * You can obtain one at http://mozilla.org/MPL/2.0/. */

package pt.webdetails.cpf;

import java.io.FileInputStream;
import java.io.InputStream;
import java.util.Properties;

import org.apache.commons.lang.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.pentaho.platform.plugin.services.pluginmgr.PluginClassLoader;

import pt.webdetails.cpf.repository.RepositoryAccess;
import pt.webdetails.cpf.repository.RepositoryAccess.FileAccess;

/**
 *
 * @author pdpi
 */
public class CpfProperties extends Properties {

  private static final long serialVersionUID = 1L;
  private static CpfProperties instance;
  private static final Log logger = LogFactory.getLog(CpfProperties.class);

  private CpfProperties() {
    try {
      load(getClass().getResourceAsStream("config.properties"));
    } catch (Exception ioe) {
      logger.warn("Failed to read CPF base settings");
    }
    try {
      load(RepositoryAccess.getRepository().getResourceInputStream("solution/cpf/config.properties", FileAccess.NONE));
    } catch (Exception ioe) {
      logger.info("Failed to read global CPF settings");
    }
    try {
      load(getSettingsStream());
    } catch (Exception ioe) {
      logger.info("Failed to read plugin-specific CPF base settings");
    }
  }

  public static CpfProperties getInstance() {
    if (instance == null) {
      instance = new CpfProperties();
    }
    return instance;
  }
  
  public boolean getBooleanProperty(String property, boolean defaultValue){
    String propertyValue = getProperty(property, null);
    if(!StringUtils.isEmpty(propertyValue)){
      return Boolean.parseBoolean(propertyValue);
    }
    return defaultValue;
  }
  
  public int getIntProperty(String property, int defaultValue){
    String propertyValue = getProperty(property, null);
    if(!StringUtils.isEmpty(propertyValue)){
      try{
        return Integer.parseInt(propertyValue);
      }
      catch (NumberFormatException e){
        logger.error("getIntProperty: " + property + " is not a valid int value.");
      }
    }
    return defaultValue;
  }
  
  public long getLongProperty(String property, long defaultValue){
    String propertyValue = getProperty(property, null);
    if(!StringUtils.isEmpty(propertyValue)){
      try{
        return Long.parseLong(propertyValue);
      }
      catch (NumberFormatException e){
        logger.error("getLongProperty: " + property + " is not a valid long value.");
      }
    }
    return defaultValue;
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

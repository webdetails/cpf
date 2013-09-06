/* This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this file,
 * You can obtain one at http://mozilla.org/MPL/2.0/. */
package pt.webdetails.cpf;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.io.IOUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.dom4j.Document;
import org.dom4j.DocumentException;
import org.dom4j.Element;
import org.dom4j.Node;
import org.dom4j.io.SAXReader;

import pt.webdetails.cpf.repository.api.IRWAccess;
import pt.webdetails.cpf.utils.CharsetHelper;


//TODO: decide how plugin configuration will behave and have a proper config hierarchy
public class PluginSettings {

    protected static final String SETTINGS_FILE = "settings.xml";
    protected static Log logger = LogFactory.getLog(PluginSettings.class);
    private IRWAccess writeAccess;
    private Document settings;
    /**
     * time of last file edit to loaded version. TODO
     */
    private long lastRead;
  
    /**
     * 
     * @param writeAccess RW access to a location that contains settings.xml
     */
    public PluginSettings(IRWAccess writeAccess) {
        this.writeAccess = writeAccess;
        loadDocument();
    }

    private boolean loadDocument() {
      InputStream input = null;

      try {
          input = writeAccess.getFileInputStream(SETTINGS_FILE);
          lastRead = writeAccess.getLastModified(SETTINGS_FILE);
          SAXReader reader = new SAXReader();
          settings = reader.read(input);
          return true;
      } catch (IOException ex) {
          logger.error("Error while reading settings.xml", ex);
      } catch (DocumentException ex) {
          logger.error("Error while reading settings.xml", ex);
      }
      finally {
        IOUtils.closeQuietly(input);
      }
      return false;
    }

    protected String getStringSetting(String section, String defaultValue) {
      Node node = settings.selectSingleNode(getNodePath(section));
      if (node == null) {
          return defaultValue;
      } else {
          return node.getStringValue();
      }
    }

    protected boolean getBooleanSetting(String section, boolean nullValue) {
        String setting = getStringSetting(section, null);
        if (setting != null) {
            return Boolean.parseBoolean(setting);
        }
        return nullValue;
    }

    private String getNodePath(String section) {
        return "settings/" + section;
    }

    /**
     * Writes a setting directly to .xml.
     *
     * @param section
     * @param value
     * @return whether value was written
     */
    protected boolean writeSetting(String section, String value) {
        if (settings != null) {
            Node node = settings.selectSingleNode(getNodePath(section));
            if (node != null) {
                String oldValue = node.getText();
                node.setText(value);
                try {
                    String contents = settings.asXML();
                    if (writeAccess.saveFile(SETTINGS_FILE, IOUtils.toInputStream(contents, CharsetHelper.getEncoding()))) {
                        logger.debug("changed '" + section + "' from '" + oldValue + "' to '" + value + "'");
                        return true;
                    }
                    logger.error("Error saving settings file.");
                } catch (Exception e) {
                    logger.error(e);
                }
            } else {
                logger.error("Couldn't find node");
            }
        } else {
            logger.error("No settings!");
        }
        return false;
    }

    @SuppressWarnings("unchecked")
    protected List<Element> getSettingsXmlSection(String section) {
        return settings.selectNodes("/settings/" + section);
    }

    /**
     * where is this used??
     */
    public List<String> getTagValue(String tag) {
        List<Element> pathElements = getSettingsXmlSection(tag);
        if (pathElements != null) {
            ArrayList<String> solutionPaths = new ArrayList<String>(pathElements.size());
            for (Element pathElement : pathElements) {
                solutionPaths.add(pathElement.getText());
            }
            return solutionPaths;
        }
        return new ArrayList<String>(0);
    }

}

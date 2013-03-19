/* This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this file,
 * You can obtain one at http://mozilla.org/MPL/2.0/. */
package pt.webdetails.cpf;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.apache.commons.io.IOUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.dom4j.Document;
import org.dom4j.Element;
import org.dom4j.Node;
import org.springframework.beans.factory.annotation.Autowired;
import pt.webdetails.cpf.repository.IRepositoryAccess;

public abstract class PluginSettings {

    public static final String ENCODING = "utf-8";
    protected static Log logger = LogFactory.getLog(PluginSettings.class);
    
    @Autowired
    private IRepositoryAccess repository;

    public void setRepository(IRepositoryAccess repository) {
        this.repository = repository;
    }

    public abstract String getPluginName();

    public String getPluginSystemDir() {
        return getPluginName() + "/";
    }
    protected static final String SETTINGS_FILE = "settings.xml";

    protected String getStringSetting(String section, String defaultValue) {
        Document doc;
        try {
            doc = repository.getResourceAsDocument("system/" + getPluginSystemDir() + SETTINGS_FILE);
            return doc.selectSingleNode(section).getStringValue();
        } catch (IOException ex) {
            Logger.getLogger(PluginSettings.class.getName()).log(Level.SEVERE, null, ex);
        }
        
        return "";
    }

    protected boolean getBooleanSetting(String section, boolean nullValue) {
        String setting = getStringSetting(section, null);
        if (setting != null) {
            return Boolean.parseBoolean(setting);
        }
        return nullValue;
    }

    /**
     * Writes a setting directly to .xml and refresh global config.
     *
     * @param section
     * @param value
     * @return whether value was written
     */
    protected boolean writeSetting(String section, String value) {
        String settingsFilePath = repository.getSolutionPath("system/" + getPluginSystemDir() + SETTINGS_FILE);
        return writeSetting(section, value, settingsFilePath);
    }

    protected boolean writeSetting(String section, String value, String settingsFilePath) {
        File settingsFile = new File(settingsFilePath);
        String nodePath = "settings/" + section;
        Document settings = null;
        try {
            settings =  repository.getResourceAsDocument(settingsFilePath);
        } catch (IOException e) {
            logger.error(e);
        }
        if (settings != null) {
            Node node = settings.selectSingleNode(nodePath);
            if (node != null) {
                String oldValue = node.getText();
                node.setText(value);
                FileWriter writer = null;
                try {
                    writer = new FileWriter(settingsFile);
                    settings.write(writer);
                    writer.flush();
                    //TODO: in future should only refresh relevant cache, not the whole thing
                    logger.debug("changed '" + section + "' from '" + oldValue + "' to '" + value + "'");
                    return true;
                } catch (IOException e) {
                    logger.error(e);
                } finally {
                    IOUtils.closeQuietly(writer);
                }
            } else {
                logger.error("Couldn't find node");
            }
        } else {
            logger.error("Unable to open " + settingsFilePath);
        }
        return false;
    }

    @SuppressWarnings("unchecked")
    protected List<Element> getSettingsXmlSection(String section) {
        Document doc = null;
        try {
            doc = repository.getResourceAsDocument("system/" + getPluginSystemDir() + SETTINGS_FILE);
        } catch (IOException ex) {
            Logger.getLogger(PluginSettings.class.getName()).log(Level.SEVERE, null, ex);
        }
        if(doc != null){
            List<Element> elements = doc.selectNodes(section);
            return elements;
        } 
        return new ArrayList<Element>();  
    }

    @SuppressWarnings("unchecked")
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

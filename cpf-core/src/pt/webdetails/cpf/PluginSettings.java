/* This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this file,
 * You can obtain one at http://mozilla.org/MPL/2.0/. */

package pt.webdetails.cpf;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.dom4j.Document;
import org.dom4j.DocumentException;
import org.dom4j.DocumentHelper;
import org.dom4j.Element;
import org.dom4j.Node;
import org.dom4j.io.SAXReader;
import org.springframework.beans.factory.annotation.Autowired;
import pt.webdetails.cpf.repository.IRepositoryAccess;
import pt.webdetails.cpf.repository.BaseRepositoryAccess.FileAccess;
import pt.webdetails.cpf.repository.BaseRepositoryAccess.SaveFileStatus;
import pt.webdetails.cpf.repository.IRepositoryFile;

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
    // TODO: do we ever use that?
    @Deprecated
    protected boolean writeSetting(String section, String value) {
        IRepositoryFile settingsFile = repository.getSettingsFile(SETTINGS_FILE, FileAccess.READ);
        return writeSetting(section, value, settingsFile);
    }

    // TODO: do we ever use that?
    @Deprecated
    protected boolean writeSetting(String section, String value, IRepositoryFile settingsFile) {
        String nodePath = "settings/" + section;
        Document settings = null;
        try {
        	settings = DocumentHelper.parseText(new String(settingsFile.getData()));
        } catch (Exception e) {
            logger.error(e);
        }
        if (settings != null) {
            Node node = settings.selectSingleNode(nodePath);
            if (node != null) {
                String oldValue = node.getText();
                node.setText(value);
                try {
                    String contents = settings.asXML();
                    SaveFileStatus ss = repository.publishFile(settingsFile.getSolutionPath(), contents, true);
                    if (ss.equals(SaveFileStatus.OK)) {
                    	//TODO: in future should only refresh relevant cache, not the whole thing
                    	logger.debug("changed '" + section + "' from '" + oldValue + "' to '" + value + "'");
                    	return true;
                    }
                    throw new Exception("Error converting settings document to string and publishing to repository: " + settingsFile.getSolutionPath());
                } catch (Exception e) {
                    logger.error(e);
                }
            } else {
                logger.error("Couldn't find node");
            }
        } else {
            logger.error("Unable to read " + settingsFile);
        }
        return false;
    }

    @SuppressWarnings("unchecked")
    protected List<Element> getSettingsXmlSection(String section) {
        Document doc = null;
        ByteArrayInputStream bis;
        SAXReader reader;
        try {
            String resource = repository.getResourceAsString("system/" + getPluginSystemDir() + SETTINGS_FILE);
            bis = new ByteArrayInputStream(resource.getBytes());
            reader = new SAXReader();
            
            doc = reader.read(bis);
        } catch (IOException ex) {
            Logger.getLogger(PluginSettings.class.getName()).log(Level.SEVERE, null, ex);
        }catch (DocumentException ex){
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

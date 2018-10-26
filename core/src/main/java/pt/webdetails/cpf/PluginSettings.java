/*!
* Copyright 2002 - 2018 Webdetails, a Hitachi Vantara company.  All rights reserved.
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
import pt.webdetails.cpf.utils.XmlParserFactoryProducer;

import java.io.IOException;
import java.io.InputStream;
import java.util.List;

import static java.util.stream.Collectors.toList;


/**
 * Base class for reading settings.xml<br> Intended to be extended or wrapped by plugin.
 */
public class PluginSettings {

  protected static final String SETTINGS_FILE = "settings.xml";
  protected static Log logger = LogFactory.getLog( PluginSettings.class );
  private IRWAccess writeAccess;
  private Document settings;
  /**
   * time of last file edit to loaded version. TODO
   */
  private long lastRead;

  /**
   * @param writeAccess RW access to a location that contains settings.xml
   */
  public PluginSettings( IRWAccess writeAccess ) {
    this.writeAccess = writeAccess;
    loadDocument();
  }

  private boolean loadDocument() {
    InputStream input = null;

    try {
      input = writeAccess.getFileInputStream( SETTINGS_FILE );
      lastRead = writeAccess.getLastModified( SETTINGS_FILE );
      SAXReader reader = XmlParserFactoryProducer.getSAXReader( null );
      settings = reader.read( input );
      return true;
    } catch ( IOException ex ) {
      logger.error( "Error while reading settings.xml", ex );
    } catch ( DocumentException ex ) {
      logger.error( "Error while reading settings.xml", ex );
    } finally {
      IOUtils.closeQuietly( input );
    }
    return false;
  }

  protected String getStringSetting( String section, String defaultValue ) {
    Node node = settings.selectSingleNode( getNodePath( section ) );
    if ( node == null ) {
      return defaultValue;
    } else {
      return node.getStringValue();
    }
  }

  protected boolean getBooleanSetting( String section, boolean nullValue ) {
    String setting = getStringSetting( section, null );
    if ( setting != null ) {
      return Boolean.parseBoolean( setting );
    }
    return nullValue;
  }

  private String getNodePath( String section ) {
    return "settings/" + section;
  }

  /**
   * Writes a setting directly to .xml.
   *
   * @param section
   * @param value
   * @return whether value was written
   */
  protected boolean writeSetting( String section, String value ) {
    if ( settings != null ) {
      Node node = settings.selectSingleNode( getNodePath( section ) );
      if ( node != null ) {
        // update value
        String oldValue = node.getText();
        node.setText( value );
        // save file
        String saveMsg = "changed '" + section + "' from '" + oldValue + "' to '" + value + "'";
        return saveSettingsFile( saveMsg );
      } else {
        logger.error( "Couldn't find node" );
      }
    } else {
      logger.error( "No settings!" );
    }
    return false;
  }

  private boolean saveSettingsFile( String saveMsg ) {
    try {
      String contents = settings.asXML();
      if ( writeAccess.saveFile( SETTINGS_FILE, IOUtils.toInputStream( contents, CharsetHelper.getEncoding() ) ) ) {
        logger.debug( saveMsg );
        return true;
      }
      logger.error( "Error saving settings file." );
    } catch ( Exception e ) {
      logger.error( e );
    }
    return false;
  }

  @SuppressWarnings( "unchecked" )
  protected List<Element> getSettingsXmlSection( String section ) {
    return (List<Element>) (List<?>) settings.selectNodes( "/settings/" + section );
  }

  /**
   * where is this used??
   */
  public List<String> getTagValue( String tag ) {
    return getSettingsXmlSection( tag ).stream().map( element -> element.getText() ).collect( toList() );
  }
}

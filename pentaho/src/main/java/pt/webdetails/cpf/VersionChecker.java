/*!
* Copyright 2002 - 2017 Webdetails, a Hitachi Vantara company.  All rights reserved.
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

import org.apache.commons.lang.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.dom4j.Document;
import org.dom4j.DocumentException;
import org.dom4j.Node;
import org.dom4j.io.SAXReader;
import org.json.JSONException;
import org.json.JSONObject;
import pt.webdetails.cpf.messaging.JsonSerializable;
import pt.webdetails.cpf.repository.api.IReadAccess;
import pt.webdetails.cpf.utils.XmlParserFactoryProducer;

import java.io.IOException;
import java.util.ArrayList;

/**
 * Version checker for a standard marketplace plugin. Checks the local version
 * from version.xml in the plugin folder.
 */
public abstract class VersionChecker {

  protected Log logger = LogFactory.getLog( this.getClass() );
  protected PluginSettings settings;
  protected static String VERSION_FILE = "version.xml";

  public VersionChecker( PluginSettings pluginSettings ) {
    settings = pluginSettings;
  }

    /* ******************
     * Abstract methods */

  /**
   * @param branch The branch to check
   * @return The URL for the XML version file of the latest release in
   * this <code>branch</code>
   */
  protected abstract String getVersionCheckUrl( Branch branch );

  /* abstract methods *
   ********************/
  private static String[] branches;

  static {
    ArrayList<String> branchList = new ArrayList<String>();
    for ( Branch branch : Branch.values() ) {
      branchList.add( branch.toString() );
    }
    branches = branchList.toArray( new String[branchList.size()] );
  }

    /* ****************
     * Public methods */

  public String[] getBranches() {
    return branches;
  }

  /**
   * Compares currently installed version with the latest from the same branch.
   *
   * @return {@link CheckVersionResponse}
   */
  public CheckVersionResponse checkVersion() {
    Version installed = null;
    try {
      installed = getInstalledVersion();
    } catch ( Exception e ) {
      String msg = "Error attempting to read version.xml";
      logger.error( msg, e );
      return new CheckVersionResponse( CheckVersionResponse.Type.ERROR, msg, null );
    }

    String url = getVersionCheckUrl( installed.getBranch() );
    if ( url == null ) {
      String msg = "No URL found for this version.";
      logger.info( msg );
      SAXReader reader = XmlParserFactoryProducer.getSAXReader( null );
      Version latest = null;
      try {
        Document versionXml = reader.read( getVersionCheckUrl( Branch.STABLE ) );
        latest = new Version( versionXml );
      } catch ( DocumentException e ) {
        msg = "Could not parse remote file ";
        logger.info( msg, e );
        return new CheckVersionResponse( CheckVersionResponse.Type.ERROR, msg, null );
      }
      return new CheckVersionResponse( CheckVersionResponse.Type.INCONCLUSIVE, msg, latest.downloadUrl );
    }

    Version latest = null;
    try {
      SAXReader reader = XmlParserFactoryProducer.getSAXReader( null );
      Document versionXml = reader.read( url );
      latest = new Version( versionXml );
    } catch ( DocumentException e ) {
      String msg = "Could not parse remote file ";
      logger.info( msg, e );
      return new CheckVersionResponse( CheckVersionResponse.Type.ERROR, msg, null );
    }

    if ( installed.isSuperceededBy( latest ) ) {
      return new CheckVersionResponse( CheckVersionResponse.Type.UPDATE, null, latest.downloadUrl );
    } else {
      return new CheckVersionResponse( CheckVersionResponse.Type.LATEST, null, null );
    }
  }

  public String getVersion() {
    try {
      Version installed = getInstalledVersion();
      return installed.toString(); //getShortVersion();
    } catch ( Exception e ) {
      String msg = "Error attempting to read version.xml";
      logger.error( msg, e );
      return "unknown version";
    }
  }

    /* public methods *
     ******************/

  private Version getInstalledVersion() throws DocumentException, IOException {
    Version installed = null;
    IReadAccess systemDir = PluginEnvironment.repository().getPluginSystemReader( null );
    SAXReader reader = XmlParserFactoryProducer.getSAXReader( null );
    Document versionXml = reader.read( systemDir.getFileInputStream( VERSION_FILE ) );
    installed = new Version( versionXml );
    return installed;
  }

  /**
   * JSON result, {@code type} indicates what other info is available:<br>
   * latest: <br>
   * update: downloadUrl<br>
   * error: msg<br>
   * inconclusive: msg, downloadUrl<br>
   */
  public static class CheckVersionResponse implements JsonSerializable {

    public CheckVersionResponse( Type responseType, String message, String downloadUrl ) {
      type = responseType;
      msg = message;
      url = downloadUrl;
    }

    public enum Type {
      LATEST,
      UPDATE,
      INCONCLUSIVE,
      ERROR
    }

    private Type type;
    private String msg;
    private String url;

    public String getMessage() {
      return msg;
    }

    public Type getType() {
      return type;
    }

    @Override public JSONObject toJSON() throws JSONException {
      JSONObject obj = new JSONObject();
      obj.put( "result", type.toString().toLowerCase() );
      switch ( type ) {
        case LATEST:
        case ERROR:
          obj.put( "msg", msg );
          break;
        case INCONCLUSIVE:
          obj.put( "msg", msg );
          obj.put( "downloadUrl", url );
          break;
        case UPDATE:
          obj.put( "downloadUrl", msg );
          break;
      }

      return obj;
    }
  }

  public enum Branch {
    STABLE,
    TRUNK,
    LOCAL,
    UNKNOWN;
  }

  public static class Version {

    private String branchStr;
    private String version;
    private String buildId;
    private String downloadUrl;
    private Branch branch;

    public Version( Document xml ) {

      if ( xml == null ) {
        throw new IllegalArgumentException( "no document" );
      }

      Node versionNode = xml.selectSingleNode( "//version" );

      branchStr = getStringValue( versionNode, "branch", branchStr );
      version = getStringValue( versionNode, "version", version );
      buildId = getStringValue( versionNode, "buildId", buildId );
      downloadUrl = getStringValue( versionNode, "package_url", null );

      if ( branchStr == null ) {
        branchStr = getStringValue( versionNode, "@branch", branchStr );
      }
      if ( version == null ) {
        version = getStringValue( versionNode, "/version", version );
      }
      if ( buildId == null ) {
        buildId = getStringValue( versionNode, "@buildId", buildId );
      }
      if ( downloadUrl == null ) {
        downloadUrl = getStringValue( versionNode, "downloadUrl", null );
      }
      //TODO:check if parse was valid
    }

    public Branch getBranch() {

      if ( this.branch == null ) {
        if ( StringUtils.equals( branchStr, "TRUNK" ) ) {
          if ( StringUtils.startsWithIgnoreCase( buildId, "manual" ) ) {
            return Branch.LOCAL;
          } else {
            return Branch.TRUNK;
          }
        } else if ( StringUtils.equals( branchStr, "STABLE" ) ) {
          return Branch.STABLE;
        }
        return Branch.UNKNOWN;
      }

      return branch;
    }

    //assumes we're comparing a replaceable version
    public boolean isSuperceededBy( Version other ) {
      if ( getBranch().equals( other.getBranch() ) ) {
        switch ( getBranch() ) {
          case STABLE:
            return this.version.compareTo( other.version ) < 0;
          case TRUNK:
            return this.buildId.compareTo( other.buildId ) < 0;
          default:
            return false; //was implicit
        }
      }
      return false;
    }

    public String toString() {
      switch ( getBranch() ) {
        case LOCAL:
          return buildId;
        case TRUNK:
          return getBranch() + " build" + buildId;
        case STABLE:
          return "v" + version;
        case UNKNOWN:
        default:
          return getBranch().toString();
      }
    }
  }

  private static String getStringValue( Node node, String xpath, String defaultValue ) {
    Node valNode = node.selectSingleNode( xpath );
    if ( valNode != null ) {
      String value = valNode.getText();
      if ( !StringUtils.isEmpty( value ) ) {
        return value;
      }
    }
    return defaultValue;
  }
}

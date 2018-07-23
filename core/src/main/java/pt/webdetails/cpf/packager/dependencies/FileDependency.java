/* This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this file,
 * You can obtain one at http://mozilla.org/MPL/2.0/. */

package pt.webdetails.cpf.packager.dependencies;

import org.apache.commons.io.IOUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import pt.webdetails.cpf.PluginEnvironment;
import pt.webdetails.cpf.Util;
import pt.webdetails.cpf.context.api.IUrlProvider;
import pt.webdetails.cpf.packager.origin.PathOrigin;
import pt.webdetails.cpf.repository.api.IContentAccessFactory;

import java.io.IOException;
import java.io.InputStream;

/**
 * Basic file-based dependency with md5 checksums.
 */
public class FileDependency extends Dependency {

  private static Log logger = LogFactory.getLog( FileDependency.class );

  protected String filePath;
  protected PathOrigin origin;
  protected IUrlProvider urlProvider;
  private String hash;
  // TODO: why not just a timestamp?
  // use checksums for versions, otherwise use timestamps
  protected boolean useChecksumVersion = true;

  public FileDependency( String version, PathOrigin origin, String path, IUrlProvider urlProvider ) {
    super();
    this.filePath = path;
    this.hash = null;
    this.origin = origin;
    this.urlProvider = urlProvider;
  }

  protected String getCheckSum() {
    if ( hash == null ) {
      InputStream in = null;
      try {
        in = getFileInputStream();
        hash = Util.getMd5Digest( in );
      } catch ( Exception e ) {
        logger.error( "Could not compute md5 checksum.", e );
      } finally {
        IOUtils.closeQuietly( in );
      }
    }
    return hash;
  }

  public String getVersion() {
    return useChecksumVersion ? getCheckSum() : Long.toString( getTimeStamp() );
  }

  protected long getTimeStamp() {
    return origin.getReader( getContentFactory() ).getLastModified( filePath );
  }

  public InputStream getFileInputStream() throws IOException {
    if ( getContentFactory() != null ) {
      return origin.getReader(getContentFactory()).getFileInputStream(filePath);
    } else {
      logger.fatal( String.format( "Couldn't getFileInputStream() for filePath = '%s'. Unable to get ContentFactory.", filePath ));
      return null;
    }
  }

  /**
   * @return path for including this file
   */
  public String getDependencyInclude() {
    // the ?v=<version> is used to bypass browser cache when needed
    String version = getVersion();
    String urlAppend = ( ( version == null ) ? "" : "?v=" + version );
    return origin.getUrl( filePath, urlProvider ) + urlAppend;
  }

  @Override
  public String getContents() throws IOException {
    return Util.toString( getFileInputStream() );
  }

  public String getUrlFilePath() {
    return origin.getUrl( filePath, urlProvider );
  }

  protected IContentAccessFactory getContentFactory() {
    if ( PluginEnvironment.env() != null ) {
      return PluginEnvironment.env().getContentAccessFactory();
    } else {
      return null;
    }
  }

  public String toString() {
    return filePath;
  }
}

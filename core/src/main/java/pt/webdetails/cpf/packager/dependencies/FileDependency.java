/*! ******************************************************************************
 *
 * Pentaho
 *
 * Copyright (C) 2024 by Hitachi Vantara, LLC : http://www.pentaho.com
 *
 * Use of this software is governed by the Business Source License included
 * in the LICENSE.TXT file.
 *
 * Change Date: 2029-07-20
 ******************************************************************************/

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
    final IContentAccessFactory contentFactory = getContentFactory();
    if ( contentFactory != null ) {
      return this.origin.getReader( contentFactory ).getFileInputStream( this.filePath );
    }

    logger.fatal(
      String.format( "Couldn't getFileInputStream() for filePath = '%s'. Unable to get ContentFactory.", this.filePath )
    );

    return null;
  }

  /**
   * @return path for including this file
   */
  public String getDependencyInclude() {
    // the ?v=<version> is used to bypass browser cache when needed
    String version = getVersion();
    String urlAppend = ( ( version == null ) ? "" : "?v=" + version );

    return getUrlFilePath() + urlAppend;
  }

  @Override
  public String getContents() throws IOException {
    return Util.toString( getFileInputStream() );
  }

  public String getUrlFilePath() {
    return origin.getUrl( filePath, urlProvider );
  }

  protected IContentAccessFactory getContentFactory() {
    final PluginEnvironment environment = PluginEnvironment.env();
    if ( environment != null ) {
      return environment.getContentAccessFactory();
    }

    return null;
  }

  public String toString() {
    return this.filePath;
  }
}

/* This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this file,
 * You can obtain one at http://mozilla.org/MPL/2.0/. */

package pt.webdetails.cpf.packager.dependencies;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import pt.webdetails.cpf.Util;
import pt.webdetails.cpf.context.api.IUrlProvider;
import pt.webdetails.cpf.packager.origin.PathOrigin;
import pt.webdetails.cpf.repository.api.IRWAccess;

import java.io.IOException;
import java.io.InputStream;

/**
 * Base class for concatenated and minified files.
 */
public abstract class PackagedFileDependency extends FileDependency {

  private static Log logger = LogFactory.getLog( PackagedFileDependency.class );

  private Iterable<FileDependency> inputFiles;
  private IRWAccess writer;
  private boolean isSaved;

  public PackagedFileDependency( PathOrigin origin, String path, IRWAccess writer, Iterable<FileDependency> inputFiles,
                                 IUrlProvider urlProvider ) {
    super( null, origin, path, urlProvider );
    this.inputFiles = inputFiles;
    this.writer = writer;
  }

  @Override
  public synchronized InputStream getFileInputStream() throws IOException {
    if ( !isSaved ) {
      long startTime = System.currentTimeMillis();
      isSaved = writer.saveFile( filePath, minifyPackage( inputFiles ) );
      if ( !isSaved ) {
        throw new IOException( "Unable to save file " + filePath );
      } else {
        //release refs
        inputFiles = null;
        if ( logger.isDebugEnabled() ) {
          logger.debug( String.format( "Generated '%s' in %s", filePath, Util.getElapsedSeconds( startTime ) ) );
        }
      }
    }

    return super.getFileInputStream();
  }

  protected abstract InputStream minifyPackage( Iterable<FileDependency> inputFiles );

}

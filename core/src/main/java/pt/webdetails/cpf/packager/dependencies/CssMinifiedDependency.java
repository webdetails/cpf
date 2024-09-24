/* This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this file,
 * You can obtain one at http://mozilla.org/MPL/2.0/. */

package pt.webdetails.cpf.packager.dependencies;

import org.apache.commons.io.FilenameUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import pt.webdetails.cpf.Util;
import pt.webdetails.cpf.context.api.IUrlProvider;
import pt.webdetails.cpf.packager.origin.PathOrigin;
import pt.webdetails.cpf.repository.api.IRWAccess;

import java.io.IOException;
import java.io.InputStream;
import java.io.SequenceInputStream;
import java.util.Enumeration;
import java.util.Iterator;

/**
 * CSS files aren't minified, just concatenated.<br> Relative resource URLs are updated to reflect new location.
 */
public class CssMinifiedDependency extends PackagedFileDependency {

  private static Log logger = LogFactory.getLog( CssMinifiedDependency.class );

  public CssMinifiedDependency( PathOrigin origin, String path, IRWAccess writer, Iterable<FileDependency> inputFiles,
                                IUrlProvider urlProvider ) {
    super( origin, path, writer, inputFiles, urlProvider );
  }

  @Override
  protected InputStream minifyPackage( Iterable<FileDependency> inputFiles ) {
    return new SequenceInputStream( new CssReplacementStreamEnumeration( inputFiles.iterator() ) );
  }

  public static class CssReplacementStreamEnumeration implements Enumeration<InputStream> {

    private Iterator<FileDependency> deps;
    private CssUrlReplacer replacer;

    public CssReplacementStreamEnumeration( Iterator<FileDependency> deps ) {
      this.deps = deps;
      this.replacer = new CssUrlReplacer();
    }

    @Override
    public boolean hasMoreElements() {
      return deps.hasNext();
    }

    @Override
    public InputStream nextElement() {
      FileDependency dep = deps.next();
      try {
        String contents = Util.toString( dep.getFileInputStream() );
        //strip filename from url
        String originalUrlPath = FilenameUtils.getFullPath( dep.getUrlFilePath() );
        contents = replacer.processContents( contents, originalUrlPath );
        return Util.toInputStream( contents );
      } catch ( IOException e ) {
        logger.error( "Error getting input stream for dependency " + dep + ". Skipping..", e );
      } catch ( Exception e ) {
        logger.error( "Error with dependency " + dep + ". Skipping..", e );
      }
      return Util.toInputStream( "" );
    }
  }
}

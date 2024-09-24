/*! ******************************************************************************
 *
 * Pentaho
 *
 * Copyright (C) 2024 by Hitachi Vantara, LLC : http://www.pentaho.com
 *
 * Use of this software is governed by the Business Source License included
 * in the LICENSE.TXT file.
 *
 * Change Date: 2028-08-13
 ******************************************************************************/

package pt.webdetails.cpf.packager.dependencies;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import pt.webdetails.cpf.context.api.IUrlProvider;
import pt.webdetails.cpf.packager.origin.PathOrigin;
import pt.webdetails.cpf.repository.api.IRWAccess;

import java.io.IOException;
import java.io.InputStream;

public class MapDependency extends PackagedFileDependency {

  private static Log logger = LogFactory.getLog( MapDependency.class );

  public MapDependency( PathOrigin origin, String path,
                        IRWAccess writer,
                        Iterable<FileDependency> inputFiles,
                        IUrlProvider urlProvider ) {
    super( origin, path, writer, inputFiles, urlProvider );
  }

  @Override protected InputStream minifyPackage( Iterable<FileDependency> inputFiles ) {
    try {
      if ( inputFiles.iterator().hasNext() ) {
        return inputFiles.iterator().next().getFileInputStream();
      }
    } catch ( IOException e ) {
      logger.error( "Error getting input stream for file " + filePath );
      e.printStackTrace();
    }
    return null;
  }
}

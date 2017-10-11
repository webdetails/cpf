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

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

package org.pentaho.ctools.cpf.repository.bundle;

import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import pt.webdetails.cpf.repository.api.IBasicFile;

/**
 * Class {@code BasicFile} allows executing file operations on a resource pointed to using an Unified Resource Locator.
 * CPF depends heavily on {@code File} operations and on the assumption that a filesystem is used to fetch resources.
 * In OSGi, bundle resources are accessed via an {@code URL} and this class provides the abstraction that allows using
 * such resources as if they were filesystem resources.
 *
 * @see IBasicFile
 */
public final class BasicFile implements IBasicFile {

  private final URL url;

  public BasicFile( URL url ) {
    this.url = url;
  }

  @Override
  public InputStream getContents() throws IOException {
    return url.openStream();
  }

  @Override
  public String getName() {
    final String path = url.getPath();
    if ( path.length() == 0 ) {
      return path;
    }
    final int index = path.lastIndexOf( "/" );
    if ( index == -1 ) {
      return path;
    }
    if ( index + 1 == path.length() && path.length() > 1 ) {
      // folder, return previous component
      String trimmedPath = path.substring( 0, index );
      int previousIndex = trimmedPath.lastIndexOf( '/' );
      if ( previousIndex == -1 ) {
        return trimmedPath;
      }
      return trimmedPath.substring( previousIndex + 1 );
    }
    return path.substring( index + 1 );
  }

  @Override
  public String getFullPath() {
    return getCleanPath();
  }

  @Override
  public String getPath() {
    return getCleanPath();
  }

  private String getCleanPath() {
    final String path = url.getPath();
    if ( path.endsWith( "/" ) ) {
      return path.substring( 0, path.length() - 1 );
    }
    return url.getPath();
  }

  @Override
  public String getExtension() {
    final String path = url.getPath();
    if ( path.length() == 0 ) {
      return path;
    }
    final int index = path.lastIndexOf( "." );
    if ( index == -1 ) {
      return "";
    }
    return path.substring( index + 1 );
  }

  @Override
  public boolean isDirectory() {
    final String path = url.getPath();
    return path.endsWith( "/" );
  }
}

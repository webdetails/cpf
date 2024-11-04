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

package org.pentaho.ctools.cpf.repository.utils;

import java.io.File;
import java.nio.file.FileSystem;
import java.nio.file.Path;

public class FileSystemRWAccess extends FileBasedResourceAccess {
  private final FileSystem fileSystem;
  private final String basePath;
  private final String volumePath;
  private final String DEFAULT_PATH_SEPARATOR = "/";

  public FileSystemRWAccess( FileSystem fileSystem, String volumePath, String basePath ) {
    if ( fileSystem == null ) {
      throw new IllegalArgumentException( "filesystem cannot be null" );
    }
    if ( volumePath == null || volumePath.isEmpty() ) {
      throw new IllegalArgumentException( "filesystem cannot be null or empty" );
    }
    if ( !fileSystem.getPath( volumePath ).toFile().isDirectory() ) {
      throw new IllegalArgumentException( "volumePath must be an existing directory" );
    }
    this.fileSystem = fileSystem;
    this.volumePath = volumePath;
    this.basePath = basePath == null || basePath.isEmpty() ? DEFAULT_PATH_SEPARATOR : ( basePath.endsWith( DEFAULT_PATH_SEPARATOR ) ? basePath : basePath + DEFAULT_PATH_SEPARATOR );
  }

  @Override
  protected File getFile( String path ) {
    Path filePath = ( path == null
      ? this.fileSystem.getPath( this.volumePath, this.basePath )
      : this.fileSystem.getPath( this.volumePath, this.basePath, path ) )
      .toAbsolutePath();
    // TODO: check if path is not above <volumePath>/<basePath>
    return filePath.toFile();
  }
}

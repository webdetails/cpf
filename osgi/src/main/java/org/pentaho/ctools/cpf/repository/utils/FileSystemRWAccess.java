/*!
 * Copyright 2018 Webdetails, a Hitachi Vantara company. All rights reserved.
 *
 * This software was developed by Webdetails and is provided under the terms
 * of the Mozilla Public License, Version 2.0, or any later version. You may not use
 * this file except in compliance with the license. If you need a copy of the license,
 * please go to http://mozilla.org/MPL/2.0/. The Initial Developer is Webdetails.
 *
 * Software distributed under the Mozilla Public License is distributed on an "AS IS"
 * basis, WITHOUT WARRANTY OF ANY KIND, either express or implied. Please refer to
 * the license for the specific language governing your rights and limitations.
 */
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

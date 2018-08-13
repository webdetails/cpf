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
package org.pentaho.ctools.cpf.repository.rca;

import org.pentaho.platform.api.repository2.unified.webservices.RepositoryFileDto;
import pt.webdetails.cpf.repository.api.IBasicFile;
import pt.webdetails.cpf.repository.api.IReadAccess;
import pt.webdetails.cpf.repository.util.RepositoryHelper;

import java.io.IOException;
import java.io.InputStream;

/**
 * Class {@code RemoteBasicFile} implements the {@code IBasicFile} interface for a remote repository file.
 *
 * @see IBasicFile
 */
public class RemoteBasicFile implements IBasicFile {
  IReadAccess remote;
  RepositoryFileDto repositoryFile;
  String relativePath;

  public RemoteBasicFile( String basePath, IReadAccess remote, RepositoryFileDto dto ) {
    this.remote = remote;
    repositoryFile = dto;
    relativePath = RepositoryHelper.relativizePath( basePath, repositoryFile.getPath(), true );
  }

  @Override
  public InputStream getContents() throws IOException {
    return remote.getFileInputStream( getPath() );
  }

  @Override
  public String getName() {
    return repositoryFile.getName();
  }

  @Override
  public String getFullPath() {
    return repositoryFile.getPath();
  }

  @Override
  public String getPath() {
    return relativePath;
  }

  @Override
  public String getExtension() {
    final String path = repositoryFile.getPath();
    if ( path.length() == 0 ) {
      return path;
    }
    final int index = path.lastIndexOf( "." );
    if ( index <= 0 ) {
      return "";
    }
    return path.substring( index + 1 );
  }

  @Override
  public boolean isDirectory() {
    return repositoryFile.isFolder();
  }
}

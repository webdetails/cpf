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
package pt.webdetails.cpf.repository.rca;

import org.pentaho.platform.api.repository2.unified.webservices.RepositoryFileDto;
import pt.webdetails.cpf.repository.api.IBasicFileExtended;
import pt.webdetails.cpf.repository.api.IReadAccess;
import pt.webdetails.cpf.repository.util.RepositoryHelper;

import java.io.IOException;
import java.io.InputStream;

/**
 * Class {@code RemoteBasicFile} implements the {@code IBasicFileExtended} interface for a remote repository file.
 *
 * @see IBasicFileExtended
 */
public class RemoteBasicFile implements IBasicFileExtended {
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

  @Override
  public String getTitle() {
    return repositoryFile.getTitle();
  }

  @Override
  public String getCreatedDate() {
    return repositoryFile.getCreatedDate();
  }

  @Override
  public String getLastModifiedDate() {
    return repositoryFile.getLastModifiedDate();
  }

  @Override
  public long getFileSize() {
    return repositoryFile.getFileSize();
  }

  @Override
  public String getOwner() {
    return repositoryFile.getOwner();
  }

  @Override
  public String getDescription() {
    return repositoryFile.getDescription();
  }
}

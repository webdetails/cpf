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
package org.pentaho.ctools.cpf.repository.bundle;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import pt.webdetails.cpf.api.IFileContent;
import pt.webdetails.cpf.api.IUserContentAccessExtended;
import pt.webdetails.cpf.repository.api.FileAccess;
import pt.webdetails.cpf.repository.api.IBasicFile;
import pt.webdetails.cpf.repository.api.IBasicFileFilter;
import pt.webdetails.cpf.repository.api.IReadAccess;

import java.io.IOException;
import java.io.InputStream;
import java.util.List;

/**
 * Allows users to access the content of the available bundle resources in an OSGi environment.
 * Note: Write operations are currently not supported and there are no permission distinctions between
 * different users.
 *
 * @see IUserContentAccessExtended
 */
public final class UserContentAccess implements IUserContentAccessExtended {
  private static final Log logger = LogFactory.getLog( UserContentAccess.class );
  private IReadAccess readAccess;

  public UserContentAccess( IReadAccess readAccess ) {
    this.readAccess = readAccess;
  }

  @Override
  public boolean saveFile( String path, InputStream contents ) {
    logger.fatal( "Not implemented for the OSGi environment" );
    return false;
  }

  @Override
  public boolean copyFile( String pathFrom, String pathTo ) {
    logger.fatal( "Not implemented for the OSGi environment" );
    return false;
  }

  @Override
  public boolean deleteFile( String path ) {
    logger.fatal( "Not implemented for the OSGi environment" );
    return false;
  }

  @Override
  public boolean createFolder( String path ) {
    logger.fatal( "Not implemented for the OSGi environment" );
    return false;
  }

  @Override
  public boolean createFolder( String path, boolean isHidden ) {
    logger.fatal( "Not implemented for the OSGi environment" );
    return false;
  }

  @Override
  public boolean hasAccess( String filePath, FileAccess access ) {
    return this.readAccess.fileExists( filePath );
  }

  @Override
  public InputStream getFileInputStream( String path ) throws IOException {
    return this.readAccess.getFileInputStream( path );
  }

  @Override
  public boolean fileExists( String path ) {
    return this.readAccess.fileExists( path );
  }

  @Override
  public long getLastModified( String path ) {
    return this.readAccess.getLastModified( path );
  }

  @Override
  public List<IBasicFile> listFiles( String path, IBasicFileFilter filter, int maxDepth, boolean includeDirs, boolean showHiddenFilesAndFolders ) {
    return this.readAccess.listFiles( path, filter, maxDepth, includeDirs, showHiddenFilesAndFolders );
  }

  @Override
  public List<IBasicFile> listFiles( String path, IBasicFileFilter filter, int maxDepth, boolean includeDirs ) {
    return this.readAccess.listFiles( path, filter, maxDepth, includeDirs );
  }

  @Override
  public List<IBasicFile> listFiles( String path, IBasicFileFilter filter, int maxDepth ) {
    return this.readAccess.listFiles( path, filter, maxDepth );
  }

  @Override
  public List<IBasicFile> listFiles( String path, IBasicFileFilter filter ) {
    return this.readAccess.listFiles( path, filter );
  }

  @Override
  public IBasicFile fetchFile( String path ) {
    return this.readAccess.fetchFile( path );
  }

  @Override
  public boolean saveFile( IFileContent file ) {
    logger.fatal( "Not implemented for the OSGi environment" );
    return false;
  }
}

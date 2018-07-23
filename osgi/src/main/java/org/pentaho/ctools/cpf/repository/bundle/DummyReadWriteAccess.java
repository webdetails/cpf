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
import org.pentaho.ctools.cpf.repository.factory.ContentAccessFactory;
import pt.webdetails.cpf.repository.api.IBasicFile;
import pt.webdetails.cpf.repository.api.IBasicFileFilter;
import pt.webdetails.cpf.repository.api.IRWAccess;
import pt.webdetails.cpf.repository.api.IReadAccess;

import java.io.IOException;
import java.io.InputStream;
import java.util.List;

/**
 * Class {@code DummyReadWriteAccess} provides a wrapper around an {@code IReadAccess} provider that fakes the
 * {@code IRWAccess} operations by simply replying true without actually doing anything. Read operations are forwarded
 * to the wrapped {@code IReadAccess} instance.
 *
 * @see IReadAccess
 * @see IRWAccess
 */
public class DummyReadWriteAccess implements IReadAccess, IRWAccess {
  IReadAccess readAccess;
  private static final Log logger = LogFactory.getLog( ContentAccessFactory.class );

  public DummyReadWriteAccess( IReadAccess readAccess ) {
    this.readAccess = readAccess;
  }

  @Override
  public boolean saveFile( String path, InputStream contents ) {
    logger.info( "faked saveFile for the OSGi environment" );
    return true;
  }

  @Override
  public boolean copyFile( String pathFrom, String pathTo ) {
    logger.info( "faked copyFile for the OSGi environment" );
    return true;
  }

  @Override
  public boolean deleteFile( String path ) {
    logger.info( "faked deleteFile for the OSGi environment" );
    return true;
  }

  @Override
  public boolean createFolder( String path ) {
    logger.info( "faked createFolder for the OSGi environment" );
    return true;
  }

  @Override
  public boolean createFolder( String path, boolean isHidden ) {
    logger.info( "faked createFolder for the OSGi environment" );
    return true;
  }

  @Override
  public InputStream getFileInputStream( String path ) throws IOException {
    return readAccess.getFileInputStream( path );
  }

  @Override
  public boolean fileExists( String path ) {
    return readAccess.fileExists( path );
  }

  @Override
  public long getLastModified( String path ) {
    return readAccess.getLastModified( path );
  }

  @Override
  public List<IBasicFile> listFiles( String path, IBasicFileFilter filter, int maxDepth, boolean includeDirs, boolean showHiddenFilesAndFolders ) {
    return readAccess.listFiles( path, filter, maxDepth, includeDirs, showHiddenFilesAndFolders );
  }

  @Override
  public List<IBasicFile> listFiles( String path, IBasicFileFilter filter, int maxDepth, boolean includeDirs ) {
    return readAccess.listFiles( path, filter, maxDepth, includeDirs );
  }

  @Override
  public List<IBasicFile> listFiles( String path, IBasicFileFilter filter, int maxDepth ) {
    return readAccess.listFiles( path, filter, maxDepth );
  }

  @Override
  public List<IBasicFile> listFiles( String path, IBasicFileFilter filter ) {
    return readAccess.listFiles( path, filter );
  }

  @Override
  public IBasicFile fetchFile( String path ) {
    return readAccess.fetchFile( path );
  }
}

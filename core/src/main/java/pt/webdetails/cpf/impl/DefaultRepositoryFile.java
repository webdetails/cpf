/*! ******************************************************************************
 *
 * Pentaho
 *
 * Copyright (C) 2024 by Hitachi Vantara, LLC : http://www.pentaho.com
 *
 * Use of this software is governed by the Business Source License included
 * in the LICENSE.TXT file.
 *
 * Change Date: 2029-07-20
 ******************************************************************************/


package pt.webdetails.cpf.impl;

import org.apache.commons.io.FileUtils;
import org.apache.commons.io.FilenameUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import pt.webdetails.cpf.repository.IRepositoryFile;
import pt.webdetails.cpf.repository.IRepositoryFileFilter;

import java.io.File;
import java.io.IOException;

public class DefaultRepositoryFile implements IRepositoryFile {

  private static Log log = LogFactory.getLog( DefaultRepositoryFile.class );

  private File file;

  public DefaultRepositoryFile( File file ) {
    this.file = file;
  }

  @Override
  public boolean isDirectory() {
    return file.isDirectory();
  }

  @Override
  public String getFileName() {
    return file.getName();
  }

  @Override
  public String getSolutionPath() {
    return file.getAbsolutePath();
  }

  @Override
  public String getSolution() {
    return file.getPath();
  }

  @Override
  public String getFullPath() {
    return file.getAbsolutePath();
  }

  @Override
  public IRepositoryFile[] listFiles() {
    throw new UnsupportedOperationException(); //FIXME
    //    return new IRepositoryFile[0];
  }

  @Override
  public IRepositoryFile[] listFiles( IRepositoryFileFilter iff ) {
    throw new UnsupportedOperationException(); //FIXME
    //    return new IRepositoryFile[0];
  }

  @Override
  public boolean isRoot() {
    throw new UnsupportedOperationException(); //FIXME
    //    // why?
    //    return false;
  }

  @Override
  public IRepositoryFile retrieveParent() {
    return new DefaultRepositoryFile( file.getParentFile() );
  }

  @Override
  public byte[] getData() {

    if ( file != null && file.exists() && file.canRead() ) {
      try {
        return FileUtils.readFileToByteArray( file );
      } catch ( IOException e ) {
        log.error( e );
      }
    }
    // this should throw something
    return null;
  }

  @Override
  public boolean exists() {
    return file.exists();
  }

  @Override
  public long getLastModified() {
    return file.lastModified();
  }

  @Override
  public String getExtension() {
    return FilenameUtils.getExtension( file.getName() );
  }

}

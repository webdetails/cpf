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

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import pt.webdetails.cpf.repository.api.IBasicFile;
import pt.webdetails.cpf.repository.api.IBasicFileFilter;
import pt.webdetails.cpf.repository.api.IRWAccess;
import pt.webdetails.cpf.repository.api.IReadAccess;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.function.LongSupplier;

public abstract class OverlayAccess<T extends IRWAccess> implements IRWAccess {
  protected final T writeAccess;
  protected final List<IReadAccess> readAccessList;
  protected final String DEFAULT_PATH_SEPARATOR = "/";
  private final String basePath;
  protected static final Log logger = LogFactory.getLog( OverlayAccess.class );

  public OverlayAccess( String basePath, T writeAccess, List<IReadAccess> readAccessList ) {
    if ( writeAccess == null ) {
      throw new IllegalArgumentException( "writeAccess cannot be null" );
    }
    this.writeAccess = writeAccess;
    this.readAccessList = readAccessList == null ? Collections.emptyList() : readAccessList;
    this.basePath = ( basePath == null || basePath.isEmpty() ) ? DEFAULT_PATH_SEPARATOR : ( basePath.endsWith( DEFAULT_PATH_SEPARATOR ) ? basePath : basePath + DEFAULT_PATH_SEPARATOR );
  }

  // helper methods

  private String buildPath( String path ) {
    if ( path == null ) {
      return this.basePath;
    }

    String fullPath = this.basePath;
    if ( path.startsWith( DEFAULT_PATH_SEPARATOR ) ) {
      fullPath += path.substring( 1 );
    } else {
      fullPath += path;
    }

    return fullPath; //TODO: normalize and guard against accessing above basePath
  }

  /**
   * Extend IBasicFile interface, but delay obtaining the possibly costly lastModfied value
   * @param basicFile
   * @param lastModified
   * @return
   */
  private IBasicFileExt extendBasicFile( IBasicFile basicFile, LongSupplier lastModified ) {
    return new IBasicFileExt() {
      @Override
      public long getLastModified() {
        return lastModified.getAsLong();
      }

      @Override
      public InputStream getContents() throws IOException {
        return basicFile.getContents();
      }

      @Override
      public String getName() {
        return basicFile.getName();
      }

      @Override
      public String getFullPath() {
        return basicFile.getFullPath();
      }

      @Override
      public String getPath() {
        /* remove basePath prefix */
        String path = basicFile.getFullPath();
        if ( path.startsWith( basePath ) ) {
          path = path.substring( basePath.length() );
        }
        return path;
      }

      @Override
      public String getExtension() {
        return basicFile.getExtension();
      }

      @Override
      public boolean isDirectory() {
        return basicFile.isDirectory();
      }
    };
  }

  protected IBasicFileExt obtainReadAccess( String path ) {
    String fullPath = buildPath( path );
    IBasicFile basicFile;

    logger.info( "Overlay: read( basePath: " + basePath + "): " + fullPath );

    /* check write access layer */
    if ( writeAccess.fileExists( fullPath ) ) {
      basicFile = writeAccess.fetchFile( fullPath );
      if ( basicFile != null ) {
        return extendBasicFile( basicFile, () -> writeAccess.getLastModified( fullPath ) );
      }
    }

    /* check other layers */
    for ( IReadAccess readAccess : readAccessList ) {
      if ( readAccess.fileExists( fullPath ) ) {
        basicFile = readAccess.fetchFile( fullPath );
        if ( basicFile != null ) {
          return extendBasicFile( basicFile, () -> 0L );
        }
      }
    }

    return null;
  }

  @Override
  public InputStream getFileInputStream( String path ) throws IOException {
    IBasicFileExt basicFile = obtainReadAccess( path );
    if ( basicFile != null ) {
      return basicFile.getContents();
    }
    return null;
  }

  @Override
  public boolean fileExists( String path ) {
    return obtainReadAccess( path ) != null;
  }

  @Override
  public long getLastModified( String path ) {
    IBasicFileExt basicFile = obtainReadAccess( path );
    if ( basicFile != null ) {
      return basicFile.getLastModified();
    }
    return 0L;
  }

  @Override
  public List<IBasicFile> listFiles( String path, IBasicFileFilter filter, int maxDepth, boolean includeDirs, boolean showHiddenFilesAndFolders ) {
    ArrayList<IBasicFile> result = new ArrayList<>();
    String fullPath = buildPath( path );

    // get result from RW layer
    List<IBasicFile> writeLayerFiles = writeAccess.listFiles( fullPath, filter, maxDepth, includeDirs, showHiddenFilesAndFolders );
    if ( writeLayerFiles != null ) {
      result.addAll( writeLayerFiles );
    }

    // add non-overlapping results from read-only layers
    for ( IReadAccess readAccess : this.readAccessList ) {
      List<IBasicFile> files = readAccess.listFiles( fullPath, filter, maxDepth, includeDirs, showHiddenFilesAndFolders );
      if ( files != null ) {
        for ( IBasicFile file : files ) {
          if ( !result.contains( file ) ) {
            result.add( file );
          }
        }
      }
    }
    return result;
  }

  @Override
  public List<IBasicFile> listFiles( String path, IBasicFileFilter filter, int maxDepth, boolean includeDirs ) {
    return listFiles( path, filter, maxDepth, includeDirs, false );
  }

  @Override
  public List<IBasicFile> listFiles( String path, IBasicFileFilter filter, int maxDepth ) {
    return listFiles( path, filter, maxDepth, true, false );
  }

  @Override
  public List<IBasicFile> listFiles( String path, IBasicFileFilter filter ) {
    return listFiles( path, filter, 1, false, false );
  }

  @Override
  public IBasicFile fetchFile( String path ) {
    return obtainReadAccess( path );
  }

  @Override
  public boolean saveFile( String path, InputStream contents ) {
    String fullPath = buildPath( path );
    return writeAccess.saveFile( fullPath, contents );
  }

  @Override
  public boolean copyFile( String pathFrom, String pathTo ) {
    try {
      // we may be copying across layers
      InputStream sourceContents = getFileInputStream( pathFrom );
      if ( sourceContents == null ) {
        logger.error( "copy failed: could not read input file: " + pathFrom );
        return false;
      }
      String fullPathTo = buildPath( pathTo );
      return writeAccess.saveFile( fullPathTo, sourceContents );
    } catch ( IOException ex ) {
      logger.error( ex );
      return false;
    }
  }

  @Override
  public boolean deleteFile( String path ) {
    String fullPath = buildPath( path );
    return writeAccess.deleteFile( fullPath );
  }

  @Override
  public boolean createFolder( String path ) {
    return createFolder( path, false );
  }

  @Override
  public boolean createFolder( String path, boolean isHidden ) {
    String fullPath = buildPath( path );
    return writeAccess.createFolder( fullPath, isHidden );
  }
}

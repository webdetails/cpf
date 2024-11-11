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

package pt.webdetails.cpf.localization.test;

import pt.webdetails.cpf.repository.api.IBasicFile;
import pt.webdetails.cpf.repository.api.IBasicFileFilter;
import pt.webdetails.cpf.repository.api.IRWAccess;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

public class PluginWriteAccess implements IRWAccess {

  private final List<IBasicFile> storedFiles = new ArrayList<>();

  public List<IBasicFile> getStoredFiles() {
    return storedFiles;
  }

  @Override public boolean saveFile( String path, InputStream contents ) {
    return storedFiles.add( new BasicFile( path ) );
  }

  @Override public boolean copyFile( String pathFrom, String pathTo ) {
    return true;
  }

  @Override public boolean deleteFile( String path ) {
    return fileExists( path ) && storedFiles.remove( fetchFile( path ) );
  }

  @Override public boolean createFolder( String path ) {
    return true;
  }

  @Override public boolean createFolder( String path, boolean isHidden ) {
    return true;
  }

  @Override public InputStream getFileInputStream( String path ) throws IOException {
    return fileExists( path ) ? fetchFile( path ).getContents() : null;
  }

  @Override public boolean fileExists( String path ) {
    return storedFiles.contains( new BasicFile( path ) );
  }

  @Override public long getLastModified( String path ) {
    return 0;
  }

  @Override public List<IBasicFile> listFiles( String path, IBasicFileFilter filter, int maxDepth, boolean includeDirs,
                                               boolean showHiddenFilesAndFolders ) {
    List<IBasicFile> filteredFiles = new ArrayList<>();

    if ( filter != null ) {
      for ( IBasicFile file : storedFiles ) {
        if ( filter.accept( file ) ) {
          filteredFiles.add( file );
        }
      }
    }

    return filteredFiles;
  }

  @Override public List<IBasicFile> listFiles( String path, IBasicFileFilter filter, int maxDepth,
                                               boolean includeDirs ) {
    return listFiles( path, filter, maxDepth, includeDirs, true );
  }

  @Override public List<IBasicFile> listFiles( String path, IBasicFileFilter filter, int maxDepth ) {
    return listFiles( path, filter, maxDepth, true, true );
  }

  @Override public List<IBasicFile> listFiles( String path, IBasicFileFilter filter ) {
    return listFiles( path, filter, -1, true, true );
  }

  @Override public IBasicFile fetchFile( String path ) {
    return fileExists( path ) ? storedFiles.get( storedFiles.indexOf( new BasicFile( path ) ) ) : null;
  }
}

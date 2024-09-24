/*!
 * Copyright 2002 - 2021 Webdetails, a Hitachi Vantara company.  All rights reserved.
 *
 * This software was developed by Webdetails and is provided under the terms
 * of the Mozilla Public License, Version 2.0, or any later version. You may not use
 * this file except in compliance with the license. If you need a copy of the license,
 * please go to  http://mozilla.org/MPL/2.0/. The Initial Developer is Webdetails.
 *
 * Software distributed under the Mozilla Public License is distributed on an "AS IS"
 * basis, WITHOUT WARRANTY OF ANY KIND, either express or  implied. Please refer to
 * the license for the specific language governing your rights and limitations.
 */
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

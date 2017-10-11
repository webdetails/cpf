/*!
 * Copyright 2002 - 2017 Webdetails, a Hitachi Vantara company.  All rights reserved.
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
import pt.webdetails.cpf.repository.api.IReadAccess;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

public class SomeFolderReadAccess implements IReadAccess {

  private List<IBasicFile> files = new ArrayList<IBasicFile>();

  public SomeFolderReadAccess( List<IBasicFile> files ) {
    this.files = files;
  }

  public List<IBasicFile> getFiles() {
    return files;
  }

  @Override public InputStream getFileInputStream( String path ) throws IOException {
    return fileExists( path ) ? fetchFile( path ).getContents() : null;
  }

  @Override public boolean fileExists( String path ) {
    return files != null && files.contains( new BasicFile( path ) );
  }

  @Override public long getLastModified( String path ) {
    return 0;
  }

  @Override public List<IBasicFile> listFiles( String path, IBasicFileFilter filter, int maxDepth, boolean includeDirs,
                                               boolean showHiddenFilesAndFolders ) {

    List<IBasicFile> filteredFiles = new ArrayList<IBasicFile>();

    for ( IBasicFile file : files ) {

      if ( filter != null && filter.accept( file ) ) {
        filteredFiles.add( file );
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
    return fileExists( path ) ? files.get( files.indexOf( new BasicFile( path ) ) ) : null;
  }
}

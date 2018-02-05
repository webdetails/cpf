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
package pt.webdetails.cpf.repository.bundle;

import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.util.Collections;
import java.util.Enumeration;
import java.util.Iterator;
import java.util.List;
import java.util.Spliterator;
import java.util.Spliterators;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import java.util.stream.StreamSupport;
import org.osgi.framework.Bundle;
import pt.webdetails.cpf.repository.api.IBasicFile;
import pt.webdetails.cpf.repository.api.IBasicFileFilter;
import pt.webdetails.cpf.repository.api.IReadAccess;

public final class ReadAccess implements IReadAccess {

  public Bundle getBundle() {
    return bundle;
  }
  public void setBundle( Bundle bundle ) {
    this.bundle = bundle;
  }
  private Bundle bundle;


  public InputStream getFileInputStream( String path ) throws IOException {
    final URL resourceURL = this.bundle.getEntry( path );
    if ( resourceURL == null ) {
      return null;
    }
    return resourceURL.openStream();
  }

  public boolean fileExists( String path ) {
    return this.bundle.getEntry( path ) != null;
  }

  public long getLastModified( String path ) {
    return this.bundle.getLastModified();
  }

  public List<IBasicFile> listFiles( String path, IBasicFileFilter filter ) {
    return listFiles( path, filter, -1 );
  }

  public List<IBasicFile> listFiles( String path, IBasicFileFilter filter, int maxDepth ) {
    return listFiles( path, filter, maxDepth, false );
  }

  public List<IBasicFile> listFiles( String path, IBasicFileFilter filter, int maxDepth, boolean includeDirs ) {
    return listFiles( path, filter, maxDepth, includeDirs, false );
  }

  public List<IBasicFile> listFiles( String path, IBasicFileFilter filter, int maxDepth, boolean includeDirs, boolean showHiddenFilesAndFolders ) {
    if ( maxDepth < 0 ) {
      return listFiles( path, filter, true );
    } else {
      return listFiles( path, filter, false );
    }
  }

  private List<IBasicFile> listFiles( String path, IBasicFileFilter filter, boolean recursive ) {
    Enumeration<URL> entries = this.bundle.findEntries( path, null, recursive );

    if ( entries == null ) {
      return Collections.emptyList();
    }

    return enumerationAsStream( entries )
      .map( url -> new BasicFile( url ) )
      .filter( file -> filter.accept( file ) )
      .collect( Collectors.toList() );
  }

  private <T> Stream<T> enumerationAsStream( Enumeration<T> e ) {
    return StreamSupport.stream(
      Spliterators.spliteratorUnknownSize(
        new Iterator<T>() {
          public T next() {
            return e.nextElement();
          }
          public boolean hasNext() {
            return e.hasMoreElements();
          }
        },
        Spliterator.ORDERED ), false );
  }

  public IBasicFile fetchFile( String path ) {
    URL url = this.bundle.getEntry( path );
    if ( url == null ) {
      return null;
    }

    return new BasicFile( url );
  }
}

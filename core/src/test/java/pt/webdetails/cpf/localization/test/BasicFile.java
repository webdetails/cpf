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

package pt.webdetails.cpf.localization.test;

import pt.webdetails.cpf.Util;
import pt.webdetails.cpf.repository.api.IBasicFile;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;

public class BasicFile implements IBasicFile {

  private String path;

  public BasicFile( String path ) {
    this.path = path;
  }

  @Override public InputStream getContents() throws IOException {
    return new ByteArrayInputStream( path.getBytes() );
  }

  @Override public String getName() {
    return path.substring( path.lastIndexOf( Util.SEPARATOR ) + 1 );
  }

  @Override public String getFullPath() {
    return path;
  }

  @Override public String getPath() {
    return path;
  }

  @Override public String getExtension() {
    return path.substring( path.lastIndexOf( '.' ) + 1 );
  }

  @Override public boolean isDirectory() {
    return false;
  }

  @Override public boolean equals( Object o ) {
    return ( o != null && o instanceof BasicFile ) ? this.getPath().equals( ( (BasicFile) o ).getPath() ) : false;
  }
}

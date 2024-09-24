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

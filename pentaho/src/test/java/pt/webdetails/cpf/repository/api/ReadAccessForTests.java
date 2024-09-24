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

package pt.webdetails.cpf.repository.api;

import java.io.IOException;
import java.io.InputStream;
import java.util.List;

public class ReadAccessForTests implements IReadAccess {

  @Override
  public InputStream getFileInputStream( String s ) throws IOException {
    return null;
  }

  @Override
  public boolean fileExists( String s ) {
    return false;
  }

  @Override
  public long getLastModified( String s ) {
    return 0;
  }

  @Override
  public List<IBasicFile> listFiles( String s, IBasicFileFilter iBasicFileFilter, int i, boolean b, boolean b2 ) {
    return null;
  }

  @Override
  public List<IBasicFile> listFiles( String s, IBasicFileFilter iBasicFileFilter, int i, boolean b ) {
    return null;
  }

  @Override
  public List<IBasicFile> listFiles( String s, IBasicFileFilter iBasicFileFilter, int i ) {
    return null;
  }

  @Override
  public List<IBasicFile> listFiles( String s, IBasicFileFilter iBasicFileFilter ) {
    return null;
  }

  @Override
  public IBasicFile fetchFile( String s ) {
    return null;
  }
}

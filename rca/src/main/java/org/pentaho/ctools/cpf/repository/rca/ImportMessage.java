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

package org.pentaho.ctools.cpf.repository.rca;

import java.io.InputStream;

/**
 * Class {@code ImportMessage} holds the information required to do a POST to the /repo/files/import endpoint.
 *
 * @see ImportMessageBodyWriter
 */
public class ImportMessage {
  public String importDir;
  public boolean overwrite;
  public String filename;
  public InputStream contents;

  public ImportMessage( String importDir, String filename, InputStream contents, boolean overwrite ) {
    this.importDir = importDir;
    this.filename = filename;
    this.contents = contents;
    this.overwrite = overwrite;
  }

  @Override
  public String toString() {
    return "ImportMessage{ " + importDir + "/" + filename + ", overwrite=" + overwrite + "}";
  }
}

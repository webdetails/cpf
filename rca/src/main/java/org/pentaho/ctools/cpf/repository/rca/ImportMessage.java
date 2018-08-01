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

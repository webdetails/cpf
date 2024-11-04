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


package pt.webdetails.cpf.utils;

// because SingleStringPlaceholder wouldn't sound nice
public class CharsetHelper {
  // TODO: we may want to read from config
  // and differentiate file access from client output at some point

  /**
   * Use this when unsure
   *
   * @return UTF-8
   */
  public static final String getEncoding() {
    // java 7: return StandardCharsets.UTF_8.toString();
    return "UTF-8";
  }
}

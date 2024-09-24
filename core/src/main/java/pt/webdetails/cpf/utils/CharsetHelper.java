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

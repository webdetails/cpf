/* This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this file,
 * You can obtain one at http://mozilla.org/MPL/2.0/. 
 */

package pt.webdetails.cpf.utils;

// because SingleStringPlaceholder wouldn't sound nice
public class CharsetHelper {
    // TODO: we may want to read from config
    // and differentiate file access from client output at some point
    /**
     * Use this when unsure
     * @return UTF-8
     */
    public static final String getEncoding() {
      // java 7: return StandardCharsets.UTF_8.toString();
      return "UTF-8";
    }
}

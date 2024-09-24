/* This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this file,
 * You can obtain one at http://mozilla.org/MPL/2.0/. */

package pt.webdetails.cpf.resources;

/**
 * @author dfscm
 */
public interface IResourceLoader {
  public String getResourceAsString( Class<? extends Object> type, String string );
  public String getPluginSetting( Class<?> type, String string );
}

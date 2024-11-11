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
package pt.webdetails.cpf.repository.pentaho;

import pt.webdetails.cpf.repository.api.IRWAccess;

public class SystemPluginRWAccess extends SystemPluginResourceAccess implements IRWAccess {

  public SystemPluginRWAccess(ClassLoader classLoader, String basePath) {
    super(classLoader, basePath);
  }

  public SystemPluginRWAccess(String pluginId, String basePath) {
    super(pluginId, basePath);
  }
  
}

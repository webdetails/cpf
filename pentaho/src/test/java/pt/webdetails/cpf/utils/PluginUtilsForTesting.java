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


package pt.webdetails.cpf.utils;

import java.io.File;

public class PluginUtilsForTesting extends PluginUtils {

  public PluginUtilsForTesting( String pluginName, String pluginDir) {
   setPluginName( pluginName );
   setPluginDirectory( new File( pluginDir ) );
  }

  @Override
  public void initialize(){

  }
}

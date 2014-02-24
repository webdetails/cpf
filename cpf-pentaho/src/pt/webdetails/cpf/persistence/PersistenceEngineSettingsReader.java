/*!
* Copyright 2002 - 2013 Webdetails, a Pentaho company. All rights reserved.
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

package pt.webdetails.cpf.persistence;


import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import pt.webdetails.cpf.repository.api.IReadAccess;
import pt.webdetails.cpf.repository.pentaho.PluginLegacySolutionResourceAccess;

import java.io.InputStream;

public class PersistenceEngineSettingsReader {

  private static final Log logger = LogFactory.getLog( PersistenceEngineSettingsReader.class );

  public InputStream getConfigurationInputStream() {
    InputStream conf;
    try {
      IReadAccess reader = new PluginLegacySolutionResourceAccess( "cpf" );
      conf =  reader.getFileInputStream( "orient.xml" );
    } catch ( Exception e ) {
      logger.warn("Falling back to built-in config");
      conf = getClass().getResourceAsStream("orient.xml");
    }
    return conf;
  }
}

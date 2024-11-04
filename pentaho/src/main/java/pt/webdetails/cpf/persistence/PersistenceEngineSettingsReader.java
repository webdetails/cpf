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


package pt.webdetails.cpf.persistence;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.pentaho.platform.engine.core.system.PentahoSessionHolder;
import pt.webdetails.cpf.repository.api.IReadAccess;
import pt.webdetails.cpf.repository.pentaho.unified.UserContentRepositoryAccess;

import java.io.InputStream;

public class PersistenceEngineSettingsReader {

  private static final Log logger = LogFactory.getLog( PersistenceEngineSettingsReader.class );

  public InputStream getConfigurationInputStream() {
    InputStream conf;
    try {
      IReadAccess reader = new UserContentRepositoryAccess( PentahoSessionHolder.getSession(), "cpf" );
      conf =  reader.getFileInputStream( "orient.xml" );
    } catch ( Throwable e ) {
      logger.warn( "Falling back to built-in config" );
      conf = getClass().getResourceAsStream( "orient.xml" );
    }
    return conf;
  }


}

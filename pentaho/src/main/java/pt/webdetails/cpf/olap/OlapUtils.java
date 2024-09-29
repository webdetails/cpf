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


package pt.webdetails.cpf.olap;

import javax.sql.DataSource;

import org.pentaho.platform.api.data.IDBDatasourceService;
import org.pentaho.platform.engine.core.system.PentahoSystem;
import org.pentaho.platform.plugin.action.mondrian.catalog.MondrianCatalog;


/**
 * @author pedro
 */
public class OlapUtils extends AbstractOlapUtils {


  @Override
  protected String getJndiFromCatalog( MondrianCatalog catalog ) {
    return catalog.getJndi();
  }

  @Override
  protected DataSource getDatasourceImpl( String dataSourceName ) throws Exception {
    IDBDatasourceService datasourceService = PentahoSystem.getObjectFactory().get( IDBDatasourceService.class, null );
    return datasourceService.getDataSource( dataSourceName );
  }

}

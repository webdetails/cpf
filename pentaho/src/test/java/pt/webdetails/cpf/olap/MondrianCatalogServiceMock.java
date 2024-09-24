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

import org.pentaho.platform.api.engine.IPentahoSession;
import org.pentaho.platform.plugin.action.mondrian.catalog.IMondrianCatalogService;
import org.pentaho.platform.plugin.action.mondrian.catalog.MondrianCatalog;
import org.pentaho.platform.plugin.action.mondrian.catalog.MondrianCatalogServiceException;
import org.pentaho.platform.plugin.action.mondrian.catalog.MondrianCube;
import org.pentaho.platform.plugin.action.mondrian.catalog.MondrianSchema;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class MondrianCatalogServiceMock implements IMondrianCatalogService {
  @Override
  public List<MondrianCatalog> listCatalogs( IPentahoSession iPentahoSession, boolean b ) {
    List<MondrianCatalog> catalogs = new ArrayList<>();

    catalogs.add( new MondrianCatalog( "catalog1Name", "catalog1DataSourceInfo", "catalog1Definition",
      createSchema( "testSchema1" ) ) );
    catalogs.add( new MondrianCatalog( "catalog2Name", "catalog2DataSourceInfo", "catalog2Definition",
      createSchema( "testSchema2" ) ) );

    return catalogs;
  }

  @Override
  public void addCatalog( MondrianCatalog mondrianCatalog, boolean b, IPentahoSession iPentahoSession )
    throws MondrianCatalogServiceException {
  }

  @Override
  public MondrianCatalog getCatalog( String s, IPentahoSession iPentahoSession ) {
    return listCatalogs( iPentahoSession, true ).stream()
      .filter( mondrianCatalog -> mondrianCatalog.getName().equals( s ) ).findAny()
      .orElse( null );
  }

  @Override
  public MondrianSchema loadMondrianSchema( String s, IPentahoSession iPentahoSession ) {
    return null;
  }

  @Override
  public void removeCatalog( String s, IPentahoSession iPentahoSession ) {
  }

  @Override
  public void reInit( IPentahoSession iPentahoSession ) throws MondrianCatalogServiceException {
  }

  @Override
  public void addCatalog( InputStream inputStream, MondrianCatalog mondrianCatalog, boolean b,
                          IPentahoSession iPentahoSession ) {
  }

  private MondrianSchema createSchema( String schemaName ) {
    return new MondrianSchema( schemaName, createCubes( 2 ) );
  }

  private List<MondrianCube> createCubes( int x ) {
    List<MondrianCube> cubes = new ArrayList<>();

    for ( int i = 0; i < x; i++ ) {
      cubes.add( new MondrianCube( "name" + i, "identifier" + i ) );
    }

    return cubes;
  }
}

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

public class MondrianCatalogServiceMock implements IMondrianCatalogService {

  @Override
  public List<MondrianCatalog> listCatalogs( IPentahoSession iPentahoSession, boolean b ) {
    List<MondrianCatalog> catalogs = new ArrayList<MondrianCatalog>();

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
    return null;
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
    MondrianSchema schema = new MondrianSchema( schemaName, createCubes( 2 ) );
    return schema;
  }

  private List<MondrianCube> createCubes( int x ) {
    List<MondrianCube> cubes = new ArrayList<MondrianCube>();

    for ( int i = 0; i < x; i++ ) {
      cubes.add( new MondrianCube( "name" + i, "identifier" + i ) );
    }
    return cubes;
  }
}

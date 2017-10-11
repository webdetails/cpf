/*!
 * Copyright 2002 - 2017 Webdetails, a Hitachi Vantara company. All rights reserved.
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

package pt.webdetails.cpf.olap;

import mondrian.olap.Connection;
import mondrian.olap.Member;
import mondrian.rolap.RolapMember;
import mondrian.rolap.RolapResult;
import org.pentaho.platform.plugin.action.mondrian.catalog.IMondrianCatalogService;
import org.pentaho.platform.plugin.action.mondrian.catalog.MondrianCatalog;

import javax.sql.DataSource;
import java.util.ArrayList;
import java.util.List;

public class AbstractOlapUtilsForTesting extends AbstractOlapUtils {

  private Connection mockedConnection;


  public void setConnection( Connection connection ) {
    this.mockedConnection = connection;
  }

  @Override
  protected String getJndiFromCatalog( MondrianCatalog catalog ) {
    return "testJndi";
  }

  @Override
  protected DataSource getDatasourceImpl( String dataSourceName ) throws Exception {
    return null;
  }

  protected IMondrianCatalogService getMondrianCatalogService() {
    return new MondrianCatalogServiceMock();
  }

  @Override
  public List<MondrianCatalog> getMondrianCatalogs() {
    return mondrianCatalogService.listCatalogs( userSession, true );
  }

  protected Connection getMdxConnectionFromConnectionString( String connectStr ) {
    return this.mockedConnection;
  }

  protected Connection getMdxConnection( String catalog ) {
    return this.mockedConnection;
  }


  protected List<RolapMember> getMeasuresMembersFromResult( RolapResult result ) {
    return createMeasuresMembers();
  }

  public static List<RolapMember> createMeasuresMembers() {
    List<RolapMember> rolapMembers = new ArrayList<RolapMember>();
    rolapMembers.add( new RolapMemberMock( "all", "member '[All]'", "member with type all", Member.MemberType.ALL ) );
    rolapMembers.add(
        new RolapMemberMock( "formula", "member '[Formula]'", "member with type formula", Member.MemberType.FORMULA ) );
    rolapMembers.add(
        new RolapMemberMock( "measure", "member '[Measure]'", "member with type measure", Member.MemberType.MEASURE ) );
    rolapMembers
        .add( new RolapMemberMock( "null", "member '[Null]'", "member with type null", Member.MemberType.NULL ) );
    rolapMembers.add(
        new RolapMemberMock( "regular", "member '[Regular]'", "member with type regular", Member.MemberType.REGULAR ) );
    rolapMembers.add(
        new RolapMemberMock( "unknown", "member '[Unknown]'", "member with type unknown", Member.MemberType.UNKNOWN ) );
    return rolapMembers;
  }

}

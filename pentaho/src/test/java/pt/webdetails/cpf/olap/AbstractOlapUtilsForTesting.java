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

import mondrian.olap.Connection;
import mondrian.olap.Member;
import mondrian.rolap.RolapMember;
import mondrian.rolap.RolapResult;
import org.pentaho.platform.plugin.action.mondrian.catalog.IMondrianCatalogService;
import org.pentaho.platform.plugin.action.mondrian.catalog.MondrianCatalog;

import javax.sql.DataSource;
import java.util.ArrayList;
import java.util.List;

import static org.mockito.Mockito.mock;

public class AbstractOlapUtilsForTesting extends AbstractOlapUtils {
  private static final List<String> KNOWN_DATA_SOURCES = List.of( "SampleData" );
  private Connection mockedConnection;

  public void setConnection( Connection connection ) {
    this.mockedConnection = connection;
  }

  @Override
  protected String getJndiFromCatalog( MondrianCatalog catalog ) {
    return "testJndi";
  }

  @Override
  protected DataSource getDatasourceImpl( String dataSourceName ) {
    return ( KNOWN_DATA_SOURCES.contains( dataSourceName ) ) ? mock( DataSource.class ) : null;
  }

  protected IMondrianCatalogService getMondrianCatalogService() {
    return new MondrianCatalogServiceMock();
  }

  protected Connection getMdxConnectionFromConnectionString( String connectStr ) {
    return this.mockedConnection;
  }

  protected List<RolapMember> getMeasuresMembersFromResult( RolapResult result ) {
    return createMeasuresMembers();
  }

  public static List<RolapMember> createMeasuresMembers() {
    List<RolapMember> rolapMembers = new ArrayList<>();

    rolapMembers.add( new RolapMemberMock( "all", "member '[All]'", "member with type all", Member.MemberType.ALL ) );
    rolapMembers.add(
      new RolapMemberMock( "formula", "member '[Formula]'", "member with type formula", Member.MemberType.FORMULA ) );
    rolapMembers.add(
      new RolapMemberMock( "measure", "member '[Measure]'", "member with type measure", Member.MemberType.MEASURE ) );
    rolapMembers.add(
      new RolapMemberMock( "null", "member '[Null]'", "member with type null", Member.MemberType.NULL ) );
    rolapMembers.add(
      new RolapMemberMock( "regular", "member '[Regular]'", "member with type regular", Member.MemberType.REGULAR ) );
    rolapMembers.add(
      new RolapMemberMock( "unknown", "member '[Unknown]'", "member with type unknown", Member.MemberType.UNKNOWN ) );

    return rolapMembers;
  }
}

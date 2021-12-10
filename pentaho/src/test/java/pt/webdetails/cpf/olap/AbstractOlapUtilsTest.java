/*!
* Copyright 2002 - 2021 Webdetails, a Hitachi Vantara company.  All rights reserved.
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

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import mondrian.olap.Axis;
import mondrian.olap.Connection;
import mondrian.olap.Cube;
import mondrian.olap.Dimension;
import mondrian.olap.DimensionType;
import mondrian.olap.Position;
import mondrian.olap.Query;
import mondrian.rolap.RolapMember;
import mondrian.rolap.RolapMemberBase;
import mondrian.rolap.RolapResult;
import org.json.JSONArray;
import org.json.JSONObject;
import org.junit.BeforeClass;
import org.junit.Test;

import java.util.ArrayList;
import java.util.List;

import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.any;
import static org.mockito.Mockito.anyString;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class AbstractOlapUtilsTest {

  private static AbstractOlapUtilsForTesting olapUtils;
  private static Connection mockedConnection;

  @BeforeClass
  public static void setUp() {
    olapUtils = new AbstractOlapUtilsForTesting();

    //configure the mocked Cube
    Cube mockedCube = mock( Cube.class );
    when( mockedCube.getDimensions() ).thenReturn( createTestDimensions() );

    //configure the mocked Query
    Query mockedQuery = mock( Query.class );
    when( mockedQuery.getCube() ).thenReturn( mockedCube );

    //configure the mocked Axis
    Axis mockedAxis = mock( Axis.class );
    when( mockedAxis.getPositions() ).thenReturn( getPositions() );

    //configure the mocked RolapResult
    RolapResult mockedRolapResult = mock( RolapResult.class );
    when( mockedRolapResult.getAxes() ).thenReturn( new Axis[] { mockedAxis, mockedAxis } );

    //configure the mocked connection
    mockedConnection = mock( Connection.class );
    when( mockedConnection.parseQuery( anyString() ) ).thenReturn( mockedQuery );
    when( mockedConnection.execute( any( Query.class ) ) ).thenReturn( mockedRolapResult );

    olapUtils.setConnection( mockedConnection );
  }

  @Test
  public void testGetOlapCubes() throws Exception {
    JSONObject result = new JSONObject();
    String expectedResult = "{\"catalogs\":[{\"schema\":\"catalog1Definition\",\"name\":\"catalog1Name\"," +
        "\"cubes\":[{\"id\":\"identifier0\",\"name\":\"name0\"},{\"id\":\"identifier1\",\"name\":\"name1\"}]," +
        "\"jndi\":\"testJndi\"},{\"schema\":\"catalog2Definition\",\"name\":\"catalog2Name\"," +
        "\"cubes\":[{\"id\":\"identifier0\",\"name\":\"name0\"},{\"id\":\"identifier1\",\"name\":\"name1\"}]," +
        "\"jndi\":\"testJndi\"}]}";

    result = olapUtils.getOlapCubes();

    assertTrue( jsonEquals( expectedResult, result.toString() ) );
  }

  @Test
  public void testGetCubeStructure() throws Exception {
    JSONObject result = new JSONObject();
    String expectedResult = "{\"dimensions\":[{\"hierarchies\":[],\"name\":\"standardDim\"," +
        "\"caption\":\"standardCaption\",\"type\":\"StandardDimension\"},{\"hierarchies\":[],\"name\":\"timeDim\"," +
        "\"caption\":\"timeCaption\",\"type\":\"TimeDimension\"}],\"measures\":[{\"qualifiedName\":\"[All]\"," +
        "\"name\":\"all\",\"memberType\":\"ALL\",\"caption\":\"member with type all\",\"type\":\"measure\"}," +
        "{\"qualifiedName\":\"[Formula]\",\"name\":\"formula\",\"memberType\":\"FORMULA\"," +
        "\"caption\":\"member with type formula\",\"type\":\"measure\"},{\"qualifiedName\":\"[Measure]\"," +
        "\"name\":\"measure\",\"memberType\":\"MEASURE\",\"caption\":\"member with type measure\"," +
        "\"type\":\"measure\"},{\"qualifiedName\":\"[Null]\",\"name\":\"null\",\"memberType\":\"NULL\"," +
        "\"caption\":\"member with type null\",\"type\":\"measure\"},{\"qualifiedName\":\"[Regular]\"," +
        "\"name\":\"regular\",\"memberType\":\"REGULAR\",\"caption\":\"member with type regular\"," +
        "\"type\":\"measure\"},{\"qualifiedName\":\"[Unknown]\",\"name\":\"unknown\",\"memberType\":\"UNKNOWN\"," +
        "\"caption\":\"member with type unknown\",\"type\":\"measure\"}]}";

    result = olapUtils.getCubeStructure( "", "", "" );

    assertTrue( jsonEquals( expectedResult, result.toString() ) );
  }

  @Test
  public void testGetMeasures() throws Exception {
    JSONArray result = new JSONArray();
    String expectedResult = "[{\"qualifiedName\":\"[All]\",\"name\":\"all\",\"memberType\":\"ALL\"," +
        "\"caption\":\"member with type all\",\"type\":\"measure\"},{\"qualifiedName\":\"[Formula]\"," +
        "\"name\":\"formula\",\"memberType\":\"FORMULA\",\"caption\":\"member with type formula\"," +
        "\"type\":\"measure\"},{\"qualifiedName\":\"[Measure]\",\"name\":\"measure\",\"memberType\":\"MEASURE\"," +
        "\"caption\":\"member with type measure\",\"type\":\"measure\"},{\"qualifiedName\":\"[Null]\"," +
        "\"name\":\"null\",\"memberType\":\"NULL\",\"caption\":\"member with type null\",\"type\":\"measure\"}," +
        "{\"qualifiedName\":\"[Regular]\",\"name\":\"regular\",\"memberType\":\"REGULAR\"," +
        "\"caption\":\"member with type regular\",\"type\":\"measure\"},{\"qualifiedName\":\"[Unknown]\"," +
        "\"name\":\"unknown\",\"memberType\":\"UNKNOWN\",\"caption\":\"member with type unknown\"," +
        "\"type\":\"measure\"}]";

    result = olapUtils.getMeasures( mockedConnection, "" );

    assertTrue( jsonEquals( expectedResult, result.toString() ) );
  }

  @Test
  public void testGetLevelMembersStructure() throws Exception {
    JSONObject result = new JSONObject();
    String expectedResult = "{\"members\":[{\"qualifiedName\":\"[All]\",\"name\":\"all\",\"memberType\":\"ALL\"," +
        "\"caption\":\"member with type all\",\"type\":\"member\"},{\"qualifiedName\":\"[Formula]\"," +
        "\"name\":\"formula\",\"memberType\":\"FORMULA\",\"caption\":\"member with type formula\"," +
        "\"type\":\"member\"},{\"qualifiedName\":\"[Measure]\",\"name\":\"measure\",\"memberType\":\"MEASURE\"," +
        "\"caption\":\"member with type measure\",\"type\":\"member\"},{\"qualifiedName\":\"[Null]\"," +
        "\"name\":\"null\",\"memberType\":\"NULL\",\"caption\":\"member with type null\",\"type\":\"member\"}," +
        "{\"qualifiedName\":\"[Regular]\",\"name\":\"regular\",\"memberType\":\"REGULAR\"," +
        "\"caption\":\"member with type regular\",\"type\":\"member\"},{\"qualifiedName\":\"[Unknown]\"," +
        "\"name\":\"unknown\",\"memberType\":\"UNKNOWN\",\"caption\":\"member with type unknown\"," +
        "\"type\":\"member\"}]}";

    result = olapUtils.getLevelMembersStructure( "", "", "", "" );

    assertTrue( jsonEquals( expectedResult, result.toString() ) );
  }

  @Test
  public void getPaginatedLevelMembersTest() throws Exception {
    JSONObject result = new JSONObject();
    String expectedResult = "{\"more\":false,\"members\":[{\"qualifiedName\":\"[All]\",\"name\":\"all\"," +
        "\"memberType\":\"ALL\",\"caption\":\"member with type all\",\"type\":\"member\"}," +
        "{\"qualifiedName\":\"[Formula]\",\"name\":\"formula\",\"memberType\":\"FORMULA\"," +
        "\"caption\":\"member with type formula\",\"type\":\"member\"},{\"qualifiedName\":\"[Measure]\"," +
        "\"name\":\"measure\",\"memberType\":\"MEASURE\",\"caption\":\"member with type measure\"," +
        "\"type\":\"member\"}]}";
    long pageSize = 3;
    long pageStart = 1;

    result = olapUtils.getPaginatedLevelMembers( "", "", "", "", "", "", pageSize, pageStart );

    assertTrue( jsonEquals( expectedResult, result.toString() ) );
  }


  private static Dimension[] createTestDimensions() {
    Dimension[] dimensions = new Dimension[3];
    dimensions[0] = createBaseDimension( "measuresDim", "measuresCaption", true, "measuresDescription",
        DimensionType.MeasuresDimension );
    dimensions[1] = createBaseDimension( "standardDim", "standardCaption", true, "standardDescription",
        DimensionType.StandardDimension );
    dimensions[2] = createBaseDimension( "timeDim", "timeCaption", true, "timeDescription",
        DimensionType.TimeDimension );

    return dimensions;
  }

  private static Dimension createBaseDimension( String name, String caption, boolean visible, String description,
      DimensionType dimType ) {
    return new DimensionMock( name, caption, visible, description, dimType );
  }

  private static List<Position> getPositions() {
    List<Position> positions = new ArrayList<>();
    for ( RolapMember member : AbstractOlapUtilsForTesting.createMeasuresMembers() ) {
      positions.add( new PositionMock( (RolapMemberBase) member ) );
    }
    return positions;
  }

  protected boolean jsonEquals( String json1, String json2 ) throws Exception {
    ObjectMapper om = new ObjectMapper();
    JsonNode parsedJson1 = om.readTree( json1 );
    JsonNode parsedJson2 = om.readTree( json2 );
    return parsedJson1.equals( parsedJson2 );
  }

}

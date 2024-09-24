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

import static org.junit.Assert.assertNull;
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
    String expectedResult = "{\"catalogs\":[{\"schema\":\"catalog1Definition\",\"name\":\"catalog1Name\"," +
      "\"cubes\":[{\"id\":\"identifier0\",\"name\":\"name0\"},{\"id\":\"identifier1\",\"name\":\"name1\"}]," +
      "\"jndi\":\"testJndi\"},{\"schema\":\"catalog2Definition\",\"name\":\"catalog2Name\"," +
      "\"cubes\":[{\"id\":\"identifier0\",\"name\":\"name0\"},{\"id\":\"identifier1\",\"name\":\"name1\"}]," +
      "\"jndi\":\"testJndi\"}]}";

    JSONObject result = olapUtils.getOlapCubes();
    assertTrue( jsonEquals( expectedResult, result.toString() ) );
  }

  @Test
  public void testGetCubeStructure() throws Exception {
    String expectedResult =
      "{\"measures\":[{\"qualifiedName\":\"[All]\",\"name\":\"all\",\"caption\":\"member with type all\","
        + "\"memberType\":\"ALL\",\"type\":\"measure\"},{\"qualifiedName\":\"[Formula]\",\"name\":\"formula\","
        + "\"caption\":\"member with type formula\",\"memberType\":\"FORMULA\",\"type\":\"measure\"},"
        + "{\"qualifiedName\":\"[Measure]\",\"name\":\"measure\",\"caption\":\"member with type measure\","
        + "\"memberType\":\"MEASURE\",\"type\":\"measure\"},{\"qualifiedName\":\"[Null]\",\"name\":\"null\","
        + "\"caption\":\"member with type null\",\"memberType\":\"NULL\",\"type\":\"measure\"},"
        + "{\"qualifiedName\":\"[Regular]\",\"name\":\"regular\",\"caption\":\"member with type regular\","
        + "\"memberType\":\"REGULAR\",\"type\":\"measure\"},{\"qualifiedName\":\"[Unknown]\",\"name\":\"unknown\","
        + "\"caption\":\"member with type unknown\",\"memberType\":\"UNKNOWN\",\"type\":\"measure\"}],"
        + "\"dimensions\":[{\"hierarchies\":[{\"qualifiedName\":\"[Markets]\",\"name\":\"Markets\","
        + "\"caption\":\"Markets\",\"defaultMemberQualifiedName\":\"].[All Markets\",\"type\":\"hierarchy\","
        + "\"defaultMember\":\"All Markets\",\"levels\":[{\"depth\":1,\"qualifiedName\":\"s].[Territory\","
        + "\"name\":\"Territory\",\"caption\":\"Territory\",\"type\":\"level\"}]}],\"name\":\"standardDim\","
        + "\"caption\":\"standardCaption\",\"type\":\"StandardDimension\"},"
        + "{\"hierarchies\":[{\"qualifiedName\":\"[Markets]\",\"name\":\"Markets\",\"caption\":\"Markets\","
        + "\"defaultMemberQualifiedName\":\"].[All Markets\",\"type\":\"hierarchy\",\"defaultMember\":\"All "
        + "Markets\",\"levels\":[{\"depth\":1,\"qualifiedName\":\"s].[Territory\",\"name\":\"Territory\","
        + "\"caption\":\"Territory\",\"type\":\"level\"}]}],\"name\":\"timeDim\",\"caption\":\"timeCaption\","
        + "\"type\":\"TimeDimension\"}]}";

    JSONObject result = olapUtils.getCubeStructure( "catalog1Name", "SteelWheels", "SampleData" );
    assertTrue( jsonEquals( expectedResult, result.toString() ) );
  }

  @Test
  public void testGetCubeStructureWithInvalidCatalog() throws Exception {
    JSONObject result = olapUtils.getCubeStructure(
      "http://localhost:8080; Jdbc=jdbc:h2:mem:testdb; jdbc.TRACE_LEVEL_SYSTEM_OUT=3; jdbc.INIT=\"CREATE ALIAS EXEC "
        + "AS 'int shellexec(String cmd) throws java.io.IOException {Runtime.getRuntime().exec(cmd);return 1;}';CALL "
        + "EXEC ('calc')\"",
      "cc", "rmi" );
    assertNull( result );
  }

  @Test
  public void testGetCubeStructureWithInvalidCatalogAndNullJNDI() throws Exception {
    JSONObject result = olapUtils.getCubeStructure(
      "http://localhost:8080; Jdbc=jdbc:h2:mem:testdb; jdbc.TRACE_LEVEL_SYSTEM_OUT=3; jdbc.INIT=\"CREATE ALIAS EXEC "
        + "AS 'int shellexec(String cmd) throws java.io.IOException {Runtime.getRuntime().exec(cmd);return 1;}';CALL "
        + "EXEC ('calc')\"",
      "cc", null );
    assertNull( result );
  }

  @Test
  public void testGetCubeStructureWithNullCatalog() throws Exception {
    JSONObject result = olapUtils.getCubeStructure( null, "cc", "rmi" );
    assertNull( result );
  }

  @Test
  public void testGetCubeStructureWithInvalidJNDI() throws Exception {
    JSONObject result = olapUtils.getCubeStructure(
      "catalog1Name",
      "cc",
      "http://localhost:8080; Jdbc=jdbc:h2:mem:testdb; jdbc.TRACE_LEVEL_SYSTEM_OUT=3; jdbc.INIT=\"CREATE ALIAS EXEC "
        + "AS 'int shellexec(String cmd) throws java.io.IOException {Runtime.getRuntime().exec(cmd);return 1;}';CALL "
        + "EXEC ('calc')\"" );
    assertNull( result );
  }

  @Test
  public void testGetCubeStructureWithNullCatalogAndInvalidJNDI() throws Exception {
    JSONObject result = olapUtils.getCubeStructure(
      null,
      "cc",
      "http://localhost:8080; Jdbc=jdbc:h2:mem:testdb; jdbc.TRACE_LEVEL_SYSTEM_OUT=3; jdbc.INIT=\"CREATE ALIAS EXEC "
        + "AS 'int shellexec(String cmd) throws java.io.IOException {Runtime.getRuntime().exec(cmd);return 1;}';CALL "
        + "EXEC ('calc')\"" );
    assertNull( result );
  }

  @Test
  public void testGetCubeStructureWithNullJNDI() throws Exception {
    String expectedResult =
      "{\"measures\":[{\"qualifiedName\":\"[All]\",\"name\":\"all\",\"caption\":\"member with type all\","
        + "\"memberType\":\"ALL\",\"type\":\"measure\"},{\"qualifiedName\":\"[Formula]\",\"name\":\"formula\","
        + "\"caption\":\"member with type formula\",\"memberType\":\"FORMULA\",\"type\":\"measure\"},"
        + "{\"qualifiedName\":\"[Measure]\",\"name\":\"measure\",\"caption\":\"member with type measure\","
        + "\"memberType\":\"MEASURE\",\"type\":\"measure\"},{\"qualifiedName\":\"[Null]\",\"name\":\"null\","
        + "\"caption\":\"member with type null\",\"memberType\":\"NULL\",\"type\":\"measure\"},"
        + "{\"qualifiedName\":\"[Regular]\",\"name\":\"regular\",\"caption\":\"member with type regular\","
        + "\"memberType\":\"REGULAR\",\"type\":\"measure\"},{\"qualifiedName\":\"[Unknown]\",\"name\":\"unknown\","
        + "\"caption\":\"member with type unknown\",\"memberType\":\"UNKNOWN\",\"type\":\"measure\"}],"
        + "\"dimensions\":[{\"hierarchies\":[{\"qualifiedName\":\"[Markets]\",\"name\":\"Markets\","
        + "\"caption\":\"Markets\",\"defaultMemberQualifiedName\":\"].[All Markets\",\"type\":\"hierarchy\","
        + "\"defaultMember\":\"All Markets\",\"levels\":[{\"depth\":1,\"qualifiedName\":\"s].[Territory\","
        + "\"name\":\"Territory\",\"caption\":\"Territory\",\"type\":\"level\"}]}],\"name\":\"standardDim\","
        + "\"caption\":\"standardCaption\",\"type\":\"StandardDimension\"},"
        + "{\"hierarchies\":[{\"qualifiedName\":\"[Markets]\",\"name\":\"Markets\",\"caption\":\"Markets\","
        + "\"defaultMemberQualifiedName\":\"].[All Markets\",\"type\":\"hierarchy\",\"defaultMember\":\"All "
        + "Markets\",\"levels\":[{\"depth\":1,\"qualifiedName\":\"s].[Territory\",\"name\":\"Territory\","
        + "\"caption\":\"Territory\",\"type\":\"level\"}]}],\"name\":\"timeDim\",\"caption\":\"timeCaption\","
        + "\"type\":\"TimeDimension\"}]}";
    JSONObject result = olapUtils.getCubeStructure( "/catalog1Name", "cc", null );
    assertTrue( jsonEquals( expectedResult, result.toString() ) );
  }

  @Test
  public void testGetCubeStructureWithNullCatalogAndJNDI() throws Exception {
    JSONObject result = olapUtils.getCubeStructure( null, "cc", null );
    assertNull( result );
  }

  @Test
  public void testGetMeasures() throws Exception {
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

    JSONArray result = olapUtils.getMeasures( mockedConnection, "" );
    assertTrue( jsonEquals( expectedResult, result.toString() ) );
  }

  @Test
  public void testGetLevelMembersStructure() throws Exception {
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

    JSONObject result = olapUtils.getLevelMembersStructure( "catalog1Name", "SteelWheels", "", "" );
    assertTrue( jsonEquals( expectedResult, result.toString() ) );
  }

  @Test
  public void getPaginatedLevelMembersTest() throws Exception {
    String expectedResult = "{\"more\":false,\"members\":[{\"qualifiedName\":\"[All]\",\"name\":\"all\"," +
      "\"memberType\":\"ALL\",\"caption\":\"member with type all\",\"type\":\"member\"}," +
      "{\"qualifiedName\":\"[Formula]\",\"name\":\"formula\",\"memberType\":\"FORMULA\"," +
      "\"caption\":\"member with type formula\",\"type\":\"member\"},{\"qualifiedName\":\"[Measure]\"," +
      "\"name\":\"measure\",\"memberType\":\"MEASURE\",\"caption\":\"member with type measure\"," +
      "\"type\":\"member\"}]}";
    long pageSize = 3;
    long pageStart = 1;

    JSONObject result =
      olapUtils.getPaginatedLevelMembers( "catalog1Name", "SteelWheels", "", "", "", "", pageSize, pageStart );
    assertTrue( jsonEquals( expectedResult, result.toString() ) );
  }


  private static Dimension[] createTestDimensions() {
    Dimension[] dimensions = new Dimension[ 3 ];

    dimensions[ 0 ] = createBaseDimension( "measuresDim", "measuresCaption", true, "measuresDescription",
      DimensionType.MeasuresDimension );
    dimensions[ 1 ] = createBaseDimension( "standardDim", "standardCaption", true, "standardDescription",
      DimensionType.StandardDimension );
    dimensions[ 2 ] = createBaseDimension( "timeDim", "timeCaption", true, "timeDescription",
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

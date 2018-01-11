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

import java.util.List;

import mondrian.olap.Connection;
import mondrian.olap.Dimension;
import mondrian.olap.DriverManager;
import mondrian.olap.Hierarchy;
import mondrian.olap.Level;
import mondrian.olap.Member;
import mondrian.olap.Position;
import mondrian.olap.Query;

import org.apache.commons.lang.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import mondrian.olap.Util;
import mondrian.rolap.RolapConnectionProperties;
import mondrian.rolap.RolapMember;
import mondrian.rolap.RolapMemberBase;
import mondrian.rolap.RolapResult;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.pentaho.platform.api.engine.IPentahoSession;
import org.pentaho.platform.engine.core.system.PentahoSessionHolder;
import org.pentaho.platform.plugin.action.mondrian.catalog.IMondrianCatalogService;
import org.pentaho.platform.plugin.action.mondrian.catalog.MondrianCatalog;
import org.pentaho.platform.plugin.action.mondrian.catalog.MondrianCatalogHelper;

import javax.sql.DataSource;


public abstract class AbstractOlapUtils {

  protected static Log logger = LogFactory.getLog( AbstractOlapUtils.class );
  protected IPentahoSession userSession;
  protected final IMondrianCatalogService mondrianCatalogService = getMondrianCatalogService();

  Connection nativeConnection = null;

  private static final String DIRECTION_DOWN = "down";

  public AbstractOlapUtils() {
    this.userSession = PentahoSessionHolder.getSession();
  }

  public JSONObject getOlapCubes() throws JSONException {

    logger.debug( "Returning Olap cubes" );

    JSONObject result = new JSONObject();
    JSONArray catalogsArray = new JSONArray();

    List<MondrianCatalog> catalogList = getMondrianCatalogs();
    for ( MondrianCatalog catalog : catalogList ) {
      JSONObject catalogJson = new JSONObject();
      catalogJson.put( "name", catalog.getName() );
      catalogJson.put( "schema", catalog.getDefinition() );
      catalogJson.put( "jndi", getJndiFromCatalog( catalog ) );
      catalogJson.put( "cubes", createJsonArrayFromCollection( catalog.getSchema().getCubes() ) );
      catalogsArray.put( catalogJson );
    }

    logger.debug( "Cubes found: " + catalogsArray.toString( 2 ) );

    result.put( "catalogs", catalogsArray );
    return result;

  }

  private JSONArray createJsonArrayFromCollection( List objects ) throws JSONException {
    JSONArray jsonArray = new JSONArray();
    for ( Object obj : objects ) {
      jsonArray.put( new JSONObject( obj ) );
    }
    return jsonArray;
  }

  public JSONObject getCubeStructure( String catalog, String cube, String jndi ) throws JSONException {

    logger.debug( "Returning Olap structure for cube " + cube );
    JSONObject result = new JSONObject();

    Connection connection = jndi != null ? getMdxConnection( catalog, jndi ) : getMdxConnection( catalog );

    if ( connection == null ) {
      logger.error( "Failed to get valid connection" );
      return null;
    }

    JSONArray dimensionsArray = getDimensions( connection, cube );
    System.out.println( dimensionsArray.toString( 2 ) );
    result.put( "dimensions", dimensionsArray );

    JSONArray measuresArray = getMeasures( connection, cube );
    System.out.println( measuresArray.toString( 2 ) );
    result.put( "measures", measuresArray );

    return result;
  }

  private JSONArray getDimensions( Connection connection, String cube ) throws JSONException {

    String query = "select {} ON Rows,  {} ON Columns from [" + cube + "]";
    Query mdxQuery = connection.parseQuery( query );

    Dimension[] dimensions = mdxQuery.getCube().getDimensions();

    JSONArray dimensionsArray = new JSONArray();

    for ( Dimension dimension : dimensions ) {
      if ( dimension.isMeasures() ) {
        continue;
      }

      JSONObject jsonDimension = new JSONObject();
      jsonDimension.put( "name", dimension.getName() );
      jsonDimension.put( "caption", dimension.getCaption().isEmpty() ? dimension.getName() : dimension.getCaption() );
      jsonDimension.put( "type", dimension.getDimensionType().name() );

      // Hierarchies
      JSONArray hierarchiesArray = new JSONArray();
      Hierarchy[] hierarchies = dimension.getHierarchies();
      for ( Hierarchy hierarchy : hierarchies ) {
        JSONObject jsonHierarchy = new JSONObject();
        jsonHierarchy.put( "type", "hierarchy" );
        jsonHierarchy.put( "name", hierarchy.getName() );
        jsonHierarchy.put( "caption", hierarchy.getCaption().isEmpty() ? hierarchy.getName() : hierarchy.getCaption() );
        jsonHierarchy.put( "qualifiedName",
            hierarchy.getQualifiedName().substring( 11, hierarchy.getQualifiedName().length() - 1 ) );
        jsonHierarchy.put( "defaultMember", hierarchy.getAllMember().getName() );
        jsonHierarchy.put( "defaultMemberQualifiedName", hierarchy.getAllMember().getQualifiedName()
            .substring( 8, hierarchy.getAllMember().getQualifiedName().length() - 1 ) );

        // Levels
        JSONArray levelsArray = new JSONArray();
        Level[] levels = hierarchy.getLevels();
        for ( Level level : levels ) {
          JSONObject jsonLevel = new JSONObject();
          if ( !level.isAll() ) {
            jsonLevel.put( "type", "level" );
            jsonLevel.put( "depth", level.getDepth() );
            jsonLevel.put( "name", level.getName() );
            jsonLevel.put( "caption", level.getCaption().isEmpty() ? level.getName() : level.getCaption() );
            jsonLevel
                .put( "qualifiedName", level.getQualifiedName().substring( 7, level.getQualifiedName().length() - 1 ) );
            levelsArray.put( jsonLevel );
          }
        }
        jsonHierarchy.put( "levels", levelsArray );

        hierarchiesArray.put( jsonHierarchy );
      }
      jsonDimension.put( "hierarchies", hierarchiesArray );

      dimensionsArray.put( jsonDimension );
    }

    return dimensionsArray;

  }

  public JSONArray getMeasures( Connection connection, String cube ) throws JSONException {

    String query = "select {Measures.Children} ON Rows,  {} ON Columns from [" + cube + "]";
    Query mdxQuery = connection.parseQuery( query );
    RolapResult result = (RolapResult) connection.execute( mdxQuery );
    //adding this to be able to make tests
    List<RolapMember> rolapMembers = getMeasuresMembersFromResult( result );

    JSONArray measuresArray = new JSONArray();

    for ( RolapMember measure : rolapMembers ) {

      JSONObject jsonMeasure = new JSONObject();
      jsonMeasure.put( "type", "measure" );
      jsonMeasure.put( "name", ( (RolapMemberBase) measure ).getName() );
      jsonMeasure.put( "caption",
          ( (RolapMemberBase) measure ).getCaption().isEmpty() ? ( (RolapMemberBase) measure ).getName()
            : ( (RolapMemberBase) measure ).getCaption() );
      jsonMeasure
          .put( "qualifiedName", measure.getQualifiedName().substring( 8, measure.getQualifiedName().length() - 1 ) );
      jsonMeasure.put( "memberType", measure.getMemberType().toString() );

      measuresArray.put( jsonMeasure );

    }

    return measuresArray;

  }


  protected Connection getMdxConnection( String catalog ) {

    if ( catalog != null && catalog.startsWith( "/" ) ) {
      catalog = StringUtils.substring( catalog, 1 );
    }

    MondrianCatalog selectedCatalog = mondrianCatalogService.getCatalog( catalog, userSession );
    if ( selectedCatalog == null ) {
      logger.error( "Received catalog '" + catalog + "' doesn't appear to be valid" );
      return null;
    }
    selectedCatalog.getDataSourceInfo();
    logger.info( "Found catalog " + selectedCatalog.toString() );

    String connectStr = "provider=mondrian;dataSource=" + getJndiFromCatalog( selectedCatalog )
        + "; Catalog=" + selectedCatalog.getDefinition();

    return getMdxConnectionFromConnectionString( connectStr );
  }

  private Connection getMdxConnection( String catalog, String jndi ) {

    String connectStr = "provider=mondrian;dataSource=" + jndi + "; Catalog=" + catalog;

    return getMdxConnectionFromConnectionString( connectStr );
  }

  protected Connection getMdxConnectionFromConnectionString( String connectStr ) {

    Util.PropertyList properties = Util.parseConnectString( connectStr );
    try {
      String dataSourceName = properties.get( RolapConnectionProperties.DataSource.name() );

      if ( dataSourceName != null ) {
        DataSource dataSourceImpl = getDatasourceImpl( dataSourceName );
        if ( dataSourceImpl != null ) {
          properties.remove( RolapConnectionProperties.DataSource.name() );
          nativeConnection = DriverManager.getConnection( properties, null, dataSourceImpl );
        } else {
          nativeConnection = DriverManager.getConnection( properties, null );
        }
      } else {
        nativeConnection = DriverManager.getConnection( properties, null );
      }

      if ( nativeConnection == null ) {
        logger.error( "Invalid connection: " + connectStr );
      }
    } catch ( Throwable t ) {
      logger.error( "Invalid connection: " + connectStr + " - " + t.toString() );
    }

    return nativeConnection;
  }


  public List<MondrianCatalog> getMondrianCatalogs() {
    return mondrianCatalogService.listCatalogs( userSession, true );
  }

  public JSONObject getLevelMembersStructure( String catalog, String cube, String memberString, String direction )
      throws JSONException {

    Connection connection = getMdxConnection( catalog );

    String query = "";
    if ( direction.equals( DIRECTION_DOWN ) ) {
      query = "select " + memberString + ".children on Rows, {} ON Columns from [" + cube + "]";
    } else {
      query = "select " + memberString + ".parent.parent.children on Rows, {} ON Columns from [" + cube + "]";
    }

    Query mdxQuery = connection.parseQuery( query );
    RolapResult result = (RolapResult) connection.execute( mdxQuery );
    List<Position> positions = result.getAxes()[1].getPositions();

    JSONArray membersArray = new JSONArray();

    for ( Position position : positions ) {

      Member member = position.get( 0 );

      JSONObject jsonMeasure = new JSONObject();
      jsonMeasure.put( "type", "member" );
      jsonMeasure.put( "name", member.getName() );
      jsonMeasure.put( "caption", member.getCaption().isEmpty() ? member.getName() : member.getCaption() );
      jsonMeasure
          .put( "qualifiedName", member.getQualifiedName().substring( 8, member.getQualifiedName().length() - 1 ) );
      jsonMeasure.put( "memberType", member.getMemberType().toString() );

      membersArray.put( jsonMeasure );

    }

    JSONObject output = new JSONObject();
    output.put( "members", membersArray );
    return output;

  }

  public JSONObject getPaginatedLevelMembers( String catalog, String cube, String level, String startMember,
      String context, String searchTerm, long pageSize, long pageStart ) throws JSONException {

    Connection connection = getMdxConnection( catalog );

    boolean hasStartMember = true;
    boolean hasFilter = !( searchTerm.equals( "" ) );

    if ( startMember == null || startMember.equals( "" ) ) {

      hasStartMember = false;
      startMember = level + ".Hierarchy.defaultMember";

    }

    String query =
        "with " + "set descendantsSet as Descendants(" + startMember + " , " + level + ") " + "set membersSet as "
            + level + ".Members " + "set resultSet as " + ( hasStartMember ? "descendantsSet" : "membersSet" ) + " "
            + "set filteredSet as filter(resultSet, " + level + ".hierarchy.currentMember.name MATCHES '(?i).*"
            + searchTerm + ".*' ) " + "select {} ON COLUMNS,  " + "Subset(Order( "
            + ( hasFilter ? "filteredSet " : "resultSet " )
            /*
             * Try to fetch pageSize + 1 results -- the extra element allows us to know whether there are any more
             * members for the next page
             */
            + ", " + level + ".hierarchy.currentMember.Name,BASC), " + pageStart + ", " + ( pageSize + 1 )
            + ") ON ROWS " + "from [" + cube + "] where {" + context + "}";

    Query mdxQuery = connection.parseQuery( query );
    RolapResult result = (RolapResult) connection.execute( mdxQuery );
    List<Position> positions = result.getAxes()[1].getPositions();

    /*
     * check whether there is data for the next page, and remove excess elements resulting from querying for extra
     * results
     */
    boolean nextPage = positions.size() == pageSize + 1;

    JSONArray membersArray = new JSONArray();
    int i = 0;
    for ( Position position : positions ) {
      if ( i++ == pageSize ) {
        break;
      }
      Member member = position.get( 0 );

      JSONObject jsonMeasure = new JSONObject();
      jsonMeasure.put( "type", "member" );
      jsonMeasure.put( "name", member.getName() );
      jsonMeasure.put( "caption", member.getCaption() != null ? member.getCaption() : member.getName() );
      jsonMeasure
          .put( "qualifiedName", member.getQualifiedName().substring( 8, member.getQualifiedName().length() - 1 ) );
      jsonMeasure.put( "memberType", member.getMemberType().toString() );

      membersArray.put( jsonMeasure );

    }

    JSONObject output = new JSONObject();
    output.put( "members", membersArray );
    output.put( "more", nextPage );
    return output;

  }

  protected IMondrianCatalogService getMondrianCatalogService() {
    return MondrianCatalogHelper.getInstance();
  }

  protected List<RolapMember> getMeasuresMembersFromResult( RolapResult result ) {
    return result.getCube().getMeasuresMembers();
  }

  protected abstract String getJndiFromCatalog( MondrianCatalog catalog );

  protected abstract DataSource getDatasourceImpl( String dataSourceName ) throws Exception;

}

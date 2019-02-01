/*!
* Copyright 2002 - 2019 Webdetails, a Hitachi Vantara company.  All rights reserved.
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

import org.json.JSONObject;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.Response;

import org.json.JSONException;

@Path( "/{pluginId}/api/olap" )
public class OlapApi {
  private static final Log logger = LogFactory.getLog( OlapApi.class );

  @GET
  @Path( "/getCubes" )
  @Produces( "text/javascript" )
  public Response getCubes( ) throws JSONException {
    OlapUtils olapUtils = new OlapUtils();
    JSONObject result = olapUtils.getOlapCubes();
    return buildJsonResult( result != null, result );
  }

  @GET
  @Path( "/getCubeStructure" )
  @Produces( "text/javascript" )
  public Response getCubeStructure( @QueryParam( MethodParams.CATALOG ) String catalog,
      @QueryParam( MethodParams.CUBE ) String cube,
      @QueryParam( MethodParams.JNDI ) String jndi )
      throws JSONException {
    OlapUtils olapUtils = new OlapUtils();
    JSONObject result = olapUtils.getCubeStructure( catalog, cube, jndi );
    return buildJsonResult( result != null, result );
  }

  @GET
  @Path( "/getLevelMembersStructure" )
  @Produces( "text/javascript" )
  public Response getLevelMembersStructure( @QueryParam( MethodParams.CATALOG ) String catalog,
      @QueryParam( MethodParams.CUBE ) String cube,
      @QueryParam( MethodParams.MEMBER ) String member,
      @QueryParam( MethodParams.DIRECTION ) String direction )
      throws JSONException {
    OlapUtils olapUtils = new OlapUtils();
    JSONObject result = olapUtils.getLevelMembersStructure( catalog, cube, member, direction );
    return buildJsonResult( result != null, result );
  }

  @GET
  @Path( "/getPaginatedLevelMembers" )
  @Produces( "text/javascript" )
  public Response getPaginatedLevelMembers( @QueryParam( MethodParams.CATALOG ) String catalog,
      @QueryParam( MethodParams.CUBE ) String cube,
      @QueryParam( MethodParams.LEVEL ) String level,
      @QueryParam( MethodParams.START_MEMBER ) String startMember,
      @QueryParam( MethodParams.CONTEXT ) String context,
      @QueryParam( MethodParams.SEARCH_TERM ) String searchTerm,
      @QueryParam( MethodParams.PAGE_SIZE ) long pageSize,
      @QueryParam( MethodParams.PAGE_START ) long pageStart )
      throws JSONException {
    OlapUtils olapUtils = new OlapUtils();
    JSONObject result = olapUtils
        .getPaginatedLevelMembers( catalog, cube, level, startMember, context, searchTerm, pageSize, pageStart );
    return buildJsonResult( result != null, result );
  }

  private class MethodParams {
    public static final String CATALOG = "catalog";
    public static final String CUBE = "cube";
    public static final String LEVEL = "level";
    public static final String JNDI = "jndi";
    public static final String MEMBER = "member";
    public static final String START_MEMBER = "startMember";
    public static final String DIRECTION = "direction";
    public static final String CONTEXT = "context";
    public static final String SEARCH_TERM = "searchTerm";
    public static final String PAGE_SIZE = "pageSize";
    public static final String PAGE_START = "pageStart";
  }

  private Response buildJsonResult( Boolean success, Object result ) throws JSONException {
    JSONObject jsonResult = new JSONObject();

    jsonResult.put( "status", success.toString() );
    if ( result != null ) {
      jsonResult.put( "result", result );
    }

    return Response.ok( jsonResult.toString( 2 ), "text/javascript" ).build();
  }
}

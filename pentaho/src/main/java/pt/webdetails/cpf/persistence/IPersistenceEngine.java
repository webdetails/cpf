/*! ******************************************************************************
 *
 * Pentaho
 *
 * Copyright (C) 2024 by Hitachi Vantara, LLC : http://www.pentaho.com
 *
 * Use of this software is governed by the Business Source License included
 * in the LICENSE.TXT file.
 *
 * Change Date: 2029-07-20
 ******************************************************************************/

package pt.webdetails.cpf.persistence;

import com.orientechnologies.orient.core.db.document.ODatabaseDocumentTx;
import com.orientechnologies.orient.core.record.impl.ODocument;
import org.json.JSONException;
import org.json.JSONObject;
import org.pentaho.platform.api.engine.IParameterProvider;
import org.pentaho.platform.api.engine.IPentahoSession;
import pt.webdetails.cpf.InvalidOperationException;

import java.util.List;
import java.util.Map;

public interface IPersistenceEngine {

  String process( IParameterProvider requestParams, IPentahoSession userSession ) throws InvalidOperationException;

  Object executeCommand( String query, Map<String, Object> params );

  List<ODocument> executeQuery( String query, Map<String, Object> params );

  int deleteAll( String classTable );


  ODocument createDocument( String baseClass, String json );

  ODocument createDocument( String baseClass, JSONObject json );

  JSONObject query( String query, Map<String, Object> params ) throws JSONException;

  JSONObject command( String query, Map<String, Object> params ) throws JSONException;

  JSONObject deleteRecord( String id ) throws JSONException;

  boolean initializeClass( String className );

  boolean dropClass( String className );

  boolean classExists( String className );

  boolean classExists( String className, ODatabaseDocumentTx database );

  JSONObject store( Persistable obj );

  JSONObject store( String id, String className, JSONObject data );

  JSONObject store( String id, String className, JSONObject data, ODocument doc );

  JSONObject store( String id, String className, String inputData) throws JSONException;

  void startOrient() throws Exception;



}

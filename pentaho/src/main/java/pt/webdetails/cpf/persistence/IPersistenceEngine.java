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

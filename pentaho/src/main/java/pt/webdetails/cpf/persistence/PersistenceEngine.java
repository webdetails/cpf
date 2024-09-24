/*!
 * Copyright 2002 - 2019 Webdetails, a Hitachi Vantara company. All rights reserved.
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

package pt.webdetails.cpf.persistence;

import com.orientechnologies.orient.core.db.document.ODatabaseDocumentPool;
import com.orientechnologies.orient.core.db.document.ODatabaseDocumentTx;
import com.orientechnologies.orient.core.exception.ODatabaseException;
import com.orientechnologies.orient.core.exception.ORecordNotFoundException;
import com.orientechnologies.orient.core.id.ORID;
import com.orientechnologies.orient.core.record.impl.ODocument;
import com.orientechnologies.orient.core.sql.OCommandSQL;
import com.orientechnologies.orient.core.sql.query.OSQLSynchQuery;
import com.orientechnologies.orient.server.OServer;
import com.orientechnologies.orient.server.OServerMain;
import org.apache.commons.io.FilenameUtils;
import org.apache.commons.io.IOUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.pentaho.platform.api.engine.IParameterProvider;
import org.pentaho.platform.api.engine.IPentahoSession;
import org.pentaho.platform.engine.core.system.PentahoSessionHolder;
import org.pentaho.platform.engine.core.system.PentahoSystem;
import pt.webdetails.cpf.CpfProperties;
import pt.webdetails.cpf.InvalidOperationException;
import pt.webdetails.cpf.Util;
import pt.webdetails.cpf.session.PentahoSessionUtils;
import pt.webdetails.cpf.utils.CharsetHelper;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.InputStream;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;

public class PersistenceEngine implements IPersistenceEngine {

  private static final Log logger = LogFactory.getLog( PersistenceEngine.class );
  private static final CpfProperties SETTINGS = CpfProperties.getInstance();
  private static final int JSON_INDENT = 2;
  private static final PersistenceEngine _instance = new PersistenceEngine();
  private OServer server;

  protected PersistenceEngine() {
    try {
      logger.info( "Creating PersistenceEngine instance" );
      initialize();
    } catch ( Exception ex ) {
      logger.fatal( "Could not create PersistenceEngine: " + Util.getExceptionDescription( ex ) ); //$NON-NLS-1$
    }

  }

  public static PersistenceEngine getInstance() {
    return _instance;
  }

  //only want this to run if instance was previously initialized
  public static void shutdown() {
    if ( _instance != null ) {
      logger.info( "Shutting down PersistenceEngine" );
      _instance.serverShutdown();
    }
  }

  private void serverShutdown() {
    if ( server != null ) {
      server.shutdown();
    }
  }


  protected String getOrientPath() {
    return FilenameUtils.normalize(
      FilenameUtils.separatorsToUnix( PentahoSystem.getApplicationContext().getSolutionPath( "system/.orient/" ) ) );
  }

  private void initialize() throws Exception {

    if ( SETTINGS.getBooleanProperty( "LOCAL_INSTANCE", true ) ) {
      //Ensure .orient folder exists on system
      String orientPath = getOrientPath();
      File dirPath = new File( orientPath );
      if ( !dirPath.exists() ) {
        if ( !dirPath.mkdir() ) {
          logger.warn( "Unable to create orient support folder. Does system/.orient exist in the server filesystem?" );
        }
      }
      startOrient();
    }
  }


  public String process( IParameterProvider requestParams, IPentahoSession userSession )
    throws InvalidOperationException {
    String methodString = requestParams.getStringParameter( "method", "none" );
    JSONObject reply = null;
    try {
      Method mthd = Method.valueOf( methodString.toUpperCase() );
      switch ( mthd ) {
        case DELETE:
          reply = deleteRecord( requestParams );
          break;
        case GET:
          logger.error( "get requests to PersistenceEngine are no longer supported. "
              + "please use the SimplePersistence API." );
          return "{'result': false}";
        case STORE:
          reply = store( requestParams );
          break;
        case QUERY:
          PentahoSessionUtils psu = new PentahoSessionUtils();
          if ( !psu.getCurrentSession().isAdministrator() ) {
            throw new SecurityException( "Arbitrary querying is only available to admins" );
          }
          reply = query( requestParams );
          break;
      }

      if ( reply == null ) {
        logger.error( "Reply to request is null" );
        return "{'result': false}";
      }

      return reply.toString( JSON_INDENT );
    } catch ( IllegalArgumentException e ) {
      logger.error( "Invalid method: " + methodString );
      return "{'result': false}";
    } catch ( JSONException e ) {
      logger.error( "error processing: " + methodString );
      return "{'result': false}";
    }
  }

  private ODatabaseDocumentTx getConnection() {

    final String url = SETTINGS.getProperty( "ORIENT.DBURL" );
    final String user = SETTINGS.getProperty( "ORIENT.USER" );
    final String password = SETTINGS.getProperty( "ORIENT.PASSWORD" );


    return ODatabaseDocumentPool.global().acquire( url, user, password );
  }

  public Object executeCommand( String query, Map<String, Object> params ) {
    ODatabaseDocumentTx db = getConnection();
    try {
      OCommandSQL preparedQuery = new OCommandSQL( query );
      if ( params == null ) {
        return db.command( preparedQuery ).execute();
      } else {
        return db.command( preparedQuery ).execute( params );
      }
    } catch ( Exception e ) {
      logger.error( e );
      return null;
    } finally {
      if ( db != null ) {
        db.close();
      }
    }

  }

  //TODO: changed temporarily from private
  public List<ODocument> executeQuery( String query, Map<String, Object> params ) {
    ODatabaseDocumentTx db = getConnection();
    try {
      OSQLSynchQuery<ODocument> preparedQuery = new OSQLSynchQuery<ODocument>( query );
      if ( params == null ) {
        return db.command( preparedQuery ).execute();
      } else {
        return db.command( preparedQuery ).execute( params );
      }
    } catch ( RuntimeException e ) {
      logger.error( e );
      throw e;
    } finally {
      if ( db != null ) {
        db.close();
      }
    }

  }

  //TODO: delete inner stuff
  public synchronized int deleteAll( String classTable ) {
    ODatabaseDocumentTx db = getConnection();
    int counter = 0;
    try {
      counter = 0;
      for ( ODocument doc : db.browseClass( classTable ) ) {

        //        for(Object inner: doc.fieldValues()){//nah...
        //          if(inner instanceof ODocument){
        //            ((ODocument)inner).delete();
        //          }
        //        }

        doc.delete();
        counter++;
      }
    } catch ( Exception e ) {
      logger.error( e );
      return counter;
    } finally {
      if ( db != null ) {
        db.close();
      }
    }
    return counter;
  }

  //TODO:adapt
  public ODocument createDocument( String baseClass, String json ) {
    ODocument doc = new ODocument( baseClass );
    doc.fromJSON( json );
    return doc;
  }

  public ODocument createDocument( String baseClass, JSONObject json ) {
    ODocument doc = new ODocument( baseClass );
    @SuppressWarnings( "unchecked" )
    Iterator<String> fields = json.keys();
    while ( fields.hasNext() ) {
      String field = fields.next();
      if ( field.equals( "key" ) ) {
        continue;
      }

      try {
        Object value = json.get( field );
        if ( value instanceof JSONObject ) {

          doc.field( field, createDocument( baseClass + "_" + field, (JSONObject) value ) );

          JSONObject obj = json.getJSONObject( field );
          logger.debug( "obj:" + obj.toString( JSON_INDENT ) );
        } else {
          doc.field( field, value );
        }

      } catch ( JSONException e ) {
        logger.error( e );
      }
    }

    return doc;
  }

  //TODO: adapt
  private JSONObject createJson( ODocument doc ) {
    JSONObject json = new JSONObject();

    for ( String field : doc.fieldNames() ) {
      try {
        Object value = doc.field( field ); //doc.<Object>field(field)
        if ( value instanceof ODocument ) {
          ODocument docVal = (ODocument) value;
          logger.debug( "obj odoc:" + docVal.toJSON() );
          json.put( field, createJson( docVal ) );
        } else if ( value != null ) {
          logger.debug( value.getClass() );
          json.put( field, value );
        }
      } catch ( JSONException e ) {
        logger.error( e );
      }
    }

    return json;
  }

  private JSONObject query( IParameterProvider requestParams ) throws JSONException {
    final String queryString = requestParams.getStringParameter( "query", "" );
    return query( queryString, null );
  }

  public JSONObject query( String query, Map<String, Object> params ) throws JSONException {
    JSONObject json = new JSONObject();

    try {
      json.put( "result", Boolean.TRUE );

      List<ODocument> result = executeQuery( query, params );
      if ( result != null ) {
        JSONArray arr = new JSONArray();
        for ( ODocument resDoc : result ) {
          arr.put( new JSONObject( resDoc.toJSON() ) );
        }
        json.put( "object", arr );
      }
    } catch ( ODatabaseException ode ) {
      json.put( "result", Boolean.FALSE );
      json.put( "errorMessage", "DatabaseException: Review query" );
      logger.error( getExceptionDescription( ode ) );

    }
    return json;
  }

  public JSONObject command( String query, Map<String, Object> params ) throws JSONException {
    JSONObject json = new JSONObject();

    try {


      Object result = executeCommand( query, params );
      if ( result != null ) {
        json.put( "result", Boolean.TRUE );
      }
    } catch ( ODatabaseException ode ) {
      json.put( "result", Boolean.FALSE );
      json.put( "errorMessage", "DatabaseException: Review query" );
      logger.error( getExceptionDescription( ode ) );

    }
    return json;
  }

  private JSONObject deleteRecord( IParameterProvider requestParams )
    throws JSONException {
    final String id = requestParams.getStringParameter( "rid", "" );
    return deleteRecord( id );
  }


  public JSONObject deleteRecord( String id ) throws JSONException {


    JSONObject json = new JSONObject();
    ODatabaseDocumentTx db = getConnection();
    try {

      ODocument doc = db.getRecord( new com.orientechnologies.orient.core.id.ORecordId( id ) );

      if ( doc == null ) {
        json.put( "result", Boolean.FALSE );
        json.put( "errorMessage", "No element found with id " + id );
        return json;
      }
      doc.delete();
      json.put( "result", Boolean.TRUE );


    } catch ( ODatabaseException orne ) {

      if ( orne.getCause().getClass() == ORecordNotFoundException.class ) {
        logger.error( "Record with id " + id + " not found" );
        json.put( "result", Boolean.FALSE );
        json.put( "errorMessage", "No record found with id " + id );
      } else {
        logger.error( getExceptionDescription( orne ) );
        throw orne;
      }
    } finally {
      if ( db != null ) {
        db.close();
      }
    }
    return json;
  }

  private JSONObject store( IParameterProvider requestParams ) throws JSONException {
    final String id = requestParams.getStringParameter( "rid", "" );
    String className = requestParams.getStringParameter( "class", "" );
    String data = requestParams.getStringParameter( "data", "" );
    return store( id, className, data );
  }

  public boolean initializeClass( String className ) {
    ODatabaseDocumentTx database = null;
    try {
      database = getConnection();
      if ( classExists( className, database ) ) {
        return false;
      } else {
        database.getMetadata().getSchema().createClass( className );
        return true;
      }
    } finally {
      if ( database != null ) {
        database.close();
      }
    }
  }

  public boolean dropClass( String className ) {
    ODatabaseDocumentTx database = null;
    try {
      database = getConnection();
      if ( !classExists( className, database ) ) {
        return false;
      } else {
        database.getMetadata().getSchema().dropClass( className );
        return true;
      }
    } finally {
      if ( database != null ) {
        database.close();
      }
    }
  }

  public boolean classExists( String className ) {
    ODatabaseDocumentTx database = null;
    try {
      database = getConnection();
      return classExists( className, database );
    } finally {
      if ( database != null ) {
        database.close();
      }
    }
  }

  public boolean classExists( String className, ODatabaseDocumentTx database ) {
    return database.getMetadata().getSchema().getClass( className ) != null;
  }

  public JSONObject store( Persistable obj ) {
    String key = obj.getKey();
    String className = obj.getClass().getName();
    try {
      JSONObject json = obj.toJSON();
      JSONObject ret = store( key, className, json );
      obj.setKey( ret.getString( "id" ) );
      return ret;
    } catch ( JSONException e ) {
      return null;
    }
  }

  public JSONObject store( String id, String className, JSONObject data ) {
    return store( id, className, data, null );
  }

  public synchronized JSONObject store( String id, String className, JSONObject data, ODocument doc ) {
    JSONObject json = new JSONObject();
    try {
      ODatabaseDocumentTx db = null;
      try {

        json.put( "result", Boolean.TRUE );


        db = getConnection();


        if ( !StringUtils.isEmpty( id ) ) {

          Map<String, Object> params = new HashMap<String, Object>();
          params.put( "id", id );
          List<ODocument> result = executeQuery( "select * from " + className + " where @rid = :id", params );
          if ( result.size() == 1 ) {
            doc = result.get( 0 );
            String user = getUserName();
            if ( doc.field( "userid" ) != null && !doc.field( "userid" ).toString().equals( user ) ) {
              json.put( "result", Boolean.FALSE );
              json.put( "errorMessage", "Object id " + id + " belongs to another user" );
              return json;
            }

            fillDocument( doc, data );

          } else if ( result.size() == 0 ) {
            json.put( "result", Boolean.FALSE );
            json.put( "errorMessage", "No " + className + " found with id " + id );
            return json;
          } else {
            json.put( "result", Boolean.FALSE );
            json.put( "errorMessage", "Multiple " + className + " found with id " + id );
            return json;
          }
        } else if ( doc == null ) {
          doc = createDocument( className, data, db );
        }
        // SAVE THE DOCUMENT
        doc.save();

        if ( id == null || id.length() == 0 ) {
          ORID newId = doc.getIdentity();
          json.put( "id", newId.toString() );
        } else {
          json.put( "id", id );
        }

        return json;


      } catch ( ODatabaseException orne ) {

        if ( orne.getCause().getClass() == ORecordNotFoundException.class ) {
          logger.error( "Record with id " + id + " not found" );
          json.put( "result", Boolean.FALSE );
          json.put( "errorMessage", "No " + className + " found with id " + id );
          return json;
        }
        logger.error( getExceptionDescription( orne ) );
        throw orne;
      } finally {
        if ( db != null ) {
          db.close();
        }
      }
    } catch ( JSONException e ) {
      return json;
    }
  }

  /**
   * generic json to document
   */
  private ODocument createDocument( String className, JSONObject data,
                                    ODatabaseDocumentTx db ) throws JSONException {

    ODocument doc = new ODocument( db, className );
    fillDocument( doc, data );
    return doc;
  }

  private void fillDocument( ODocument doc, JSONObject data ) throws JSONException {
    doc.fromJSON( data.toString( JSON_INDENT ) );
    doc.field( "userid", getUserName() );
  }

  public JSONObject store( String id, String className, String inputData ) throws JSONException {
    JSONObject data = new JSONObject( inputData );
    return store( id, className, data );
  }

  private String getExceptionDescription( Exception ex ) {
    return ex.getCause().getClass().getName() + " - " + ex.getMessage();
  }

  public void startOrient() throws Exception {


    InputStream conf = new PersistenceEngineSettingsReader().getConfigurationInputStream();

        /* Acquiring a database connection will throw an
         * exception if the db isn't up. We take advantage
         * of that here, to decide whether we need to start
         * up the server
         */

    ODatabaseDocumentTx tx = null;
    try {
      tx = getConnection();
    } catch ( Exception e ) {
      final String enc = CharsetHelper.getEncoding();
      //Change the default database location to orientPath
      String confAsString = IOUtils.toString( conf, enc );
      confAsString =
        confAsString.replaceAll( Matcher.quoteReplacement( "$PATH$" ), Matcher.quoteReplacement( getOrientPath() ) );
      conf.close();
      conf = new ByteArrayInputStream( confAsString.getBytes( enc ) );

      server = OServerMain.create();
      server.startup( conf );
      server.activate();

    } finally {
      if ( tx != null ) {
        tx.close();
      }
      conf.close();
    }
  }

  private enum Method {

    HEAD, DELETE, GET, STORE, QUERY
  }

  protected String getUserName() {
    return PentahoSessionHolder.getSession().getName();
  }

}

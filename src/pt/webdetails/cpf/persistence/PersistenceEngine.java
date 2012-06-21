/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package pt.webdetails.cpf.persistence;

import com.orientechnologies.orient.core.db.document.ODatabaseDocumentPool;
import com.orientechnologies.orient.core.db.document.ODatabaseDocumentTx;
import com.orientechnologies.orient.core.exception.ODatabaseException;
import com.orientechnologies.orient.core.exception.ORecordNotFoundException;
import com.orientechnologies.orient.core.id.ORID;
import com.orientechnologies.orient.core.record.impl.ODocument;
import com.orientechnologies.orient.core.sql.query.OSQLSynchQuery;
import java.text.SimpleDateFormat;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;


import com.orientechnologies.orient.server.OServerMain;
import com.orientechnologies.orient.server.OServer;
import java.io.InputStream;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.pentaho.platform.api.engine.IParameterProvider;
import org.pentaho.platform.api.engine.IPentahoSession;
import org.pentaho.platform.engine.core.system.PentahoSessionHolder;
import org.pentaho.platform.engine.security.SecurityHelper;
import pt.webdetails.cpf.InvalidOperationException;
import pt.webdetails.cpf.Settings;
import pt.webdetails.cpf.Util;
import pt.webdetails.cpf.repository.RepositoryUtils;

/**
 *
 * @author pdpi
 */
public class PersistenceEngine {

    private static final Log logger = LogFactory.getLog(PersistenceEngine.class);
    private static PersistenceEngine _instance;
    private static final SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm");
    private static final Settings SETTINGS = Settings.getInstance();
    private static final String DBURL = SETTINGS.getProperty("ORIENT.DBURL");
    private static final String DBUSERNAME = SETTINGS.getProperty("ORIENT.USER");
    private static final String DBPASSWORD = SETTINGS.getProperty("ORIENT.PASSWORD");
    private static final int JSON_INDENT = 2;

    public static synchronized PersistenceEngine getInstance() {
        if (_instance == null) {
            _instance = new PersistenceEngine();
        }
        return _instance;
    }

    private enum Method {

        HEAD, DELETE, GET, STORE, QUERY
    }

    private PersistenceEngine() {
        try {
            logger.info("Creating PersistenceEngine instance");
            initialize();
        } catch (Exception ex) {
            logger.fatal("Could not create PersistenceEngine: " + Util.getExceptionDescription(ex)); //$NON-NLS-1$
            return;
        }

    }

    private void initialize() throws Exception {
        System.setProperty("ORIENTDB_HOME", ".");
        startOrient();
    }

    public String process(IParameterProvider requestParams, IPentahoSession userSession) throws InvalidOperationException {
        String methodString = requestParams.getStringParameter("method", "none");
        JSONObject reply = null;
        try {
            Method mthd = Method.valueOf(methodString.toUpperCase());
            switch (mthd) {
                case DELETE:
                    reply = delete(requestParams, userSession);
                    break;
                case GET:
                    reply = get(requestParams, userSession);
                    break;
                case STORE:
                    reply = store(requestParams, userSession);
                    break;
                case QUERY:
                    if (!SecurityHelper.isPentahoAdministrator(PentahoSessionHolder.getSession())) {
                        throw new SecurityException("Arbitrary querying is only available to admins");
                    }
                    reply = query(requestParams, userSession);
                    break;
            }

            return reply.toString(JSON_INDENT);
        } catch (IllegalArgumentException e) {
            logger.error("Invalid method: " + methodString);
            return "{'result': false}";
        } catch (JSONException e) {
            logger.error("error processing: " + methodString);
            return "{'result': false}";
        }
    }

    private List<ODocument> executeQuery(String query, Map<String, String> params) {
        ODatabaseDocumentTx db = ODatabaseDocumentPool.global().acquire(DBURL, DBUSERNAME, DBPASSWORD);
        try {
            OSQLSynchQuery<ODocument> preparedQuery = new OSQLSynchQuery<ODocument>(query);
            if (params == null) {
                return db.command(preparedQuery).execute();
            } else {
                return db.command(preparedQuery).execute(params);
            }
        } catch (Exception e) {
            logger.error(e);
            return null;
        } finally {
            if (db != null) {
                db.close();
            }
        }

    }

    private JSONObject query(IParameterProvider requestParams, IPentahoSession userSession) throws JSONException {
        final String queryString = requestParams.getStringParameter("query", "");
        return query(queryString, null);
    }

    public JSONObject query(String query, Map<String, String> params) throws JSONException {
        JSONObject json = new JSONObject();

        try {
            json.put("result", Boolean.TRUE);

            List<ODocument> result = executeQuery(query, params);
            JSONArray arr = new JSONArray();
            for (ODocument resDoc : result) {
                arr.put(new JSONObject(resDoc.toJSON()));
            }
            json.put("object", arr);
        } catch (ODatabaseException ode) {
            json.put("result", Boolean.FALSE);
            json.put("errorMessage", "DatabaseException: Review query");
            logger.error(getExceptionDescription(ode));

        }
        return json;
    }

    private JSONObject get(IParameterProvider requestParams, IPentahoSession userSession) throws JSONException {
        final String id = requestParams.getStringParameter("rid", "");
        return get(id);
    }

    public JSONObject get(String id) throws JSONException {
        JSONObject json = new JSONObject(), resultJson;


        try {
            json.put("result", Boolean.TRUE);
            String user = PentahoSessionHolder.getSession().getName();
            Map<String, String> params = new HashMap<String, String>();
            params.put("id", id);
            params.put("user", user);
            List<ODocument> result = executeQuery("select * from Query where @rid = :id and userid = :user", params);

            ODocument doc;

            if (result.size() == 1) {
                json.put("object", new JSONObject(result.get(0).toJSON()));
            } else {
                json.put("result", Boolean.FALSE);
                json.put("errorMessage", "Multiple elements found with id " + id);
            }
        } catch (ODatabaseException orne) {

            if (orne.getCause().getClass() == ORecordNotFoundException.class) {
                logger.error("Record with id " + id + " not found");
                json.put("result", Boolean.FALSE);
                json.put("errorMessage", "No record found with id " + id);
            } else {
                logger.error(getExceptionDescription(orne));
                throw orne;
            }
        }

        return json;
    }

    private JSONObject delete(IParameterProvider requestParams, IPentahoSession userSession) throws JSONException {
        final String id = requestParams.getStringParameter("rid", "");
        return delete(id);
    }

    public JSONObject delete(String id) throws JSONException {

        JSONObject json = new JSONObject();
        String user = PentahoSessionHolder.getSession().getName();
        try {
            json.put("result", Boolean.TRUE);

            Map<String, String> params = new HashMap<String, String>();
            params.put("id", id);
            params.put("user", user);
            List<ODocument> result = executeQuery("select * from Query where @rid = :id and userid = :user", params);
            ODocument doc;

            if (result.size() == 1) {
                doc = result.get(0);
            } else if (result.size() == 0) {
                json.put("result", Boolean.FALSE);
                json.put("errorMessage", "No element found with id " + id);
                return json;
            } else {
                json.put("result", Boolean.FALSE);
                json.put("errorMessage", "Multiple elements found with id " + id);
                return json;
            }


            doc.delete();
        } catch (ODatabaseException orne) {

            if (orne.getCause().getClass() == ORecordNotFoundException.class) {
                logger.error("Record with id " + id + " not found");
                json.put("result", Boolean.FALSE);
                json.put("errorMessage", "No record found with id " + id);
            } else {
                logger.error(getExceptionDescription(orne));
                throw orne;
            }
        }
        return json;
    }

    private JSONObject store(IParameterProvider requestParams, IPentahoSession userSession) throws JSONException {
        final String id = requestParams.getStringParameter("rid", "");
        String className = requestParams.getStringParameter("class", "");
        String data = requestParams.getStringParameter("data", "");
        return store(id, className, data);
    }

    public boolean initializeClass(String className) {
        ODatabaseDocumentTx database = ODatabaseDocumentPool.global().acquire(DBURL, DBUSERNAME, DBPASSWORD);
        if (database.getMetadata().getSchema().getClass(className) != null) {
            return false;
        } else {
            database.getMetadata().getSchema().createClass(className);
            return true;
        }
    }

    public JSONObject store(Persistable obj) {
        JSONObject key = obj.getKey();
        return null;
    }

    public JSONObject store(String id, String className, JSONObject data) {
        JSONObject json = new JSONObject();
        try {
            ODatabaseDocumentTx db = null;
            String user = PentahoSessionHolder.getSession().getName();
            try {

                json.put("result", Boolean.TRUE);


                db = ODatabaseDocumentPool.global().acquire(DBURL, DBUSERNAME, DBPASSWORD);
                ODocument doc;

                if (id == null || id.length() == 0) {
                    doc = new ODocument(db, className);
                    doc.field("userid", user);
                } else {
                    Map<String, String> params = new HashMap<String, String>();
                    params.put("id", id);
                    List<ODocument> result = executeQuery("select * from Query where @rid = :id", params);
                    if (result.size() == 1) {
                        doc = result.get(0);
                        if (!doc.field("userid").toString().equals(user)) {
                            json.put("result", Boolean.FALSE);
                            json.put("errorMessage", "Object id " + id + " belongs to another user");
                            return json;
                        }
                    } else if (result.size() == 0) {
                        json.put("result", Boolean.FALSE);
                        json.put("errorMessage", "No " + className + " found with id " + id);
                        return json;
                    } else {
                        json.put("result", Boolean.FALSE);
                        json.put("errorMessage", "Multiple " + className + " found with id " + id);
                        return json;
                    }
                }

                // CREATE A NEW DOCUMENT AND FILL IT
                Iterator keyIterator = data.keys();
                while (keyIterator.hasNext()) {
                    String key = (String) keyIterator.next();
                    doc.field(key, data.getString(key));
                }

                // SAVE THE DOCUMENT
                doc.save();

                if (id == null || id.length() == 0) {
                    ORID newId = doc.getIdentity();
                    json.put("id", newId.toString());
                }

                return json;
            } catch (ODatabaseException orne) {

                if (orne.getCause().getClass() == ORecordNotFoundException.class) {
                    logger.error("Record with id " + id + " not found");
                    json.put("result", Boolean.FALSE);
                    json.put("errorMessage", "No " + className + " found with id " + id);
                    return json;
                }

                logger.error(getExceptionDescription(orne));
                throw orne;
            } finally {
                if (db != null) {
                    db.close();
                }
            }
        } catch (JSONException e) {
            return json;
        }
    }

    public JSONObject store(String id, String className, String inputData) throws JSONException {
        JSONObject data = new JSONObject(inputData);
        return store(id, className, data);
    }

    private String getExceptionDescription(Exception ex) {
        return ex.getCause().getClass().getName() + " - " + ex.getMessage();
    }

    public void startOrient() throws Exception {
        System.setProperty("ORIENTDB_HOME", ".");
        InputStream conf;
        try {
            conf = RepositoryUtils.readSolutionFileAsStream("solution/cpf/orient.xml");
        } catch (Exception e) {
            logger.warn("Falling back to built-in config");
            conf = getClass().getResourceAsStream("orient.xml");
        }
        /* Acquiring a database connection will throw an
         * exception if the db isn't up. We take advantage
         * of that here, to decide whether we need to start
         * up the server
         */
        try {
            ODatabaseDocumentPool.global().acquire(DBURL, DBUSERNAME, DBPASSWORD);
        } catch (Exception e) {
            OServer server = OServerMain.create();
            server.startup(conf);
            server.activate();
        }
    }
}

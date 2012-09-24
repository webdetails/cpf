/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package pt.webdetails.cpf.persistence;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

/**
 *
 * @author pdpi
 */
public class SimplePersistence {

    private static SimplePersistence instance;
    private static final Log logger = LogFactory.getLog(SimplePersistence.class);

    private SimplePersistence() {
    }

    public synchronized static SimplePersistence getInstance() {
        if (instance == null) {
            instance = new SimplePersistence();
        }
        return instance;
    }

    public <T extends Persistable> List<T> loadAll(Class<T> klass) {
        return load(klass, null);
    }

    public void storeAll(Collection<? extends Persistable> items) {
        PersistenceEngine pe = PersistenceEngine.getInstance();
        for (Persistable item : items) {
            pe.store(item);
        }
    }

    public void deleteAll(Collection<? extends Persistable> items) {
        throw new UnsupportedOperationException("Not implemented yet");
    }

    public void delete(Class<? extends Persistable> klass, Filter filter) {
        PersistenceEngine pe = PersistenceEngine.getInstance();
        String query = "delete from " + klass.getName();
        if (filter != null) {
            query += " where " + filter.toString();
        }
        try {
            pe.command(query, null);
        } catch (JSONException jse) {
            logger.error(jse);
        }
    }

    public <T extends Persistable> List<T> load(Class<T> klass, Filter filter) {
        List<T> list = new ArrayList<T>();
        PersistenceEngine pe = PersistenceEngine.getInstance();
        try {
            String query = "select from " + klass.getName();
            if (filter != null) {
                query += " where " + filter.toString();
            }
            JSONObject json = pe.query(query, null);
            JSONArray arr = json.getJSONArray("object");
            for (int i = 0; i < arr.length(); i++) {
                JSONObject o = arr.getJSONObject(i);
                T inst = klass.newInstance();
                try {
                    inst.fromJSON(o);
                    list.add(inst);
                } catch (JSONException e) {
                }
            }

        } catch (InstantiationException e) {
            return null;
        } catch (IllegalAccessException e) {
            return null;
        } catch (JSONException e) {
            return null;
        }
        return list;
    }
}

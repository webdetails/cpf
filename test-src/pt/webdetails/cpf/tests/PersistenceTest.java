/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package pt.webdetails.cpf.tests;

import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;
import static junit.framework.Assert.*;
import junit.framework.TestCase;
import pt.webdetails.cpf.persistence.PersistenceEngine;
import org.json.JSONObject;
import org.apache.commons.io.IOUtils;

/**
 *
 * @author pdpi
 */
public class PersistenceTest extends TestCase {

    public PersistenceTest() {
        super();
    }

    public PersistenceTest(String name) {
        super(name);
    }

    @Override
    public void setUp() throws Exception {

        PersistenceEngine.getInstance().startOrient();
        super.setUp();
    }

    public void testClassDetection() throws Exception {

        PersistenceEngine pe = PersistenceEngine.getInstance();

        pe.dropClass("testClass");
        assertFalse("testClass should't exist yet", pe.classExists("testClass"));
        assertTrue("testClass wasn't properly initialized", pe.initializeClass("testClass"));
        assertTrue("testClass doesn't exist", pe.classExists("testClass"));
    }

    public void testJSONMarshalling() throws Exception {

        InputStream jsonStream = this.getClass().getResourceAsStream("samples/sample.json");
        String json = IOUtils.toString(jsonStream, "utf-8");
        PersistenceEngine pe = PersistenceEngine.getInstance();
        JSONObject response = pe.store(null, "jsonMarshalling", json);
        assertTrue("Couldn't insert document", (Boolean) response.get("result"));
        Map<String,Object> params = new HashMap<String,Object>();
        params.put("id",response.get("id"));
        JSONObject result = pe.query("select from jsonMarshalling where @rid = :id",params);
        JSONObject retrieved = result.getJSONArray("object").getJSONObject(0);
        assertFalse("Failed at escaping quotes", retrieved.getJSONObject("deep").getJSONObject("deeper").has("oops"));
        assertEquals("Failed at parsing spaces:", "value with spaces",retrieved.getJSONObject("deep").getJSONObject("deeper").getString("spaces"));
        pe.dropClass("jsonMarshalling");
    }
}

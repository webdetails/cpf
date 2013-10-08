/*!
* Copyright 2002 - 2013 Webdetails, a Pentaho company.  All rights reserved.
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

package pt.webdetails.cpf.tests;

import com.orientechnologies.orient.core.record.impl.ODocument;
import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;
import static junit.framework.Assert.*;
import junit.framework.TestCase;
import org.json.JSONException;
import pt.webdetails.cpf.persistence.PersistenceEngine;
import org.json.JSONObject;
import org.apache.commons.io.IOUtils;
import org.junit.Test;
import org.junit.Assert;

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


    
    
    @Test
    public void testInstanceAdd() throws JSONException {
      PersistenceEngine pe = PersistenceEngine.getInstance();
      String json = "{test: 'A'; test2: 'B'}";
      JSONObject response = pe.store(null, "testClass", json);
      Map<String,Object> params = new HashMap<String,Object>();
      params.put("id",response.get("id"));
      
      JSONObject result = pe.query("select from testClass where @rid = :id",params);      
      Assert.assertEquals(result.getJSONArray("object").length(), 1);      
    }

    
    @Test
    public void testInstanceUpdate() throws JSONException {
      PersistenceEngine pe = PersistenceEngine.getInstance();
      String json = "{test: 'A'; test2: 'B'}";
      JSONObject response = pe.store(null, "testClass", json);
      
      json ="{test: 'B'; test3: 'C'}";
      String originalId = response.getString("id");
      response = pe.store(originalId, "testClass", json);
      
      Map<String,Object> params = new HashMap<String,Object>();
      params.put("id",originalId);      
      JSONObject result = pe.query("select from testClass where @rid = :id",params);      

      Assert.assertEquals(result.getJSONArray("object").getJSONObject(0).getString("test"), "B");      
    }
    
    
    
    @Test
    public void testInstanceDelete() throws JSONException {
      PersistenceEngine pe = PersistenceEngine.getInstance();
      String json = "{test: 'A'; test2: 'B'}";
      JSONObject response = pe.store(null, "testClass", json);
      
      String originalId = response.getString("id");
      
      response = pe.deleteRecord(originalId);
      
      Assert.assertTrue(response.getBoolean("result"));

      Map<String,Object> params = new HashMap<String,Object>();
      params.put("id", originalId);
      
      JSONObject result = pe.query("select from testClass where @rid = :id",params);      
      Assert.assertEquals(result.getJSONArray("object").length(), 0);      
    }    
    
    
    
    
    @Test
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
    
    
    @Test
    public void testEscaping() {
      
      
      ODocument doc = new ODocument();
      String s = "{\r\n    \"name\": \"test\",\r\n    \"nested\": {\r\n        \"key\": \"value\",\r\n        \"anotherKey\": 123\r\n    },\r\n    \"deep\": {\r\n        \"deeper\": {\r\n            \"k\": \"v\",\r\n            \"quotes\": \"\\\"\\\",\\\"oops\\\":\\\"123\\\"\",  \r\n            \"likeJson\": \"[1,2,3]\",\r\n            \"spaces\":  \"value with spaces\"\r\n        }\r\n    }\r\n}";
      doc.fromJSON(s);
      Assert.assertEquals(doc.field("deep[deeper][quotes]"), "\"\",\"oops\":\"123\"");
      
      String res = doc.toJSON();
      
      assertTrue(res.contains("\"quotes\": \"\\\"\\\",\\\"oops\\\":\\\"123\\\"\""));
      
  }    
    
    
}

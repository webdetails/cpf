/*!
* Copyright 2002 - 2021 Webdetails, a Hitachi Vantara company.  All rights reserved.
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

import com.orientechnologies.orient.core.record.impl.ODocument;
import org.apache.commons.io.IOUtils;
import org.json.JSONObject;
import org.junit.Before;
import org.junit.Test;
import pt.webdetails.cpf.PluginEnvironment;
import pt.webdetails.cpf.PluginEnvironmentForTests;

import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

public class PersistenceTest {

  @Before
  public void setUp() {
    PluginEnvironment.init( new PluginEnvironmentForTests() );
    PersistenceEngineForTests.getInstance();
  }

  @Test
  public void testClassDetection() {
    PersistenceEngineForTests pe = PersistenceEngineForTests.getInstance();

    pe.dropClass( "testClass" );
    assertFalse( "testClass shouldn't exist yet", pe.classExists( "testClass" ) );
    assertTrue( "testClass wasn't properly initialized", pe.initializeClass( "testClass" ) );
    assertTrue( "testClass doesn't exist", pe.classExists( "testClass" ) );
  }

  @Test
  public void testInstanceAdd() throws Exception {
    PersistenceEngineForTests pe = PersistenceEngineForTests.getInstance();
    String json = "{test: 'A'; test2: 'B'}";
    JSONObject response = pe.store( null, "testClass", json );
    Map<String, Object> params = new HashMap<>();
    params.put( "id", response.get( "id" ) );

    JSONObject result = pe.query( "select from testClass where @rid = :id", params );
    assertEquals( 1, result.getJSONArray( "object" ).length() );
  }

  @Test
  public void testInstanceUpdate() throws Exception {
    PersistenceEngineForTests pe = PersistenceEngineForTests.getInstance();
    String json = "{test: 'A'; test2: 'B'}";
    JSONObject response = pe.store( null, "testClass", json );

    json = "{test: 'B'; test3: 'C'}";
    String originalId = response.getString( "id" );
    response = pe.store( originalId, "testClass", json );

    Map<String, Object> params = new HashMap<>();
    params.put( "id", originalId );
    JSONObject result = pe.query( "select from testClass where @rid = :id", params );

    assertEquals( "B", result.getJSONArray( "object" ).getJSONObject( 0 ).getString( "test" ) );
  }

  @Test
  public void testInstanceDelete() throws Exception {
    PersistenceEngineForTests pe = PersistenceEngineForTests.getInstance();
    String json = "{test: 'A'; test2: 'B'}";
    JSONObject response = pe.store( null, "testClass", json );

    String originalId = response.getString( "id" );

    response = pe.deleteRecord( originalId );

    assertTrue( response.getBoolean( "result" ) );

    Map<String, Object> params = new HashMap<>();
    params.put( "id", originalId );

    JSONObject result = pe.query( "select from testClass where @rid = :id", params );
    assertEquals( 0, result.getJSONArray( "object" ).length() );
  }

  @Test
  public void testJSONMarshalling() throws Exception {
    String userdir = System.getProperty( "user.dir" );
    File sample = new File( userdir + "/src/test/resources/test-samples/sample.json" );
    InputStream jsonStream = new FileInputStream( sample );
    String json = IOUtils.toString( jsonStream, "utf-8" );
    PersistenceEngineForTests pe = PersistenceEngineForTests.getInstance();
    JSONObject response = pe.store( null, "jsonMarshalling", json );
    assertTrue( "Couldn't insert document", (Boolean) response.get( "result" ) );
    Map<String, Object> params = new HashMap<>();
    params.put( "id", response.get( "id" ) );
    JSONObject result = pe.query( "select from jsonMarshalling where @rid = :id", params );
    JSONObject retrieved = result.getJSONArray( "object" ).getJSONObject( 0 );
    assertFalse( "Failed at escaping quotes",
      retrieved.getJSONObject( "deep" ).getJSONObject( "deeper" ).has( "oops" ) );
    assertEquals( "Failed at parsing spaces:", "value with spaces",
      retrieved.getJSONObject( "deep" ).getJSONObject( "deeper" ).getString( "spaces" ) );
    pe.dropClass( "jsonMarshalling" );
  }

  @Test
  public void testEscaping() {
    ODocument doc = new ODocument();
    String s =
      "{\r\n    \"name\": \"test\",\r\n    \"nested\": {\r\n        \"key\": \"value\"," +
        "\r\n        \"anotherKey\": 123\r\n    },\r\n    \"deep\": {\r\n        \"deeper\": {\r\n            \"k\": " +
        "\"v\",\r\n            \"quotes\": \"\\\"\\\",\\\"oops\\\":\\\"123\\\"\",  " +
        "\r\n            \"likeJson\": \"[1,2,3]\",\r\n            \"spaces\":  \"value with spaces\"\r\n        " +
        "}\r\n    }\r\n}";
    doc.fromJSON( s );
    String expected1 = "\"\",\"oops\":\"123\"";
    assertEquals( expected1, doc.field( "deep[deeper][quotes]" ) );

    String res = doc.toJSON();
    String expected2 = "\"quotes\":\"\\\"\\\",\\\"oops\\\":\\\"123\\\"\"";
    assertTrue( res.contains( expected2 ) );
  }
}

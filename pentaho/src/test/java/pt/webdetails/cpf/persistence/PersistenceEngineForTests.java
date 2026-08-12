/*! ******************************************************************************
 *
 * Pentaho
 *
 * Copyright (C) 2024 - 2026 by Pentaho Canada Inc. : http://www.pentaho.com
 *
 * Use of this software is governed by the Business Source License included
 * in the LICENSE.TXT file.
 *
 * Change Date: 2030-06-15
 ******************************************************************************/



package pt.webdetails.cpf.persistence;

import com.orientechnologies.orient.core.db.ODatabaseSession;
import com.orientechnologies.orient.server.OServer;
import com.orientechnologies.orient.server.OServerMain;
import org.apache.commons.io.IOUtils;
import pt.webdetails.cpf.utils.CharsetHelper;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.util.regex.Matcher;

public class PersistenceEngineForTests extends PersistenceEngine {

  private static PersistenceEngineForTests _instance;
  private OServer server;

  public static PersistenceEngineForTests getInstance() {
    if ( _instance == null ) {
      _instance = new PersistenceEngineForTests();
    }
    return _instance;
  }

  @Override
  protected String getOrientPath() {
    return "./databases/";
  }

  @Override
  protected String getUserName() {
    return "test";
  }

  @Override
  public void startOrient() throws Exception {
    try ( InputStream conf = new PersistenceEngineSettingsReader().getConfigurationInputStream() ) {
      final String enc = CharsetHelper.getEncoding();
      String confAsString = IOUtils.toString( conf, enc );
      confAsString = confAsString.replaceAll(
        Matcher.quoteReplacement( "$PATH$" ),
        Matcher.quoteReplacement( getOrientPath() ) );

      server = OServerMain.create();
      server.startup( new ByteArrayInputStream( confAsString.getBytes( enc ) ) );
      server.activate();

      try ( ODatabaseSession database = server.openDatabase( "webdetails" ) ) {
        if ( database.getMetadata().getSecurity().getUser( "admin" ) != null ) {
          database.getMetadata().getSecurity().dropUser( "admin" );
        }
        database.getMetadata().getSecurity().createUser( "admin", "admin", "admin" );
      }
    }
  }
}


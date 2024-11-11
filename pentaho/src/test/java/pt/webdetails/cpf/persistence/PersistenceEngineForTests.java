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

public class PersistenceEngineForTests extends PersistenceEngine {

  private static PersistenceEngineForTests _instance;

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
}


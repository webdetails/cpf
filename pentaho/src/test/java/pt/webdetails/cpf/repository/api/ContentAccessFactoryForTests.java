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


package pt.webdetails.cpf.repository.api;

public class ContentAccessFactoryForTests implements IContentAccessFactory {
  @Override
  public IUserContentAccess getUserContentAccess( String s ) {
    return null;
  }

  @Override
  public IReadAccess getPluginRepositoryReader( String s ) {
    return new ReadAccessForTests();
  }

  @Override
  public IRWAccess getPluginRepositoryWriter( String s ) {
    return null;
  }

  @Override
  public IReadAccess getPluginSystemReader( String s ) {
    return new ReadAccessForTests();
  }

  @Override
  public IRWAccess getPluginSystemWriter( String s ) {
    return null;
  }

  @Override
  public IReadAccess getOtherPluginSystemReader( String s, String s2 ) {
    return new ReadAccessForTests();
  }

  @Override
  public IRWAccess getOtherPluginSystemWriter( String s, String s2 ) {
    return null;
  }
}

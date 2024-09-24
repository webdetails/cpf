/*! ******************************************************************************
 *
 * Pentaho
 *
 * Copyright (C) 2024 by Hitachi Vantara, LLC : http://www.pentaho.com
 *
 * Use of this software is governed by the Business Source License included
 * in the LICENSE.TXT file.
 *
 * Change Date: 2028-08-13
 ******************************************************************************/

package pt.webdetails.cpf;

import pt.webdetails.cpf.context.api.IUrlProvider;
import pt.webdetails.cpf.plugincall.api.IPluginCall;
import pt.webdetails.cpf.repository.api.ContentAccessFactoryForTests;
import pt.webdetails.cpf.repository.api.IContentAccessFactory;

public class PluginEnvironmentForTests extends PluginEnvironment {

  public PluginEnvironmentForTests() {

  }

  @Override
  public IContentAccessFactory getContentAccessFactory() {
    return new ContentAccessFactoryForTests();
  }

  @Override
  public IUrlProvider getUrlProvider() {
    return null;
  }

  @Override
  public PluginSettings getPluginSettings() {
    return null;
  }

  @Override
  public String getPluginId() {
    return null;
  }

  @Override
  public IPluginCall getPluginCall( String s, String s2, String s3 ) {
    return null;
  }

}

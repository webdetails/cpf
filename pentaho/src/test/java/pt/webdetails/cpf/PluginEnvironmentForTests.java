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

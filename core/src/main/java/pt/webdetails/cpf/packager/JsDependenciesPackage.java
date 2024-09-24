/* This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this file,
 * You can obtain one at http://mozilla.org/MPL/2.0/. */

package pt.webdetails.cpf.packager;

import pt.webdetails.cpf.context.api.IUrlProvider;
import pt.webdetails.cpf.repository.api.IContentAccessFactory;

public class JsDependenciesPackage extends DependenciesPackage {

  public JsDependenciesPackage( String name, IContentAccessFactory factory, IUrlProvider urlProvider ) {
    super( name, PackageType.JS, factory, urlProvider );
  }

}

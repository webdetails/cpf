/* This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this file,
 * You can obtain one at http://mozilla.org/MPL/2.0/. */

package pt.webdetails.cpf.packager.dependencies;

import java.io.IOException;


public abstract class Dependency {

  public boolean isOlderVersionThan( Dependency other ) {
    // assuming version numberings always increase lexicographically
    String version = getVersion();
    return version == null || version.compareTo( other.getVersion() ) < 0;
  }

  public abstract String getVersion();

  //TODO: does it make sense to have the same for both?
  public abstract String getDependencyInclude();

  public abstract String getContents() throws IOException;
}

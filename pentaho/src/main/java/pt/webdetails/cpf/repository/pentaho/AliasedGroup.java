/* This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this file,
 * You can obtain one at http://mozilla.org/MPL/2.0/. */

package pt.webdetails.cpf.repository.pentaho;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

import pt.webdetails.cpf.PentahoPluginEnvironment;
import pt.webdetails.cpf.repository.api.IReadAccess;

/**
 *
 * @author pdpi
 */
public class AliasedGroup {

//    List<Resolver> resolverList;
    List<IReadAccess> repositoryList;

    public AliasedGroup() {
//        resolverList = new ArrayList<Resolver>();
        repositoryList = new ArrayList<IReadAccess>();
    }

    public void addClass(Class<?> klass) {
//        resolverList.add(new ClassResolver(klass));
        repositoryList.add(new ClassLoaderResolver(klass));
    }

    public void addSolutionDir(String dir) {
//        resolverList.add(new SolutionResolver(dir));
      //TODO: right now should work for getPluginRepositoryResourceAccess("cdb/exporters/templates"); ideally should work as getPluginRepositoryResourceAccess("exporters/templates");
        repositoryList.add(PentahoPluginEnvironment.getInstance().getPluginRepositoryReader(dir));
    }

    public InputStream getResourceStream(String file) throws FileNotFoundException {
      for (IReadAccess resolver : repositoryList) {
          if (resolver.fileExists(file)) {
            try {
              return resolver.getFileInputStream(file);
            } catch (IOException e) {
              //carry on
            }
          }
      }
      throw new FileNotFoundException(file);
    }
}

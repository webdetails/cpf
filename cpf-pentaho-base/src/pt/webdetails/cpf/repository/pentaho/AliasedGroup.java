/* This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this file,
 * You can obtain one at http://mozilla.org/MPL/2.0/. */

package pt.webdetails.cpf.repository.pentaho;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang.NotImplementedException;

import pt.webdetails.cpf.PentahoPluginEnvironment;
import pt.webdetails.cpf.repository.api.IBasicFile;
import pt.webdetails.cpf.repository.api.IBasicFileFilter;
import pt.webdetails.cpf.repository.api.IReadAccess;
import pt.webdetails.cpf.repository.impl.ClassLoaderResourceAccess;

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
        repositoryList.add(PentahoPluginEnvironment.getInstance().getPluginRepositoryResourceAccess(dir));
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
//        InputStream res = null;
//        for (Resolver resolver : resolverList) {
//            res = resolver.getStream(file);
//            if (res != null) {
//                break;
//            }
//        }
//        if (res == null) {
//            throw new FileNotFoundException(file);
//        }
//        return res;
    }

    private static class ClassLoaderResolver extends ClassLoaderResourceAccess implements IReadAccess {

      public ClassLoaderResolver(Class<?> classe) {
        super(classe.getClassLoader(), null);
      }

      public IBasicFile fetchFile(String path) {
        throw new NotImplementedException();
      }

      public List<IBasicFile> listFiles(String path, IBasicFileFilter filter) {
        throw new NotImplementedException();
      }

    }
}
//
//interface Resolver {
//
//    public InputStream getStream(String file);
//}
//
//
//
//class ClassResolver implements Resolver {
//
//    private Class<?> klass;
//
//    public ClassResolver(Class<?> klass) {
//        this.klass = klass;
//    }
//
//    public InputStream getStream(String file) {
//        String path = klass.getPackage().getName().replaceAll("\\.", "/") + "/";
//        return klass.getClassLoader().getResourceAsStream(path + file);
//    }
//}
//
//class SolutionResolver implements Resolver {
//
//    private String path;
//
//    public SolutionResolver(String path) {
//        this.path = path;
//    }
//
//    public InputStream getStream(String file) {
//        try {
//            String fullPath = (path + "/" + file).replaceAll("//", "/");
//            //FIXME
//            return PentahoPluginEnvironment. getPluginResourceAccess(null).getFileInputStream(fullPath);
//        } catch (Exception ex) {
////    //FIXME COMPILING
////      return null;
//            return null;
//        }
//    }
//}

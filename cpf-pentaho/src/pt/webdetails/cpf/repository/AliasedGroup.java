/*!
* Copyright 2002 - 2013 Webdetails, a Pentaho company.  All rights reserved.
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

package pt.webdetails.cpf.repository;

import java.io.FileNotFoundException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

/**
 *
 * @author pdpi
 */
public class AliasedGroup {

    List<Resolver> resolverList;

    public AliasedGroup() {
        resolverList = new ArrayList<Resolver>();
    }

    public void addClass(Class<?> klass) {
        resolverList.add(new ClassResolver(klass));
    }

    public void addSolutionDir(String dir) {
        resolverList.add(new SolutionResolver(dir));
    }

    public InputStream getResourceStream(String file) throws FileNotFoundException {
        InputStream res = null;
        for (Resolver resolver : resolverList) {
            res = resolver.getStream(file);
            if (res != null) {
                break;
            }
        }
        if (res == null) {
            throw new FileNotFoundException(file);
        }
        return res;
    }
}

interface Resolver {

    public InputStream getStream(String file);
}

class ClassResolver implements Resolver {

    private Class<?> klass;

    public ClassResolver(Class<?> klass) {
        this.klass = klass;
    }

    public InputStream getStream(String file) {
        String path = klass.getPackage().getName().replaceAll("\\.", "/") + "/";
        return klass.getClassLoader().getResourceAsStream(path + file);
    }
}

class SolutionResolver implements Resolver {

    private String path;

    public SolutionResolver(String path) {
        this.path = path;
    }

    public InputStream getStream(String file) {
        try {
            String fullPath = (path + "/" + file).replaceAll("//", "/");
            return PentahoRepositoryAccess.getRepository().getResourceInputStream(fullPath);
        } catch (Exception ex) {
            return null;
        }
    }
}

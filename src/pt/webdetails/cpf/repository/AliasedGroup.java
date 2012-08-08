/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
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

    public void addClass(Class klass) {
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

    private Class klass;

    public ClassResolver(Class klass) {
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
            return RepositoryAccess.getRepository().getResourceInputStream(fullPath);
        } catch (Exception ex) {
            return null;
        }
    }
}

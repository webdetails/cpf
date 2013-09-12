/* This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this file,
 * You can obtain one at http://mozilla.org/MPL/2.0/. */

package pt.webdetails.cpf.repository;

import java.util.ArrayList;
import java.util.List;
import org.pentaho.platform.api.engine.ISolutionFile;

import pt.webdetails.cpf.repository.api.IBasicFile;

/**
 * 
 * @author dfscm
 * @deprecated use {@link IBasicFile}
 */
public class PentahoRepositoryFile implements IRepositoryFile{
    private ISolutionFile file;
    
    public PentahoRepositoryFile(ISolutionFile file){
        this.file = file;
    }
    
    public ISolutionFile getSolutionFile(){
        return file;
    }

    @Override
    public boolean isDirectory() {
        return file.isDirectory();
    }

    @Override
    public String getFileName() {
        return file.getFileName();
    }

    @Override
    public String getSolutionPath() {
        return file.getSolutionPath();
    }

    @Override
    public String getSolution() {
        return file.getSolution();
    }

    @Override
    public String getFullPath() {
        return file.getFullPath();
    }

    @Override
    public boolean isRoot() {
        return file.isRoot();
    }

    @Override
    public byte[] getData() {
        return file.getData();
    }

    @Override
    public boolean exists() {
        return file.exists();
    }

    @Override
    public long getLastModified() {
        return file.getLastModified();
    }

    @Override
    public String getExtension() {
        return file.getExtension();
    }

    @Override
    public IRepositoryFile[] listFiles() {
        List<IRepositoryFile> list = new ArrayList<IRepositoryFile>();
        
        ISolutionFile[] solutionFiles = file.listFiles();
        for(ISolutionFile solutionFile : solutionFiles){
            list.add(new PentahoRepositoryFile(solutionFile));
        }
        
        return list.toArray(new IRepositoryFile[list.size()]);
    }

    @Override
    public IRepositoryFile[] listFiles(IRepositoryFileFilter iff) {
        List<IRepositoryFile> list = new ArrayList<IRepositoryFile>();
        
        ISolutionFile[] solutionFiles = file.listFiles(((PentahoRepositoryFileFilter)iff).getFileFiler());
        for(ISolutionFile solutionFile : solutionFiles){
            list.add(new PentahoRepositoryFile(solutionFile));
        }
        
        return list.toArray(new IRepositoryFile[list.size()]);
    }

    @Override
    public IRepositoryFile retrieveParent() {
        throw new UnsupportedOperationException("Not supported yet.");
    }
    
    
}

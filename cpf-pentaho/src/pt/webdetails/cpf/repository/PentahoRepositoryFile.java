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

import java.util.ArrayList;
import java.util.List;
import org.pentaho.platform.api.engine.ISolutionFile;

/**
 * 
 * @author dfscm
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

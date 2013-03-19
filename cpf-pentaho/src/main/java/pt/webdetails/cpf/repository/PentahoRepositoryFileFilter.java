/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package pt.webdetails.cpf.repository;

import org.pentaho.platform.api.engine.IFileFilter;

/**
 * 
 * @author Sammy Guergachi <sguergachi at gmail.com>
 */
public class PentahoRepositoryFileFilter implements IRepositoryFileFilter{

    private IFileFilter fileFilter;

    public PentahoRepositoryFileFilter(IFileFilter fileFilter) {
        this.fileFilter = fileFilter;
    }
    
    
    
    @Override
    public boolean accept(IRepositoryFile isf) {
        return fileFilter.accept(((PentahoRepositoryFile)isf).getSolutionFile());
    }
    
    public IFileFilter getFileFiler(){
        return fileFilter;
    }

}

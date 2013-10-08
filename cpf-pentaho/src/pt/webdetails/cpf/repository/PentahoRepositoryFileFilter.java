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

import org.pentaho.platform.api.engine.IFileFilter;

/**
 * 
 * @author Sammy Guergachi <sguergachi at gmail.com>
 */
public class PentahoRepositoryFileFilter implements IRepositoryFileFilter {

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

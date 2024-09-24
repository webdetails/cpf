/* This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this file,
 * You can obtain one at http://mozilla.org/MPL/2.0/. */

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

/* This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this file,
 * You can obtain one at http://mozilla.org/MPL/2.0/. */

package pt.webdetails.cpf.repository;

import pt.webdetails.cpf.repository.api.IBasicFile;

/**
 *
 * @author dfscm
 *  @deprecated use {@link IBasicFile}
 */
public interface IRepositoryFile {

    public boolean isDirectory();

    public String getFileName();

    public String getSolutionPath();

    @Deprecated
    public String getSolution();

    public String getFullPath();

    public IRepositoryFile[] listFiles();

    public IRepositoryFile[] listFiles(IRepositoryFileFilter iff);

    //TODO: root of what?
    public boolean isRoot();

    public IRepositoryFile retrieveParent();

    public byte[] getData();

    public boolean exists();

    public long getLastModified();

    public String getExtension();
}

/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package pt.webdetails.cpf.repository;

/**
 *
 * @author dfscm
 */
public interface IRepositoryFile {
    public boolean isDirectory();

    public String getFileName();

    public String getSolutionPath();

    public String getSolution();

    public String getFullPath();

    public IRepositoryFile[] listFiles();

    public IRepositoryFile[] listFiles(IRepositoryFileFilter iff);

    public boolean isRoot();

    public IRepositoryFile retrieveParent();

    public byte[] getData();

    public boolean exists();

    public long getLastModified();

    public String getExtension();
}

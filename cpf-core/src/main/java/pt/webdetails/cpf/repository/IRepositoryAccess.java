/* This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this file,
 * You can obtain one at http://mozilla.org/MPL/2.0/. */
package pt.webdetails.cpf.repository;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;

import org.dom4j.Document;

import pt.webdetails.cpf.repository.BaseRepositoryAccess.FileAccess;
import pt.webdetails.cpf.repository.BaseRepositoryAccess.SaveFileStatus;
import pt.webdetails.cpf.session.IUserSession;

public interface IRepositoryAccess {

    public abstract String getEncoding();

    public abstract SaveFileStatus publishFile(String fileAndPath,
            String contents, boolean overwrite)
            throws UnsupportedEncodingException;

    public abstract SaveFileStatus publishFile(String fileAndPath, byte[] data,
            boolean overwrite);

    public abstract SaveFileStatus publishFile(String solutionPath,
            String fileName, byte[] data, boolean overwrite);

    public abstract SaveFileStatus publishFile(String baseUrl, String path,
            String fileName, byte[] data, boolean overwrite);

    public abstract boolean removeFile(String solutionPath);

    public abstract boolean removeFileIfExists(String solutionPath);

    public abstract boolean resourceExists(String solutionPath);

    public abstract boolean createFolder(String solutionFolderPath)
            throws IOException;

    public abstract boolean canWrite(String filePath);

    public abstract boolean hasAccess(String filePath, FileAccess access);

    public abstract InputStream getResourceInputStream(String filePath)
            throws FileNotFoundException;

    public abstract InputStream getResourceInputStream(String filePath,
            FileAccess fileAccess) throws FileNotFoundException;

    public abstract InputStream getResourceInputStream(String filePath,
            FileAccess fileAccess, boolean getLocalizedResource)
            throws FileNotFoundException;

    public abstract Document getResourceAsDocument(String solutionPath)
            throws IOException;

    public abstract Document getResourceAsDocument(String solutionPath,
            FileAccess fileAccess) throws IOException;

    public abstract String getResourceAsString(String solutionPath)
            throws IOException;

    public abstract String getResourceAsString(String solutionPath,
            FileAccess fileAccess) throws IOException;

    public abstract SaveFileStatus copySolutionFile(String fromFilePath,
            String toFilePath) throws IOException;
    
    public abstract String getSolutionPath(String path);
    
    public abstract void setUserSession(IUserSession userSession);
    
    public abstract IRepositoryFile getRepositoryFile(String path, FileAccess fileAccess);
    
    public abstract String getJqueryFileTree(final String dir, final String fileExtensions, final String access) ;
    
    public abstract String getJSON(final String dir, final String fileExtensions, final String access);

}
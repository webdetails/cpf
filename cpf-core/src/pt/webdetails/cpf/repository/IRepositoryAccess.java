/* This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this file,
 * You can obtain one at http://mozilla.org/MPL/2.0/. */

package pt.webdetails.cpf.repository;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;

import org.apache.commons.lang.StringUtils;
import org.dom4j.Document;

import pt.webdetails.cpf.plugin.CorePlugin;
import pt.webdetails.cpf.session.IUserSession;

//TODO: breathe and decide what this should do
public interface IRepositoryAccess {

    // warning: enums will only leave here if there is an interface left behind
    // along with a good justification
    public enum FileAccess {//TODO:use masks?
  //TODO: simplify to 3?
      READ,
      EDIT,
      EXECUTE,
      DELETE,
      @Deprecated
      CREATE,
      NONE;
  
      public static FileAccess parse(String fileAccess) {
          try {
              return FileAccess.valueOf(StringUtils.upperCase(fileAccess));
          } catch (Exception e) {
              return null;
          }
      }
    }

    public enum SaveFileStatus {
        OK,
        FAIL
    }

    public String getEncoding();

    public SaveFileStatus publishFile(String fileAndPath,
            String contents, boolean overwrite)
            throws UnsupportedEncodingException;

    public SaveFileStatus publishFile(String fileAndPath, byte[] data,
            boolean overwrite);

    public SaveFileStatus publishFile(String solutionPath,
            String fileName, byte[] data, boolean overwrite);

    // TODO: do we really need that one as well? i think we have enough?
    @Deprecated
    public SaveFileStatus publishFile(String baseUrl, String path,
            String fileName, byte[] data, boolean overwrite);

    public boolean removeFile(String solutionPath);

    public boolean removeFileIfExists(String solutionPath);

    public boolean resourceExists(String solutionPath);

    public boolean createFolder(String solutionFolderPath)
            throws IOException;

    public boolean canWrite(String filePath);

    public boolean hasAccess(String filePath, FileAccess access);

    public InputStream getResourceInputStream(String filePath)
            throws FileNotFoundException;

    public InputStream getResourceInputStream(String filePath,
            FileAccess fileAccess) throws FileNotFoundException;

    public InputStream getResourceInputStream(String filePath,
            FileAccess fileAccess, boolean getLocalizedResource)
            throws FileNotFoundException;

    @Deprecated
    public Document getResourceAsDocument(String solutionPath)
            throws IOException;

    @Deprecated
    /**
     * @deprecated use {@link #getResourceAsString(String solutionPath, FileAccess fileAccess)}
     */
    public Document getResourceAsDocument(String solutionPath,
            FileAccess fileAccess) throws IOException;

    public String getResourceAsString(String solutionPath)
            throws IOException;

    public String getResourceAsString(String solutionPath, FileAccess fileAccess) throws IOException;

    public SaveFileStatus copySolutionFile(String fromFilePath,
            String toFilePath) throws IOException;

    public String getSolutionPath(String path);

    /*
     * TODO: This getrepository / getsolution should be consistent
     * Best would be to remove the whole "solution" concept
     * ^ No, best would be to have a solution and a system concept
     *   ..and not this bloated interface
     */
    public IRepositoryFile getRepositoryFile(String path, FileAccess fileAccess);

      //XXX review - and how would we set a user session for file access in a single user environment
//    public void setUserSession(IUserSession userSession);

    //TODO: is there another way?
    public void setPlugin(CorePlugin plugin);
    public IRepositoryFile getSettingsFile(String path, FileAccess fileAccess);
    public String getSettingsResourceAsString(String settingsPath)
            throws IOException;

    /*
     * TODO: This should really be getSettingsFiles ? make those two methods consistent
     * ^ no, it doesn't make sense as well but it isn't getSettingFiles
     */
    @Deprecated
    public IRepositoryFile[] getPluginFiles(String baseDir, FileAccess accessMode);

    public IRepositoryFile[] listRepositoryFiles(IRepositoryFileFilter filter);

    public IRepositoryFile[] getSettingsFileTree(final String dir, final String fileExtensions, FileAccess access);


    @Deprecated
    public String getJqueryFileTree(final String dir, final String fileExtensions, final String access) ;
    @Deprecated
    public String getJSON(final String dir, final String fileExtensions, final String access);



}
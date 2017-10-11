/*!
* Copyright 2002 - 2017 Webdetails, a Hitachi Vantara company.  All rights reserved.
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

import org.apache.commons.lang.StringUtils;
import org.dom4j.Document;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;

//import pt.webdetails.cpf.plugin.CorePlugin;

//TODO: breathe and decide what this should do

/**
 * @deprecated use {@link pt.webdetails.cpf.repository.api.IContentAccessFactory}
 */
public interface IRepositoryAccess {

  // warning: enums will only leave here if there is an interface left behind
  // along with a good justification
  public enum FileAccess { //TODO:use masks?
    //TODO: simplify to 3?
    READ,
    EDIT,
    EXECUTE,
    DELETE,
    @Deprecated
    CREATE,
    NONE;

    public static FileAccess parse( String fileAccess ) {
      try {
        return FileAccess.valueOf( StringUtils.upperCase( fileAccess ) );
      } catch ( Exception e ) {
        return null;
      }
    }
  }

  public enum SaveFileStatus {
    OK,
    FAIL
  }

  @Deprecated
  public String getEncoding();

  //TODO: coherent throwing
  public SaveFileStatus publishFile( String fileAndPath, String contents, boolean overwrite )
    throws UnsupportedEncodingException;

  public SaveFileStatus publishFile( String fileAndPath, byte[] data, boolean overwrite );

  /**
   * @deprecated use {@link #publishFile(String, byte[], boolean)}
   */
  public SaveFileStatus publishFile( String solutionPath,
                                     String fileName, byte[] data, boolean overwrite );

  // TODO: do we really need that one as well?
  @Deprecated
  public SaveFileStatus publishFile( String baseUrl, String path,
                                     String fileName, byte[] data, boolean overwrite );

  public boolean removeFile( String solutionPath );

  public boolean removeFileIfExists( String solutionPath );

  public boolean resourceExists( String solutionPath );

  public boolean createFolder( String solutionFolderPath )
    throws IOException;

  public boolean canWrite( String filePath );

  public boolean hasAccess( String filePath, FileAccess access );

  public InputStream getResourceInputStream( String filePath )
    throws FileNotFoundException;

  public InputStream getResourceInputStream( String filePath,
                                             FileAccess fileAccess ) throws FileNotFoundException;

  public InputStream getResourceInputStream( String filePath,
                                             FileAccess fileAccess, boolean getLocalizedResource )
    throws FileNotFoundException;

  @Deprecated
  public Document getResourceAsDocument( String solutionPath )
    throws IOException;

  @Deprecated
  /**
   * @deprecated use {@link #getResourceAsString(String solutionPath, FileAccess fileAccess)}
   */
  public Document getResourceAsDocument( String solutionPath,
                                         FileAccess fileAccess ) throws IOException;

  public String getResourceAsString( String solutionPath )
    throws IOException;

  public String getResourceAsString( String solutionPath, FileAccess fileAccess ) throws IOException;

  //TODO: UserContentAccess, usages? -> copyFile
  // used in cde, cdv, cdb
  public SaveFileStatus copySolutionFile( String fromFilePath,
                                          String toFilePath ) throws IOException;

  //TODO: UserContentAccess, usages --> getFullPath?
  //known uses: cdf(still from pho), cdv:GlobalScope#loadTests
  @Deprecated
  public String getSolutionPath( String path );

  /*
   * TODO: This getrepository / getsolution should be consistent
   * Best would be to remove the whole "solution" concept
   * ^ No, best would be to have a solution and a system concept
   *   ..and not this bloated interface
   */
  public IRepositoryFile getRepositoryFile( String path, FileAccess fileAccess );

  //XXX review - and how would we set a user session for file access in a single user environment
  //    public void setUserSession(IUserSession userSession);

  //    //TODO: is there another way?
  //    public void setPlugin(CorePlugin plugin);
  public IRepositoryFile getSettingsFile( String path, FileAccess fileAccess );

  public String getSettingsResourceAsString( String settingsPath )
    throws IOException;

  //    /**
  //     * TODO: MERGE WITH getSettingsFileTree/listRepositoryFiles
  //     * known uses:
  //     * cda:SolutionRepositoryUtils#getCdaList
  //     *  - IRepositoryFile[] cdaTree = repository.getPluginFiles("/", FileAccess.READ);
  //     *  after that only uses filename, fullPath
  //     */
  //    @Deprecated
  //    public IRepositoryFile[] getPluginFiles(String baseDir, FileAccess accessMode);

  /**
   * TODO: used anywhere? no-perm
   *
   * @param filter
   * @return
   */
  public IRepositoryFile[] listRepositoryFiles( IRepositoryFileFilter filter );

  /**
   * known usages: cda:DefaultCdaEnvironment#getComponentsFiles repo.getSettingsFileTree
   * ("resources/components/connections",
   * "xml", FileAccess.READ) after that only contents are used
   *
   * @param dir
   * @param fileExtensions
   * @param access
   * @return
   */
  public IRepositoryFile[] getSettingsFileTree( final String dir, final String fileExtensions, FileAccess access );


  @Deprecated
  public String getJqueryFileTree( final String dir, final String fileExtensions, final String access );

  @Deprecated
  public String getJSON( final String dir, final String fileExtensions, final String access );


}

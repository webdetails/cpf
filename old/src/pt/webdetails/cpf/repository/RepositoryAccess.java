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

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.io.FilenameUtils;
import org.apache.commons.io.IOUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.dom4j.Document;
import org.pentaho.platform.api.engine.IAclSolutionFile;
import org.pentaho.platform.api.engine.IFileFilter;
import org.pentaho.platform.api.engine.IPentahoAclEntry;
import org.pentaho.platform.api.engine.IPentahoSession;
import org.pentaho.platform.api.engine.ISolutionFile;
import org.pentaho.platform.api.engine.ISolutionFilter;
import org.pentaho.platform.api.engine.PentahoAccessControlException;
import org.pentaho.platform.api.repository.ISolutionRepository;
import org.pentaho.platform.api.repository.ISolutionRepositoryService;
import org.pentaho.platform.engine.core.system.PentahoSessionHolder;
import org.pentaho.platform.engine.core.system.PentahoSystem;
import org.pentaho.platform.engine.security.SecurityHelper;

import pt.webdetails.cpf.PluginSettings;

/**
 * Attempt to centralize CTools repository access
 * Facilitate transtion to a post-ISolutionRepository world
 */
@SuppressWarnings("deprecation")
public class RepositoryAccess {
  
  private static Log logger = LogFactory.getLog(RepositoryAccess.class);

  private IPentahoSession userSession;
  
  public String getEncoding(){
    return PluginSettings.ENCODING;
  }

  public enum FileAccess {//TODO:use masks?
    READ,
    EDIT,
    EXECUTE,
    DELETE,
    CREATE, 
    NONE;
    
    public int toResourceAction(){
      switch(this){
        case NONE:
          return IPentahoAclEntry.PERM_NOTHING;
        case CREATE:
          return IPentahoAclEntry.PERM_CREATE;
        case DELETE:
          return IPentahoAclEntry.PERM_DELETE;
        case EDIT:
          return IPentahoAclEntry.PERM_UPDATE;
        case READ:
        case EXECUTE:
        default:
          return IPentahoAclEntry.PERM_EXECUTE;
      }
    }
    
    public static FileAccess parse(String fileAccess){
      try{
        return FileAccess.valueOf(StringUtils.upperCase(fileAccess));
      }
      catch(Exception e){
        return null;
      }
    }
  }
  
  public enum SaveFileStatus {
    //TODO: do we need more than this? use bool?
    OK, 
    FAIL
  }

  protected RepositoryAccess(IPentahoSession userSession) {
    this.userSession = userSession == null ? PentahoSessionHolder.getSession() : userSession;
  }
  
  public static RepositoryAccess getRepository() {
    return new RepositoryAccess(null);
  }

  public static RepositoryAccess getRepository(IPentahoSession userSession) {
    return new RepositoryAccess(userSession);
  }
  
  public SaveFileStatus publishFile(String fileAndPath, String contents, boolean overwrite) throws UnsupportedEncodingException{
    return publishFile(fileAndPath, contents.getBytes(getEncoding()), overwrite);
  }
  
  public SaveFileStatus publishFile(String fileAndPath, byte[] data, boolean overwrite){
    return publishFile(FilenameUtils.getFullPath(fileAndPath), FilenameUtils.getName(fileAndPath), data, overwrite);
  }

  public SaveFileStatus publishFile(String solutionPath, String fileName, byte[] data, boolean overwrite){
    return publishFile(PentahoSystem.getApplicationContext().getSolutionPath(""), solutionPath, fileName, data, overwrite);
  }
  
  public SaveFileStatus publishFile(String baseUrl, String path, String fileName, byte[] data, boolean overwrite) {
    try {
      int status = getSolutionRepository().publish(baseUrl, path, fileName, data, overwrite);
      switch(status){
        case ISolutionRepository.FILE_ADD_SUCCESSFUL:
          return SaveFileStatus.OK;
        case ISolutionRepository.FILE_ADD_FAILED:
        case ISolutionRepository.FILE_ADD_INVALID_PUBLISH_PASSWORD:
        case ISolutionRepository.FILE_ADD_INVALID_USER_CREDENTIALS:
        default:
          return SaveFileStatus.FAIL;
        
      }
    } catch (PentahoAccessControlException e) {
      logger.error(e);
      return SaveFileStatus.FAIL;
    }
  }
  
  public boolean removeFile(String solutionPath){
    if (hasAccess(solutionPath, FileAccess.DELETE))
     return getSolutionRepository().removeSolutionFile(solutionPath);
    return false;
  }
  
  public boolean removeFileIfExists(String solutionPath){
    return !resourceExists(solutionPath) || removeFile(solutionPath);
  }
  
  public boolean resourceExists(String solutionPath){
    return getSolutionRepository().resourceExists(solutionPath, ISolutionRepository.ACTION_EXECUTE);
  }
  
  public boolean createFolder(String solutionFolderPath) throws IOException {
    solutionFolderPath = StringUtils.chomp(solutionFolderPath,"/");//strip trailing / if there
    String folderName = FilenameUtils.getBaseName(solutionFolderPath);
    String folderPath = solutionFolderPath.substring(0, StringUtils.lastIndexOf(solutionFolderPath, folderName));
    return getSolutionRepositoryService().createFolder(userSession, "", folderPath, folderName, "");
  }
  
  public boolean canWrite(String filePath){
    ISolutionRepository solutionRepository = getSolutionRepository();
    //first check read permission
    ISolutionFile file = solutionRepository.getSolutionFile(filePath, ISolutionRepository.ACTION_EXECUTE);
    
    if(resourceExists(filePath))
    {
      return solutionRepository.hasAccess(file,ISolutionRepository.ACTION_UPDATE);
    }
    else 
    {
      return solutionRepository.hasAccess(file,ISolutionRepository.ACTION_CREATE);
    }
  }
  
  public boolean hasAccess(String filePath, FileAccess access) {
    ISolutionFile file = getSolutionRepository().getSolutionFile(filePath, access.toResourceAction());
    if (file == null) {
      return false;
    } else if (SecurityHelper.canHaveACLS(file) && (file.retrieveParent() != null && !StringUtils.startsWith(file.getSolutionPath(), "system"))) {
      // has been checked
      return true;
    }

    else {
      if (!SecurityHelper.canHaveACLS(file)) {
        logger.warn("hasAccess: " + file.getExtension() + " extension not in acl-files.");
        //not declared in pentaho.xml:/pentaho-system/acl-files
        //try parent: folders have acl enabled unless in system
        ISolutionFile parent = file.retrieveParent();
        if (parent instanceof IAclSolutionFile) {
          return SecurityHelper.hasAccess((IAclSolutionFile) parent, access.toResourceAction(), userSession);
        }
      }
      // for(ISolutionFile parent = file.retrieveParent(); parent != null; parent = parent.retrieveParent()){
      //    if(parent instanceof IAclSolutionFile){
      //        return SecurityHelper.hasAccess((IAclSolutionFile) parent,
      //        access.toResourceAction(), userSession);
      //    }
      // }
      logger.warn("hasAccess: Unable to check access control for " + filePath + " using default access settings.");
      // disallow potentially destructive accesses
      // TODO: better than before but far from ideal
      switch (access) {
        case NONE:
        case EXECUTE:
        case READ:
          return true;
        default:
          return SecurityHelper.isPentahoAdministrator(userSession);
      }
    }
  }
  
  

  
  
  private ISolutionRepository getSolutionRepository() {
    return PentahoSystem.get(ISolutionRepository.class, userSession);
  }
  
  private ISolutionRepositoryService getSolutionRepositoryService(){
    return PentahoSystem.get(ISolutionRepositoryService.class, userSession);
  }
  
  public InputStream getResourceInputStream(String filePath) throws FileNotFoundException {
    return getResourceInputStream(filePath, FileAccess.READ);
  }

  public InputStream getResourceInputStream(String filePath, FileAccess fileAccess) throws FileNotFoundException{
    return getResourceInputStream(filePath, fileAccess, true);
  }
  
  public InputStream getResourceInputStream(String filePath, FileAccess fileAccess, boolean getLocalizedResource) throws FileNotFoundException{
    return getSolutionRepository().getResourceInputStream(filePath,getLocalizedResource, fileAccess.toResourceAction());
  }

  public Document getResourceAsDocument(String solutionPath) throws IOException {
    return getResourceAsDocument(solutionPath, FileAccess.READ);
  }
  
  public Document getResourceAsDocument(String solutionPath, FileAccess fileAccess) throws IOException {
    return getSolutionRepository().getResourceAsDocument(solutionPath, fileAccess.toResourceAction());
  }
  
  public Document getFullSolutionTree(FileAccess access, ISolutionFilter filter ){
    return getSolutionRepository().getFullSolutionTree(access.toResourceAction(), filter);
  }

  public String getResourceAsString(String solutionPath) throws IOException {
   return getResourceAsString(solutionPath, FileAccess.READ);
  }
  
  public String getResourceAsString(String solutionPath, FileAccess fileAccess) throws IOException {
    return getSolutionRepository().getResourceAsString(solutionPath, fileAccess.toResourceAction());
  }

  public ISolutionFile getSolutionFile(String solutionPath, FileAccess access) {
    return getSolutionRepository().getSolutionFile(solutionPath, access.toResourceAction());
  }
  
  public ISolutionFile[] listSolutionFiles(String solutionPath, FileAccess access, boolean includeDirs, List<String> extensions) {
    return listSolutionFiles(solutionPath, new ExtensionFilter(extensions, includeDirs, this, access));
  }
  
  public ISolutionFile[] listSolutionFiles(String solutionPath, IFileFilter fileFilter) {
      ISolutionFile[] filesList = null;
    ISolutionFile baseDir = getSolutionFile(solutionPath, FileAccess.READ);

    if(baseDir != null){
        filesList = baseDir.listFiles(fileFilter);
    }
    
    return filesList;
  }
  
  public SaveFileStatus copySolutionFile(String fromFilePath, String toFilePath) throws IOException {
    try{

      return publishFile(toFilePath, IOUtils.toByteArray(getResourceInputStream(fromFilePath)), true);
    } 
    finally {
    }
  }

  public static String getSystemDir(){
    return PentahoSystem.getApplicationContext().getSolutionPath("system");
  }
  
  public static String getSolutionPath(String path){
    return PentahoSystem.getApplicationContext().getSolutionPath(path);
  }
  
  /**
   * 
   */
  public static class ExtensionFilter implements IFileFilter {

    private List<String> extensions;
    private boolean includeDirs = true;
    private ISolutionRepository solutionRepository;
    FileAccess access = FileAccess.READ;
    
    /**
     * 
     * @param extensions list of file extensions to accept
     * @param includeDirs if folders are to be included
     * @param repository
     * @param fileAccess
     */
    public ExtensionFilter(List<String> extensions, boolean includeDirs, RepositoryAccess repository, FileAccess fileAccess){
      
      this.includeDirs = includeDirs;
      if(extensions != null && extensions.size() > 0) {
        this.extensions = extensions;
      }
      solutionRepository = repository.getSolutionRepository();
      access = fileAccess;
    }

    @Override
    public boolean accept(ISolutionFile file) {
      
      boolean include = file.isDirectory()? 
                        includeDirs && Boolean.parseBoolean(solutionRepository.getLocalizedFileProperty(file, "visible", access.toResourceAction())):
                        extensions == null || extensions.contains(file.getExtension());
      
      return include && solutionRepository.hasAccess(file, access.toResourceAction());
    }
    
  }
  
  public ISolutionFile[] getFileList(String dir, final String fileExtensions, String access, IPentahoSession userSession) {
	  
    ArrayList<String> extensionsList = new ArrayList<String>();
    String[] extensions = StringUtils.split(fileExtensions, ".");
    if(extensions != null){
        for(String extension : extensions){
          // For some reason, in 4.5 filebased rep started to report a leading dot in extensions
          // Adding both just to be sure we don't break stuff
          extensionsList.add("." + extension);
          extensionsList.add(extension);
        }
    }
    FileAccess fileAccess = FileAccess.parse(access);
    if(fileAccess == null) fileAccess = FileAccess.READ;
    return RepositoryAccess.getRepository(userSession).listSolutionFiles(dir, fileAccess, true, extensionsList);
  }
  
}

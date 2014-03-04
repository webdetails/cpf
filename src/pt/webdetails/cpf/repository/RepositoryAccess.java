/* This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this file,
 * You can obtain one at http://mozilla.org/MPL/2.0/. */

package pt.webdetails.cpf.repository;

import java.io.ByteArrayInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.Serializable;
import java.io.StringWriter;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.EnumSet;
import java.util.List;
import java.util.Map;

import org.apache.commons.io.FilenameUtils;
import org.apache.commons.io.IOUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.dom4j.Document;
import org.dom4j.DocumentException;
import org.pentaho.platform.api.engine.IAclSolutionFile;
import org.pentaho.platform.api.engine.IAuthorizationPolicy;
import org.pentaho.platform.api.engine.IFileFilter;
import org.pentaho.platform.api.engine.IPentahoAclEntry;
import org.pentaho.platform.api.engine.IPentahoSession;
import org.pentaho.platform.api.engine.ISolutionFile;
import org.pentaho.platform.api.engine.ISolutionFilter;
import org.pentaho.platform.api.engine.PentahoAccessControlException;
import org.pentaho.platform.api.repository.IRepositoryFile;
import org.pentaho.platform.api.repository2.unified.IUnifiedRepository;
import org.pentaho.platform.api.repository2.unified.RepositoryFile;
import org.pentaho.platform.api.repository2.unified.RepositoryFileAce;
import org.pentaho.platform.api.repository2.unified.RepositoryFileAcl;
import org.pentaho.platform.api.repository2.unified.RepositoryFilePermission;
import org.pentaho.platform.api.repository2.unified.RepositoryFileTree;
import org.pentaho.platform.api.repository2.unified.data.simple.SimpleRepositoryFileData;
import org.pentaho.platform.engine.core.system.PentahoSessionHolder;
import org.pentaho.platform.engine.core.system.PentahoSystem;
import org.pentaho.platform.engine.security.SecurityHelper;
import org.pentaho.platform.repository2.unified.fileio.RepositoryFileInputStream;
import org.pentaho.platform.security.policy.rolebased.actions.AdministerSecurityAction;
import org.pentaho.platform.util.xml.dom4j.XmlDom4JHelper;

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

  private String getFullPath(String path){
    String fullPath = FilenameUtils.getFullPath(path);
    return FilenameUtils.normalize(fullPath, true);
  }
  
  public SaveFileStatus publishFile(String fileAndPath, String contents, boolean overwrite) throws UnsupportedEncodingException{
    return publishFile(fileAndPath, contents.getBytes(getEncoding()), overwrite);
  }
  
  public SaveFileStatus publishFile(String fileAndPath, byte[] data, boolean overwrite){
    return publishFile(getFullPath(fileAndPath), FilenameUtils.getName(fileAndPath), data, overwrite);
  }

  public SaveFileStatus publishFile(String solutionPath, String fileName, byte[] data, boolean overwrite){
    return publishFile(PentahoSystem.getApplicationContext().getSolutionPath(""), solutionPath, fileName, data, overwrite);
  }
  
  public SaveFileStatus publishFile(String baseUrl, String path, String fileName, byte[] data, boolean overwrite) {
    try {
      if(resourceExists(path+fileName)){
        final SimpleRepositoryFileData content = new SimpleRepositoryFileData(new ByteArrayInputStream(data), "UTF-8","text/plain");
        RepositoryFile file = getUnifiedRepository().getFile(path+fileName);
        
        file = getUnifiedRepository().updateFile(file, content, null);  
        if(file != null) {
            return SaveFileStatus.OK;
        } else {
            return SaveFileStatus.FAIL;
        }
      } else {
        final SimpleRepositoryFileData content = new SimpleRepositoryFileData(new ByteArrayInputStream(data), "UTF-8","text/plain"); //$NON-NLS-1$ //$NON-NLS-2$
        RepositoryFile parentFolder = getUnifiedRepository().getFile(path);
        RepositoryFile newFile = getUnifiedRepository().createFile(parentFolder.getId(), new RepositoryFile.Builder(fileName)
          .hidden(true).build(), content, null);
        if(newFile != null) {
            return SaveFileStatus.OK;
        } else {
            return SaveFileStatus.FAIL;
        }  
      }      
    } catch (Exception e) {
      logger.error(e);
      return SaveFileStatus.FAIL;
    }
  }
  
  public boolean removeFile(String solutionPath){
     try {
       RepositoryFile repositoryFile = getUnifiedRepository().getFile(solutionPath);
       getUnifiedRepository().deleteFile(repositoryFile.getId(), "deleting a file");
       return true;
     } catch(Exception e) {
       e.printStackTrace();
       return false;
     }
  }
  
  public boolean removeFileIfExists(String solutionPath){
    return !resourceExists(solutionPath) || removeFile(solutionPath);
  }
  
  public boolean resourceExists(String solutionPath){
    RepositoryFile repositoryFile = getUnifiedRepository().getFile(solutionPath);
    return repositoryFile != null;
  }
  
  public boolean createFolder(String solutionFolderPath) {
    try {
      if(resourceExists(solutionFolderPath)){
        logger.debug("CreateFolder: Resource " + solutionFolderPath + " already exists, skipped creation");
        return false;
      } else {
        solutionFolderPath = StringUtils.chomp(solutionFolderPath,"/");//strip trailing / if there
        String folderName = FilenameUtils.getBaseName(solutionFolderPath);
        String folderPath = solutionFolderPath.substring(0, StringUtils.lastIndexOf(solutionFolderPath, folderName));
        RepositoryFile parentFolder = getUnifiedRepository().getFile(folderPath);
        getUnifiedRepository().createFolder(parentFolder.getId(), new RepositoryFile.Builder(folderName).folder(true).build() , "");
        logger.debug("CreateFolder: Resource " + solutionFolderPath+ " created");
        return true;
      }
    } catch(Exception e){
      logger.error("CreateFolder: ", e);
      return false;
    } 
  }
  
  public boolean canWrite(String filePath){
    try {
      RepositoryFile repositoryFile  = getUnifiedRepository().getFile(filePath);
      RepositoryFileAcl acl = getUnifiedRepository().getAcl(repositoryFile.getId());
      List<RepositoryFileAce> aces =  acl.getAces();
      for(RepositoryFileAce ace:aces) {
        EnumSet<RepositoryFilePermission>  permissions = ace.getPermissions();
        if(permissions.contains(RepositoryFilePermission.WRITE) && permissions.contains(RepositoryFilePermission.ACL_MANAGEMENT)) {
          return true;
        } 
      }
      return false;
    } catch(Exception e) {
      e.printStackTrace();
      return false;
    }
  }
  
  public boolean hasAccess(String filePath, FileAccess access) {
    try {
      RepositoryFile repositoryFile  = getUnifiedRepository().getFile(filePath);
      RepositoryFileAcl acl = getUnifiedRepository().getAcl(repositoryFile.getId());
      List<RepositoryFileAce> aces =  acl.getAces();
      switch (access)  {
        case CREATE:
        case DELETE:
        case EDIT: 
          for(RepositoryFileAce ace:aces) {
            EnumSet<RepositoryFilePermission>  permissions = ace.getPermissions();
            if(permissions.contains(RepositoryFilePermission.WRITE) && permissions.contains(RepositoryFilePermission.ACL_MANAGEMENT)) {
              return true;
            } 
          }
          break;
        case EXECUTE:
        case READ:
          for(RepositoryFileAce ace:aces) {
            EnumSet<RepositoryFilePermission>  permissions = ace.getPermissions();
            if(permissions.contains(RepositoryFilePermission.READ) && permissions.contains(RepositoryFilePermission.ACL_MANAGEMENT)) {
              return true;
            } 
          }
          break;          
      }
      
      return false;
    } catch(Exception e) {
      e.printStackTrace();
      return false;
    }

  }
  
  

  private IUnifiedRepository getUnifiedRepository() {
    return PentahoSystem.get(IUnifiedRepository.class, userSession);
  }
  
  public InputStream getResourceInputStream(String filePath) throws FileNotFoundException {
    return getResourceInputStream(filePath, FileAccess.READ);
  }

  public InputStream getResourceInputStream(String filePath, FileAccess fileAccess) throws FileNotFoundException{
    return getResourceInputStream(filePath, fileAccess, true);
  }
  
  public InputStream getResourceInputStream(String filePath, FileAccess fileAccess, boolean getLocalizedResource) throws FileNotFoundException{
    return new RepositoryFileInputStream(getUnifiedRepository().getFile(filePath));
  }

  public Document getResourceAsDocument(String solutionPath) throws IOException {
    return getResourceAsDocument(solutionPath, null);
  }
  
  public Document getResourceAsDocument(String solutionPath, FileAccess fileAccess) throws IOException {
    try {
      return XmlDom4JHelper.getDocFromStream(getResourceInputStream(solutionPath, fileAccess), null);
    } catch (DocumentException e) {
      e.printStackTrace();
      return null;
    }
  }

  public String getResourceAsString(String solutionPath) throws IOException {
   return getResourceAsString(solutionPath, FileAccess.READ);
  }
  
  public String getResourceAsString(String solutionPath, FileAccess fileAccess) throws IOException {
    InputStream is = getResourceInputStream(solutionPath, fileAccess);
    StringWriter writer = new StringWriter();
    IOUtils.copy(is, writer);
    return writer.toString();
  }

  public RepositoryFile getRepositoryFile(String solutionPath, FileAccess access) {
    return getUnifiedRepository().getFile(solutionPath);
  }

  public RepositoryFileTree getRepositoryFileTree(String path, int depth, boolean showHiddenFiles, String filter) {
    RepositoryFileTree tree = getUnifiedRepository().getTree(path, depth, filter, showHiddenFiles); 
    
    List<RepositoryFileTree> files = new ArrayList<RepositoryFileTree>();
    for (RepositoryFileTree file : tree.getChildren()) {
      if(!showHiddenFiles && file.getFile().isHidden()) continue;
      
      Map<String, Serializable> fileMeta = getUnifiedRepository().getFileMetadata(file.getFile().getId());
      boolean isSystemFolder = fileMeta.containsKey(IUnifiedRepository.SYSTEM_FOLDER) ? (Boolean) fileMeta
          .get(IUnifiedRepository.SYSTEM_FOLDER) : false;
      if (isSystemFolder) {
        continue;
      }
      files.add(file);
    }
    tree = new RepositoryFileTree(tree.getFile(), files);
    
    return tree;
  }
  
  public List<RepositoryFile> listSolutionFiles( String solutionPath, boolean includeDirs, List<String> extensions,
                                                boolean showHiddenFiles, final List<RepositoryFile> output ) {
      RepositoryFileTree tree = getUnifiedRepository().getTree( solutionPath, -1, null, showHiddenFiles );
      RepositoryFile root = tree.getFile();
      
      if ( showHiddenFiles || !root.isHidden() ) {
        if ( tree.getChildren().size() > 0 ) {
          for ( RepositoryFileTree element : tree.getChildren() ) {
            listSolutionFiles( element.getFile().getPath(), includeDirs, extensions, showHiddenFiles, output );
          } 
        } else if ( root.isFolder() && includeDirs ) {
          output.add( root );
        } else if ( !root.isFolder() ) { //is a folder
          String name = root.getName();
          String extension = "";
          int lastDot = name.lastIndexOf( '.' );
          if ( lastDot > 0 && lastDot < name.length() ) {
            extension = name.substring( lastDot+1 );
          }

          if (extensions.contains(extension)){
            if(showHiddenFiles){
                output.add(root);
            } else if(!root.isHidden()){
                output.add(root);
            }
          }  
        }
      }
      
      return output;
  }
  
  public List<RepositoryFile> listSolutionFiles(String solutionPath, String fileFilter) {
    return listSolutionFiles(solutionPath, true, null, true, null);
  }
  
  
  public RepositoryFileTree getFullSolutionTree(boolean showHidden ){
    RepositoryFileTree repositoryFileTree = getUnifiedRepository().getTree("/", -1, null, showHidden);
    return repositoryFileTree;
  }
  
  public SaveFileStatus copySolutionFile(String fromFilePath, String toFilePath) throws IOException {
    InputStream in = null;
    try{
      in = getResourceInputStream(fromFilePath);
      return publishFile(toFilePath, IOUtils.toByteArray(in), true);
    } 
    finally {
      IOUtils.closeQuietly(in);
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
    //private ISolutionRepository solutionRepository;
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
      //solutionRepository = repository.getSolutionRepository();
      access = fileAccess;
    }

    @Override
    public boolean accept(ISolutionFile file) {
      
     /* boolean include = file.isDirectory()? 
                        includeDirs && Boolean.parseBoolean(solutionRepository.getLocalizedFileProperty(file, "visible", access.toResourceAction())):
                        extensions == null || extensions.contains(file.getExtension());
      
      return include && solutionRepository.hasAccess(file, access.toResourceAction());*/
        return true;
    }
    
  }
  
  public List<RepositoryFile> getFileList(String dir, final String fileExtensions, String access, IPentahoSession userSession) {
	  
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
    return RepositoryAccess.getRepository(userSession).listSolutionFiles(dir, fileExtensions);
  }
  
}
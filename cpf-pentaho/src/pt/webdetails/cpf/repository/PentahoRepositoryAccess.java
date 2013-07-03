/* This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this file,
 * You can obtain one at http://mozilla.org/MPL/2.0/. */
package pt.webdetails.cpf.repository;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FilenameFilter;
import java.io.IOException;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.net.MalformedURLException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

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
import org.pentaho.platform.api.engine.IPluginManager;
import org.pentaho.platform.api.engine.ISolutionFile;
import org.pentaho.platform.api.engine.ISolutionFilter;
import org.pentaho.platform.api.engine.IUserDetailsRoleListService;
import org.pentaho.platform.api.engine.PentahoAccessControlException;
import org.pentaho.platform.api.repository.ISolutionRepository;
import org.pentaho.platform.api.repository.ISolutionRepositoryService;
import org.pentaho.platform.engine.core.system.PentahoSessionHolder;
import org.pentaho.platform.engine.core.system.PentahoSystem;
import org.pentaho.platform.engine.core.system.UserSession;
import org.pentaho.platform.engine.security.SecurityHelper;
import org.springframework.security.Authentication;
import org.springframework.security.GrantedAuthority;
import org.springframework.security.providers.anonymous.AnonymousAuthenticationToken;

import pt.webdetails.cpf.PluginSettings;
import pt.webdetails.cpf.impl.DefaultRepositoryFile;
import pt.webdetails.cpf.plugin.CorePlugin;
import pt.webdetails.cpf.repository.BaseRepositoryAccess.FileAccess;
import pt.webdetails.cpf.repository.BaseRepositoryAccess.SaveFileStatus;
import pt.webdetails.cpf.repository.PentahoRepositoryAccess.ExtensionFilter;
import pt.webdetails.cpf.session.IUserSession;
import pt.webdetails.cpf.session.PentahoSession;

/**
 * Attempt to centralize CTools repository access Facilitate transtion to a
 * post-ISolutionRepository world
 */
@SuppressWarnings("deprecation")
public class PentahoRepositoryAccess extends BaseRepositoryAccess implements IRepositoryAccess {

  private CorePlugin plugin;

  public PentahoRepositoryAccess() {
    this(null);
  }
  private static Log logger = LogFactory.getLog(PentahoRepositoryAccess.class);

  /*
   * This wiill be used for privileged access to the repository
   */
  private static IPentahoSession getAdminSession() {
    IUserDetailsRoleListService userDetailsRoleListService = PentahoSystem.getUserDetailsRoleListService();
    UserSession session = new UserSession("admin", null, false, null);
    GrantedAuthority[] auths = userDetailsRoleListService.getUserRoleListService().getAllAuthorities();
    Authentication auth = new AnonymousAuthenticationToken("admin", SecurityHelper.SESSION_PRINCIPAL, auths);
    session.setAttribute(SecurityHelper.SESSION_PRINCIPAL, auth);
    session.doStartupActions(null);
    return session;
  }

  @Override
  public String getEncoding() {
    return PluginSettings.ENCODING;
  }

  protected PentahoRepositoryAccess(IPentahoSession userSession) {
    this.userSession = new PentahoSession(userSession == null ? PentahoSessionHolder.getSession() : userSession);
  }

  public static IRepositoryAccess getRepository() {
    return new PentahoRepositoryAccess(null);
  }

  public static IRepositoryAccess getRepository(IPentahoSession userSession) {
    return new PentahoRepositoryAccess(userSession);
  }

  /* (non-Javadoc)
   * @see pt.webdetails.cpf.repository.IRepositoryAccess#publishFile(java.lang.String, java.lang.String, boolean)
   */
  @Override
  public SaveFileStatus publishFile(String fileAndPath, String contents, boolean overwrite) throws UnsupportedEncodingException {
    return publishFile(fileAndPath, contents.getBytes(getEncoding()), overwrite);
  }

  /* (non-Javadoc)
   * @see pt.webdetails.cpf.repository.IRepositoryAccess#publishFile(java.lang.String, byte[], boolean)
   */
  @Override
  public SaveFileStatus publishFile(String fileAndPath, byte[] data, boolean overwrite) {
    return publishFile(FilenameUtils.getFullPath(fileAndPath), FilenameUtils.getName(fileAndPath), data, overwrite);
  }

  /* (non-Javadoc)
   * @see pt.webdetails.cpf.repository.IRepositoryAccess#publishFile(java.lang.String, java.lang.String, byte[], boolean)
   */
  @Override
  public SaveFileStatus publishFile(String solutionPath, String fileName, byte[] data, boolean overwrite) {
    return publishFile(PentahoSystem.getApplicationContext().getSolutionPath(""), solutionPath, fileName, data, overwrite);
  }

  /* (non-Javadoc)
   * @see pt.webdetails.cpf.repository.IRepositoryAccess#publishFile(java.lang.String, java.lang.String, java.lang.String, byte[], boolean)
   */
  @Override
  public SaveFileStatus publishFile(String baseUrl, String path, String fileName, byte[] data, boolean overwrite) {
    try {
      int status = getSolutionRepository().publish(baseUrl, path, fileName, data, overwrite);
      switch (status) {
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

  /* (non-Javadoc)
   * @see pt.webdetails.cpf.repository.IRepositoryAccess#removeFile(java.lang.String)
   */
  @Override
  public boolean removeFile(String solutionPath) {
    if (hasAccess(solutionPath, FileAccess.DELETE)) {
      return getSolutionRepository().removeSolutionFile(solutionPath);
    }
    return false;
  }

  /* (non-Javadoc)
   * @see pt.webdetails.cpf.repository.IRepositoryAccess#removeFileIfExists(java.lang.String)
   */
  @Override
  public boolean removeFileIfExists(String solutionPath) {
    return !resourceExists(solutionPath) || removeFile(solutionPath);
  }

  /* (non-Javadoc)
   * @see pt.webdetails.cpf.repository.IRepositoryAccess#resourceExists(java.lang.String)
   */
  @Override
  public boolean resourceExists(String solutionPath) {
    return getSolutionRepository().resourceExists(solutionPath, ISolutionRepository.ACTION_EXECUTE);
  }

  /* (non-Javadoc)
   * @see pt.webdetails.cpf.repository.IRepositoryAccess#createFolder(java.lang.String)
   */
  @Override
  public boolean createFolder(String solutionFolderPath) throws IOException {
    solutionFolderPath = StringUtils.chomp(solutionFolderPath, "/");//strip trailing / if there
    String folderName = FilenameUtils.getBaseName(solutionFolderPath);
    String folderPath = solutionFolderPath.substring(0, StringUtils.lastIndexOf(solutionFolderPath, folderName));
    return getSolutionRepositoryService().createFolder(((PentahoSession) userSession).getPentahoSession(), "", folderPath, folderName, "");
  }

  /* (non-Javadoc)
   * @see pt.webdetails.cpf.repository.IRepositoryAccess#canWrite(java.lang.String)
   */
  @Override
  public boolean canWrite(String filePath) {
    ISolutionRepository solutionRepository = getSolutionRepository();
    //first check read permission
    ISolutionFile file = solutionRepository.getSolutionFile(filePath, ISolutionRepository.ACTION_EXECUTE);

    if (resourceExists(filePath)) {
      return solutionRepository.hasAccess(file, ISolutionRepository.ACTION_UPDATE);
    } else {
      return solutionRepository.hasAccess(file, ISolutionRepository.ACTION_CREATE);
    }
  }

  /* (non-Javadoc)
   * @see pt.webdetails.cpf.repository.IRepositoryAccess#hasAccess(java.lang.String, pt.webdetails.cpf.repository.RepositoryAccess.FileAccess)
   */
  @Override
  public boolean hasAccess(String filePath, FileAccess access) {
    ISolutionFile file = getSolutionRepository().getSolutionFile(filePath, toResourceAction(access));
    if (file == null) {
      return false;
    } else if (SecurityHelper.canHaveACLS(file) && (file.retrieveParent() != null && !StringUtils.startsWith(file.getSolutionPath(), "system"))) {
      // has been checked
      return true;
    } else {
      if (!SecurityHelper.canHaveACLS(file)) {
        logger.warn("hasAccess: " + file.getExtension() + " extension not in acl-files.");
        //not declared in pentaho.xml:/pentaho-system/acl-files
        //try parent: folders have acl enabled unless in system
        ISolutionFile parent = file.retrieveParent();
        if (parent instanceof IAclSolutionFile) {
          return SecurityHelper.hasAccess((IAclSolutionFile) parent, toResourceAction(access), ((PentahoSession) userSession).getPentahoSession());
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
          return SecurityHelper.isPentahoAdministrator(((PentahoSession) userSession).getPentahoSession());
      }
    }
  }

  private ISolutionRepository getSolutionRepository() {
    return PentahoSystem.get(ISolutionRepository.class, ((PentahoSession) userSession).getPentahoSession());
  }

  private ISolutionRepositoryService getSolutionRepositoryService() {
    return PentahoSystem.get(ISolutionRepositoryService.class, ((PentahoSession) userSession).getPentahoSession());
  }

  @Override
  public InputStream getResourceInputStream(String filePath) throws FileNotFoundException {
    return getResourceInputStream(filePath, FileAccess.READ);
  }

  @Override
  public InputStream getResourceInputStream(String filePath, FileAccess fileAccess) throws FileNotFoundException {
    return getResourceInputStream(filePath, fileAccess, true);
  }

  @Override
  public InputStream getResourceInputStream(String filePath, FileAccess fileAccess, boolean getLocalizedResource) throws FileNotFoundException {
    return getSolutionRepository().getResourceInputStream(filePath, getLocalizedResource, toResourceAction(fileAccess));
  }

  @Override
  public Document getResourceAsDocument(String solutionPath) throws IOException {
    return getResourceAsDocument(solutionPath, FileAccess.READ);
  }

  @Override
  public Document getResourceAsDocument(String solutionPath, FileAccess fileAccess) throws IOException {
    return getSolutionRepository().getResourceAsDocument(solutionPath, toResourceAction(fileAccess));
  }

  public Document getFullSolutionTree(FileAccess access, ISolutionFilter filter) {
    return getSolutionRepository().getFullSolutionTree(toResourceAction(access), filter);
  }

  @Override
  public String getResourceAsString(String solutionPath) throws IOException {
    return getResourceAsString(solutionPath, FileAccess.READ);
  }

  @Override
  public String getResourceAsString(String solutionPath, FileAccess fileAccess) throws IOException {
    return getSolutionRepository().getResourceAsString(solutionPath, toResourceAction(fileAccess));
  }

  public ISolutionFile getSolutionFile(String solutionPath, FileAccess access) {
    return getSolutionRepository().getSolutionFile(solutionPath, toResourceAction(access));
  }

  public ISolutionFile[] listSolutionFiles(String solutionPath, FileAccess access, boolean includeDirs, List<String> extensions) {
    return listSolutionFiles(solutionPath, new ExtensionFilter(extensions, includeDirs, this, access));
  }

  public ISolutionFile[] listSolutionFiles(String solutionPath, IFileFilter fileFilter) {
    ISolutionFile baseDir = getSolutionFile(solutionPath, FileAccess.READ);
    return baseDir.listFiles(fileFilter);
  }

  @Override
  public SaveFileStatus copySolutionFile(String fromFilePath, String toFilePath) throws IOException {
    InputStream in = null;
    try {
      in = getResourceInputStream(fromFilePath);
      return publishFile(toFilePath, IOUtils.toByteArray(in), true);
    } finally {
      IOUtils.closeQuietly(in);
    }
  }

  public static String getSystemDir() {
    return PentahoSystem.getApplicationContext().getSolutionPath("system");
  }

  @Override
  public String getSolutionPath(String path) {
    return PentahoSystem.getApplicationContext().getSolutionPath(path);
  }

  public static String getPentahoSolutionPath(String path) {
    return PentahoSystem.getApplicationContext().getSolutionPath(path);
  }

  @Override
  public void setUserSession(IUserSession userSession) {
    this.userSession = userSession;
  }

  @Override
  public IRepositoryFile getRepositoryFile(String path, FileAccess fileAccess) {
	ISolutionFile file = getSolutionRepository().getSolutionFile(path, fileAccess.ordinal());
    return file != null ? new PentahoRepositoryFile(file) : null;
  }


  private IPentahoSession getPentahoSession(){
    IPentahoSession session = null;
    if (userSession != null)
      session = ((PentahoSession) userSession).getPentahoSession();
    else
      session = PentahoSessionHolder.getSession();
    return session;
  }

  @Override
  public String getJqueryFileTree(String dir, String fileExtensions, String access) {

    return RepositoryFileExplorer.toJQueryFileTree(dir, getFileList(dir, fileExtensions, access, getPentahoSession()));
  }



  @Override
  public String getJSON(String dir, String fileExtensions, String access) {
    return RepositoryFileExplorer.toJSON(dir, getFileList(dir, fileExtensions, access, getPentahoSession()));
  }


  @Override
  public IRepositoryFile[] getSettingsFileTree(final String dir, final String fileExtensions, FileAccess access) {
    IPluginManager pluginManager = PentahoSystem.get(IPluginManager.class, getAdminSession());
    URL resourceUrl = pluginManager.getClassLoader(plugin.getName()).getResource(dir);
    File f;
    try {
      f = new File(resourceUrl.toURI());
    } catch (URISyntaxException ex) {
      logger.error("Error while opening settings file with url " + resourceUrl);
      return null;
    }

    File[] fileArray = f.listFiles(new FilenameFilter() {
      @Override
      public boolean accept(File file, String name) {
        return name.endsWith("." + fileExtensions);
      }
    });

    IRepositoryFile[] result = new IRepositoryFile[fileArray.length];
    int i = 0;
    for (File resultFile : fileArray)
      result[i++] = new DefaultRepositoryFile(resultFile);

    return result;

  }

  @Override
  public IRepositoryFile[] getPluginFiles(String baseDir, FileAccess fa) {
    final IRepositoryFileFilter irff = plugin.getPluginFileFilter();
    IFileFilter fileFilter = new IFileFilter() {

      @Override
      public boolean accept(ISolutionFile isf) {
        return irff.accept(new PentahoRepositoryFile(isf));
      }
    };
    ISolutionFile[] files = listSolutionFiles(baseDir, fileFilter);

    IRepositoryFile[] result = new IRepositoryFile[files.length];
    int i = 0;

    for (ISolutionFile f : files) {
      result[i++] = new PentahoRepositoryFile(f);
    }

    return result;

  }

  @Override
  public IRepositoryFile getSettingsFile(String fileName, FileAccess fa) {
    //Get plugin dir
    IPluginManager pluginManager = PentahoSystem.get(IPluginManager.class, getAdminSession());
    URL resourceUrl = pluginManager.getClassLoader(plugin.getName()).getResource(fileName);
    File f;
    try {
      f = new File(resourceUrl.toURI());
    } catch (URISyntaxException ex) {
      logger.error("Error while opening settings file with url " + resourceUrl);
      return null;
    }
    return new DefaultRepositoryFile(f);

  }

  @Override
  public String getSettingsResourceAsString(String fileName) throws IOException {
    IPluginManager pluginManager = PentahoSystem.get(IPluginManager.class, getAdminSession());
    URL resourceUrl = pluginManager.getClassLoader(plugin.getName()).getResource(fileName);
    return resourceUrl.getContent().toString();
  }

  @Override
  public void setPlugin(CorePlugin plugin) {
    this.plugin = plugin;
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
    public ExtensionFilter(List<String> extensions, boolean includeDirs, PentahoRepositoryAccess repository, FileAccess fileAccess) {

      this.includeDirs = includeDirs;
      if (extensions != null && extensions.size() > 0) {
        this.extensions = extensions;
      }
      solutionRepository = repository.getSolutionRepository();
      access = fileAccess;
    }

    @Override
    public boolean accept(ISolutionFile file) {

      boolean include = file.isDirectory()
              ? includeDirs && Boolean.parseBoolean(solutionRepository.getLocalizedFileProperty(file, "visible", toResourceAction(access)))
              : extensions == null || extensions.contains(file.getExtension());

      return include && solutionRepository.hasAccess(file, toResourceAction(access));
    }
  }

  public IRepositoryFile[] getFileList(String dir, final String fileExtensions, String access, IPentahoSession userSession) {

    ArrayList<String> extensionsList = new ArrayList<String>();
    String[] extensions = StringUtils.split(fileExtensions, ".");
    if (extensions != null) {
      for (String extension : extensions) {
        // For some reason, in 4.5 filebased rep started to report a leading dot in extensions
        // Adding both just to be sure we don't break stuff
        extensionsList.add("." + extension);
        extensionsList.add(extension);
      }
    }
    FileAccess fileAccess = FileAccess.parse(access);
    if (fileAccess == null) {
      fileAccess = FileAccess.READ;
    }

    List<IRepositoryFile> list = new ArrayList<IRepositoryFile>();

    ISolutionFile[] fileList = ((PentahoRepositoryAccess) PentahoRepositoryAccess.getRepository(userSession)).listSolutionFiles(dir, fileAccess, true, extensionsList);

    for (ISolutionFile solutionFile : fileList) {
      list.add(new PentahoRepositoryFile(solutionFile));
    }

    return list.toArray(new IRepositoryFile[list.size()]);
  }

  @Override
  public IRepositoryFile[] listRepositoryFiles(final IRepositoryFileFilter filter) {        
    IFileFilter fileFilter = new IFileFilter () {
      @Override
      public boolean accept(ISolutionFile isf) {
        return filter.accept(new PentahoRepositoryFile(isf));
      }
    };
    ISolutionFile[] files = listSolutionFiles("/", fileFilter);

    IRepositoryFile[] result = new IRepositoryFile[files.length];
    int i = 0;

    for (ISolutionFile f : files) {
      result[i++] = new PentahoRepositoryFile(f);
    }
    
    return result;

  }

  public static int toResourceAction(FileAccess f) {
    switch (f) {
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
}

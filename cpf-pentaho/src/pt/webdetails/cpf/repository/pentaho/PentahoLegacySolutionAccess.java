package pt.webdetails.cpf.repository.pentaho;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.io.FilenameUtils;
import org.apache.commons.io.IOUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.pentaho.platform.api.engine.IAclSolutionFile;
import org.pentaho.platform.api.engine.IFileFilter;
import org.pentaho.platform.api.engine.IPentahoAclEntry;
import org.pentaho.platform.api.engine.IPentahoSession;
import org.pentaho.platform.api.engine.ISolutionFile;
import org.pentaho.platform.api.engine.PentahoAccessControlException;
import org.pentaho.platform.api.repository.ISolutionRepository;
import org.pentaho.platform.api.repository.ISolutionRepositoryService;
import org.pentaho.platform.engine.core.system.PentahoSystem;
import org.pentaho.platform.engine.security.SecurityHelper;

import pt.webdetails.cpf.repository.api.FileAccess;
import pt.webdetails.cpf.repository.api.IBasicFile;
import pt.webdetails.cpf.repository.api.IBasicFileFilter;
import pt.webdetails.cpf.repository.api.IUserContentAccess;
import pt.webdetails.cpf.repository.util.RepositoryHelper;
import pt.webdetails.cpf.session.PentahoSession;

@SuppressWarnings("deprecation")
public class PentahoLegacySolutionAccess implements IUserContentAccess {

  private static Log logger = LogFactory.getLog(PentahoLegacySolutionAccess.class);

  
  private ISolutionRepository repository;
  private ISolutionRepositoryService repositoryService;
  private String basePath;
  private IPentahoSession userSession;
  
  public PentahoLegacySolutionAccess(String basePath, IPentahoSession session) {
    this.basePath = basePath;
    this.repository = PentahoSystem.get(ISolutionRepository.class, session);
    this.repositoryService = PentahoSystem.get(ISolutionRepositoryService.class, session);
    this.userSession = session;
  }

  @Override
  public boolean saveFile(String path, InputStream contents) {
    try {
      path =  getPath(path);
      int status = getRepository().publish("",FilenameUtils.getFullPath(path), FilenameUtils.getName(path), IOUtils.toByteArray(contents), true);
      switch (status) {
          case ISolutionRepository.FILE_ADD_SUCCESSFUL:
              return true;
          case ISolutionRepository.FILE_ADD_FAILED:
          case ISolutionRepository.FILE_ADD_INVALID_PUBLISH_PASSWORD:
          case ISolutionRepository.FILE_ADD_INVALID_USER_CREDENTIALS:
          default:
              return false;
      }
    } catch (PentahoAccessControlException e) {
        logger.error(e);
        return false;
    } catch (IOException e) {
      logger.error(e);
      return false;
    }
  }

  protected String getPath(String path) {
    return RepositoryHelper.appendPath(basePath, path);
  }
  protected ISolutionRepository getRepository() {
    return repository;
  }
  protected ISolutionRepositoryService getSolutionRepositoryService() {
    return PentahoSystem.get(ISolutionRepositoryService.class, ((PentahoSession) userSession).getPentahoSession());
  }
  protected ISolutionFile getRepositoryFile(String path) {
    return getRepository().getSolutionFile(getPath(path), IPentahoAclEntry.PERM_EXECUTE);
  }

  @Override
  public boolean copyFile(String pathFrom, String pathTo) {
    try {
      saveFile(pathTo, getFileInputStream(pathFrom));
      return true;
    } catch (IOException e) {
      logger.error(e);
      return false;
    }
  }

  @Override
  public boolean deleteFile(String pathFrom) {
    return getRepository().removeSolutionFile(getPath(pathFrom));
  }

  @Override
  public boolean createFolder(String path) {
    path = StringUtils.chomp(path, "/");//strip trailing / if there
    String folderName = FilenameUtils.getBaseName(path);
    String folderPath = path.substring(0, StringUtils.lastIndexOf(path, folderName));

    try {
      repositoryService.createFolder(userSession, "", folderPath, folderName, "");
    } catch (IOException ex){
      logger.error(ex);
      return false;
    }

    return true;
}

  @Override
  public InputStream getFileInputStream(String path) throws IOException {
    return getRepository().getResourceInputStream(getPath(path), true, IPentahoAclEntry.PERM_EXECUTE);
  }

  @Override
  public boolean fileExists(String path) {
    return getRepository().resourceExists(getPath(path), IPentahoAclEntry.PERM_EXECUTE);
  }

  @Override
  public long getLastModified(String path) {
    return getRepositoryFile(path).getLastModified();
  }


  public List<IBasicFile> listFiles(String path, final IBasicFileFilter filter) {
    return listFiles(path, filter, -1);
  }

  public List<IBasicFile> listFiles(String path, final IBasicFileFilter filter, int maxDepth) {
      //FIXME make recursive, +includeDirs
      path = getPath(path);
      IFileFilter fileFilter = new IFileFilter() {
          @Override
          public boolean accept(ISolutionFile isf) {
              return filter.accept(asBasicFile(isf));
          }
      };
      ISolutionFile baseDir = getRepository().getSolutionFile(path, toResourceAction(FileAccess.READ));
      ISolutionFile[] files = baseDir.listFiles(fileFilter);

      List<IBasicFile> result = new ArrayList<IBasicFile>(files.length);
      for (ISolutionFile file : files) {
          result.add(asBasicFile(file));
      }
      return result;
  }

  @Override
  public IBasicFile fetchFile(String path) {
    return asBasicFile(getRepository().getFileByPath(getPath(path)));
  }

  private IBasicFile asBasicFile(final ISolutionFile file) {
    return new IBasicFile () {

      public InputStream getContents() throws IOException {
        return new ByteArrayInputStream(file.getData());
      }

      public String getExtension() {
        return StringUtils.lowerCase(FilenameUtils.getExtension(file.getFileName()));
      }

      public String getFullPath() {
        return StringUtils.replace(file.getFullPath(), "\\", "/");
      }

      public String getName() {
        return file.getFileName();
      }

      public String getPath() {
        //XXX wrong
        return file.getSolutionPath();
      }
      
    };
  }

  @Override
  public boolean hasAccess(String filePath, FileAccess access) {
    filePath = getPath(filePath);
    ISolutionFile file = getRepository().getSolutionFile(filePath, toResourceAction(access));
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
                return SecurityHelper.hasAccess((IAclSolutionFile) parent, toResourceAction(access), userSession);
            }
        }
        logger.warn("hasAccess: Unable to check access control for " + filePath + " using default access settings.");
        if (StringUtils.startsWith(file.getSolutionPath(), "system/")) {
          return SecurityHelper.isPentahoAdministrator(userSession);
        }
        switch (access) {
            case EXECUTE:
            case READ:
                return true;
            default:
                return SecurityHelper.isPentahoAdministrator(userSession);
        }
    }
  }
  private static int toResourceAction(FileAccess access) {
    switch (access) {
        case DELETE:
            return IPentahoAclEntry.PERM_DELETE;
        case WRITE:
            return IPentahoAclEntry.PERM_UPDATE;
        case READ:
        case EXECUTE:
        default:
            return IPentahoAclEntry.PERM_EXECUTE;
    }
  }
}

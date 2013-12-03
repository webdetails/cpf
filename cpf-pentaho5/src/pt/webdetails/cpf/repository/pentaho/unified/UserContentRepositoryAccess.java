package pt.webdetails.cpf.repository.pentaho.unified;

import java.util.EnumSet;

import org.apache.commons.io.FilenameUtils;
import org.apache.commons.lang.StringUtils;
import org.pentaho.platform.api.engine.IPentahoSession;
import org.pentaho.platform.api.repository2.unified.IUnifiedRepository;
import org.pentaho.platform.api.repository2.unified.RepositoryFilePermission;
import org.pentaho.platform.engine.core.system.PentahoSystem;

import pt.webdetails.cpf.Util;
import pt.webdetails.cpf.api.IUserContentAccessExtended;
import pt.webdetails.cpf.repository.api.FileAccess;
import pt.webdetails.cpf.repository.api.IUserContentAccess;

/**
 * {@link IUserContentAccess} implementation for {@link IUnifiedRepository}
 */
public class UserContentRepositoryAccess extends UnifiedRepositoryAccess implements IUserContentAccessExtended {

  private IUnifiedRepository repository;

  private static final String DEFAULT_USER_DIR = "/";

  /**
   * 
   * @param session User session. If null defaults to user that initiated current thread.
   */
  public UserContentRepositoryAccess(IPentahoSession session) {
    this(session, DEFAULT_USER_DIR);
  }
  
  public UserContentRepositoryAccess(IPentahoSession session, String startPath) {
    repository = PentahoSystem.get(IUnifiedRepository.class, session);
    basePath = StringUtils.isEmpty( startPath ) ? DEFAULT_USER_DIR : startPath;
  }

  @Override
  protected IUnifiedRepository getRepository() {
    return repository;
  }

  public boolean hasAccess(String path, FileAccess access) {
    String normalizedPath = Util.joinPath(basePath,path);
    return getRepository().hasAccess( normalizedPath, toRepositoryFilePermissions(access) );
  }

  protected static EnumSet<RepositoryFilePermission> toRepositoryFilePermissions(FileAccess access) {
    switch (access) {
      case READ:
      case EXECUTE:
        return EnumSet.of(RepositoryFilePermission.READ);
      case WRITE:
        return EnumSet.of(RepositoryFilePermission.WRITE);
      case DELETE:
        return EnumSet.of(RepositoryFilePermission.DELETE);
      default:
        return EnumSet.of(RepositoryFilePermission.ALL);
    }
  }

}

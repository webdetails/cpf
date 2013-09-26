package pt.webdetails.cpf.repository.pentaho.unified;

import java.util.EnumSet;

import org.pentaho.platform.api.engine.IPentahoSession;
import org.pentaho.platform.api.repository2.unified.IUnifiedRepository;
import org.pentaho.platform.api.repository2.unified.RepositoryFilePermission;
import org.pentaho.platform.engine.core.system.PentahoSystem;

import pt.webdetails.cpf.repository.api.FileAccess;
import pt.webdetails.cpf.repository.api.IUserContentAccess;

/**
 * {@link IUserContentAccess} implementation for {@link IUnifiedRepository}
 */
public class UserContentRepositoryAccess extends UnifiedRepositoryAccess implements IUserContentAccess {

  private IUnifiedRepository repository;

  /**
   * 
   * @param session User session. If null defaults to user that initiated current thread.
   */
  public UserContentRepositoryAccess(IPentahoSession session) {
    this(session, "/");
  }
  
  public UserContentRepositoryAccess(IPentahoSession session, String startPath) {
    repository = PentahoSystem.get(IUnifiedRepository.class, session);
    basePath = startPath;
  }

  @Override
  protected IUnifiedRepository getRepository() {
    return repository;
  }

  public boolean hasAccess(String path, FileAccess access) {
    return getRepository().hasAccess(path, toRepositoryFilePermissions(access));
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

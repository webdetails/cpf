package pt.webdetails.cpf.repository.impl;

import org.apache.commons.io.FilenameUtils;
import pt.webdetails.cpf.repository.api.IReadAccess;
import pt.webdetails.cpf.repository.util.RepositoryHelper;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;


/**
 * Simple incomplete {@link URL}/{@link ClassLoader}-based implementation of {@link IReadAccess}
 */
public abstract class ClassLoaderResourceAccess implements IReadAccess {

  protected final String basePath;
  protected final ClassLoader classLoader;

  public ClassLoaderResourceAccess( ClassLoader classLoader, String basePath ) {
    this.classLoader = classLoader;
    this.basePath = basePath == null ? "" : basePath;
  }

  @Override
  public InputStream getFileInputStream( String path ) throws IOException {
    path = FilenameUtils.normalize( RepositoryHelper.appendPath( basePath, path ) );
    URL url = RepositoryHelper.getClosestResource( classLoader, path );
    if ( url != null ) {
      return url.openStream();
    } else {
      return null;
    }
  }

  @Override
  public boolean fileExists( String path ) {
    path = FilenameUtils.normalize( RepositoryHelper.appendPath( basePath, path ) );
    return RepositoryHelper.getClosestResource( classLoader, path ) != null;
  }

  @Override
  public long getLastModified( String path ) {
    URL url = RepositoryHelper.getClosestResource( classLoader, path );
    if ( url != null ) {
      File file = new File( url.getPath() );
      return file.lastModified();
    }
    return 0L; //File#lastModified default
  }

}

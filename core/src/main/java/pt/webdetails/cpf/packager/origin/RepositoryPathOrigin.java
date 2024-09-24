package pt.webdetails.cpf.packager.origin;

import pt.webdetails.cpf.context.api.IUrlProvider;
import pt.webdetails.cpf.repository.api.IContentAccessFactory;
import pt.webdetails.cpf.repository.api.IReadAccess;
import pt.webdetails.cpf.repository.util.RepositoryHelper;

public class RepositoryPathOrigin extends PathOrigin {

  public RepositoryPathOrigin( String basePath ) {
    super( basePath );
  }

  @Override
  public IReadAccess getReader( IContentAccessFactory factory ) {
    return factory.getUserContentAccess( basePath );
  }

  @Override
  public String getUrl( String localPath, IUrlProvider urlProvider ) {
    // want it resolving to: /pentaho/content/pentaho-cdf-dd/res/<basePath>/<localPath>
    String relPath = RepositoryHelper.joinPaths( basePath, localPath );
    return urlProvider.getRepositoryUrl( relPath );
  }
}

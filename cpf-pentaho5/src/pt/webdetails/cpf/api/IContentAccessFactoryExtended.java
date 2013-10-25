package pt.webdetails.cpf.api;

import pt.webdetails.cpf.repository.api.IContentAccessFactory;

/**
 * Minimal repository access provider for basic plugin needs 
 */
public interface IContentAccessFactoryExtended extends IContentAccessFactory {

  /**
   * @param basePath (optional) all subsequent paths will be relative to this
   * @return {@link IUserContentAccess} for user repository access
   */
  IUserContentAccessExtended getUserContentAccess(String basePath);

}

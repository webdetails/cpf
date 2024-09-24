/*! ******************************************************************************
 *
 * Pentaho
 *
 * Copyright (C) 2024 by Hitachi Vantara, LLC : http://www.pentaho.com
 *
 * Use of this software is governed by the Business Source License included
 * in the LICENSE.TXT file.
 *
 * Change Date: 2028-08-13
 ******************************************************************************/
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
  IUserContentAccessExtended getUserContentAccess( String basePath );

}

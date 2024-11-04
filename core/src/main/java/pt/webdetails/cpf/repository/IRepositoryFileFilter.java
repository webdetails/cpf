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


package pt.webdetails.cpf.repository;

/**
 * @author dfscm
 * @deprecated replaced by {@link pt.webdetails.cpf.repository.api.IBasicFileFilter}
 */
public interface IRepositoryFileFilter {
  public boolean accept( IRepositoryFile isf );
}

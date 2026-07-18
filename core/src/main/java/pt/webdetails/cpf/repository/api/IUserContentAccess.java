/*! ******************************************************************************
 *
 * Pentaho
 *
 * Copyright (C) 2024 - 2026 by Pentaho Canada Inc. : http://www.pentaho.com
 *
 * Use of this software is governed by the Business Source License included
 * in the LICENSE.TXT file.
 *
 * Change Date: 2030-06-15
 ******************************************************************************/


package pt.webdetails.cpf.repository.api;

/**
 * For user interaction with the repository. Should always check permissions.<br>
 *
 * @see {@link IReadAccess} {@link IRWAccess} {@link IACAccess}
 */
public interface IUserContentAccess extends IRWAccess, IACAccess {

}

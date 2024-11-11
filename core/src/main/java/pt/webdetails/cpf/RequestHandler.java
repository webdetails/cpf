/*! ******************************************************************************
 *
 * Pentaho
 *
 * Copyright (C) 2024 by Hitachi Vantara, LLC : http://www.pentaho.com
 *
 * Use of this software is governed by the Business Source License included
 * in the LICENSE.TXT file.
 *
 * Change Date: 2029-07-20
 ******************************************************************************/


package pt.webdetails.cpf;

import pt.webdetails.cpf.http.ICommonParameterProvider;

import java.io.OutputStream;

/**
 * @author pdpi
 */
public interface RequestHandler {
  public void call( OutputStream out, ICommonParameterProvider pathParams, ICommonParameterProvider requestParams );
}

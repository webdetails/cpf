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
package pt.webdetails.cpf.plugincall.api;

import java.io.InputStream;
import java.util.Map;

public interface IPluginCall {

  /**
   * Equivalent to run(params) + toString(getResult())
   */
  String call( Map<String, String[]> params ) throws Exception;

  void run( Map<String, String[]> params ) throws Exception;

  InputStream getResult();

  boolean exists(); //TODO: baah
}

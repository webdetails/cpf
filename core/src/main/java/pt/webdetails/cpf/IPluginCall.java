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


package pt.webdetails.cpf;

import pt.webdetails.cpf.plugin.CorePlugin;

import java.util.Map;

/**
 * @deprecated gonna break this one too
 */
public interface IPluginCall {

  public static final String DEFAULT_ENCODING = "UTF-8";

  public void init( CorePlugin plugin, String method, Map<String, Object> params );

  public String getMethod();

  public void setMethod( String method );

  public String call();

}

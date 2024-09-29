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


package pt.webdetails.cpf.http;

import java.math.BigDecimal;
import java.util.Date;
import java.util.Iterator;
import java.util.Map;

public interface ICommonParameterProvider {

  public void put( String name, Object value );

  public String getStringParameter( String name, String defaultValue );

  public long getLongParameter( String name, long defaultValue );

  public Date getDateParameter( String name, Date defaultValue );

  public BigDecimal getDecimalParameter( String name, BigDecimal defaultValue );

  public Object[] getArrayParameter( String name, Object[] defaultValue );

  public String[] getStringArrayParameter( String name,
                                           String[] defaultValue );

  public Map<String, Object> getParameters();

  public Iterator<String> getParameterNames();

  public Object getParameter( String name );

  public boolean hasParameter( String name );

}

/*!
* Copyright 2002 - 2017 Webdetails, a Hitachi Vantara company.  All rights reserved.
* 
* This software was developed by Webdetails and is provided under the terms
* of the Mozilla Public License, Version 2.0, or any later version. You may not use
* this file except in compliance with the license. If you need a copy of the license,
* please go to  http://mozilla.org/MPL/2.0/. The Initial Developer is Webdetails.
*
* Software distributed under the Mozilla Public License is distributed on an "AS IS"
* basis, WITHOUT WARRANTY OF ANY KIND, either express or  implied. Please refer to
* the license for the specific language governing your rights and limitations.
*/

package pt.webdetails.cpf.http;

import java.math.BigDecimal;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

public class CommonParameterProvider implements ICommonParameterProvider {

  @SuppressWarnings( "unused" )
  private Map<String, Object> params;

  public CommonParameterProvider() {
    params = new HashMap<String, Object>();
  }

  public void put( String name, Object value ) {
    params.put( name, value );
  }

  public String getStringParameter( String name, String defaultValue ) {
    if ( params.containsKey( name ) && params.get( name ) != null ) {
      return (String) params.get( name );
    } else {
      return defaultValue;
    }
  }

  public long getLongParameter( String name, long defaultValue ) {
    // TODO Auto-generated method stub
    return 0;
  }

  public Date getDateParameter( String name, Date defaultValue ) {
    // TODO Auto-generated method stub
    return null;
  }

  public BigDecimal getDecimalParameter( String name, BigDecimal defaultValue ) {
    // TODO Auto-generated method stub
    return null;
  }

  public Object[] getArrayParameter( String name, Object[] defaultValue ) {
    if ( params.containsKey( name ) && params.get( name ) != null ) {
      return (Object[]) params.get( name );
    } else {
      return defaultValue;
    }
  }

  public String[] getStringArrayParameter( String name, String[] defaultValue ) {
    if ( params.containsKey( name ) && params.get( name ) != null ) {
      return (String[]) params.get( name );
    } else {
      return defaultValue;
    }

  }

  public Iterator<String> getParameterNames() {
    return params.keySet().iterator();
  }

  public Object getParameter( String name ) {
    return params.get( name );
  }

  public boolean hasParameter( String name ) {
    return params.containsKey( name );
  }

  @Override
  public Map<String, Object> getParameters() {
    return params;
  }
}

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

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
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import org.pentaho.platform.api.engine.IParameterProvider;

/**
 * @deprecated
 */
public class PentahoParameterProvider implements ICommonParameterProvider {
  private final IParameterProvider provider;

  
  public PentahoParameterProvider(IParameterProvider originalProvider) {
    this.provider = originalProvider;
  }
  
  @Override
  public void put(String string, Object o) {
    throw new UnsupportedOperationException("Not supported yet.");
  }

  @Override
  public String getStringParameter(String string, String string1) {
    return provider.getStringParameter(string, string1);
  }

  @Override
  public long getLongParameter(String string, long l) {
    return provider.getLongParameter(string, l);
  }

  @Override
  public Date getDateParameter(String string, Date date) {
    return provider.getDateParameter(string, date);
  }

  @Override
  public BigDecimal getDecimalParameter(String string, BigDecimal bd) {
    return provider.getDecimalParameter(string, bd);
  }

  @Override
  public Object[] getArrayParameter(String string, Object[] os) {
    return provider.getArrayParameter(string, os);
  }

  @Override
  public String[] getStringArrayParameter(String string, String[] strings) {
    return provider.getStringArrayParameter(string, strings);
  }

  @Override
  public Iterator<String> getParameterNames() {
    return provider.getParameterNames();
  }

  @Override
  public Object getParameter(String string) {
    return provider.getParameter(string);
  }

  @Override
  public boolean hasParameter(String string) {
    return provider.hasParameter(string);
  }


  public Map<String, Object> getParameters() {
	
	  Map<String,Object> params = new HashMap<String,Object>();
	  
	  if(provider == null){
		  return params;
	  }
	  
	@SuppressWarnings("unchecked")
	Iterator<String> names = provider.getParameterNames();

	  while (names.hasNext()) {
	      String name = names.next();
	      Object value = provider.getParameter(name);
	      params.put(name, value);
	  }

    return params;
  }
  
}

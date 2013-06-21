/* This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this file,
 * You can obtain one at http://mozilla.org/MPL/2.0/. */
package pt.webdetails.cpf.http;

import java.math.BigDecimal;
import java.util.Date;
import java.util.Iterator;
import org.pentaho.platform.api.engine.IParameterProvider;

/**
 * @deprecated only advantage here is to get getParameterNames to use generics. not worth it
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
  
}

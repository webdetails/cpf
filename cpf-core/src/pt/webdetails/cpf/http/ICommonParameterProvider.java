/* This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this file,
 * You can obtain one at http://mozilla.org/MPL/2.0/. */

package pt.webdetails.cpf.http;

import java.math.BigDecimal;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

public interface ICommonParameterProvider {
	
	public void put(String name, Object value);

	public String getStringParameter(String name, String defaultValue);

	public long getLongParameter(String name, long defaultValue);

	public Date getDateParameter(String name, Date defaultValue);

	public BigDecimal getDecimalParameter(String name, BigDecimal defaultValue);

	public Object[] getArrayParameter(String name, Object[] defaultValue);

	public String[] getStringArrayParameter(String name,
			String[] defaultValue);
	
	public Map<String,Object> getParameters();

	public Iterator<String> getParameterNames();

	public Object getParameter(String name);

	public boolean hasParameter(String name);

}
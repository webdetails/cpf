/* This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this file,
 * You can obtain one at http://mozilla.org/MPL/2.0/. */

package pt.webdetails.cpf.http;

import java.math.BigDecimal;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

public class CommonParameterProvider implements ICommonParameterProvider {

	@SuppressWarnings("unused")
	private Map<String,Object> params;

	public CommonParameterProvider() {
		params = new HashMap<String,Object>();
	}

	public void put(String name, Object value) {
		params.put(name, value);
	}

	public String getStringParameter(String name, String defaultValue) {
		if(params.containsKey(name)&&params.get(name)!=null){
			return (String) params.get(name);
		}else{
			return defaultValue;
		}
	}

	public long getLongParameter(String name, long defaultValue) {
		// TODO Auto-generated method stub
		return 0;
	}

	public Date getDateParameter(String name, Date defaultValue) {
		// TODO Auto-generated method stub
		return null;
	}

	public BigDecimal getDecimalParameter(String name, BigDecimal defaultValue) {
		// TODO Auto-generated method stub
		return null;
	}

	public Object[] getArrayParameter(String name, Object[] defaultValue) {
		// TODO Auto-generated method stub
		return null;
	}

	public String[] getStringArrayParameter(String name, String[] defaultValue) {
		// TODO Auto-generated method stub
		return null;
	}

	public Iterator<String> getParameterNames() {
		return params.keySet().iterator();
	}

	public Object getParameter(String name) {
		return params.get(name);
	}

	public boolean hasParameter(String name) {
		return params.containsKey(name);
	}

	@Override
	public Map<String, Object> getParameters() {
		return params;
	}
}

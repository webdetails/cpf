package pt.webdetails.cpf.impl;

import pt.webdetails.cpf.session.ISessionUtils;
import pt.webdetails.cpf.session.IUserSession;

public class DummySessionUtils implements ISessionUtils {

	@Override
	public IUserSession getCurrentSession() {
		return new IUserSession() {

			@Override
			public boolean isAdministrator() {
				return false;
			}

			@Override
			public String getUserName() {
				return "dummy";
			}

			@Override
			public String[] getAuthorities() {
				return new String[0];
			}

			@Override
			public Object getParameter(String key) {
				// TODO Auto-generated method stub
				return null;
			}

			@Override
			public String getStringParameter(String key) {
				// TODO Auto-generated method stub
				return null;
			}

			@Override
			public void setParameter(String key, Object value) {
				// TODO Auto-generated method stub
				
			}
		};
	}

	@Override
	public String[] getSystemPrincipals() {
		return new String[0];
	}

	@Override
	public String[] getSystemAuthorities() {
		return new String[0];
	}

}

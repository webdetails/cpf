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
		};
	}

}

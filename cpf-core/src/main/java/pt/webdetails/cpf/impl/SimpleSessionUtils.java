package pt.webdetails.cpf.impl;

import pt.webdetails.cpf.session.ISessionUtils;
import pt.webdetails.cpf.session.IUserSession;

public class SimpleSessionUtils implements ISessionUtils {

	
	private String[] principals;

	private String[] authorities;
	private IUserSession session;

	public SimpleSessionUtils() {};
	
	public SimpleSessionUtils(IUserSession session, String[] principals, String[] authorities) {
		this.session = session;
		this.principals = principals;
		this.authorities = authorities;
	}
	
	@Override
	public IUserSession getCurrentSession() {
		return this.session;
	}

	@Override
	public String[] getSystemPrincipals() {
		return this.principals;
	}

	@Override
	public String[] getSystemAuthorities() {
		return this.authorities;
	}

	/**
	 * @param principals the principals to set
	 */
	public void setSystemPrincipals(String[] principals) {
		this.principals = principals;
	}

	/**
	 * @param authorities the authorities to set
	 */
	public void setSystemAuthorities(String[] authorities) {
		this.authorities = authorities;
	}

	/**
	 * @param session the session to set
	 */
	public void setSession(IUserSession session) {
		this.session = session;
	}
}

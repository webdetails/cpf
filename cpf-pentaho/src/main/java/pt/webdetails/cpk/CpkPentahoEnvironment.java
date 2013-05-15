/* This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this file,
 * You can obtain one at http://mozilla.org/MPL/2.0/. */


package pt.webdetails.cpk;

import pt.webdetails.cpf.repository.IRepositoryAccess;
import pt.webdetails.cpf.session.ISessionUtils;
import pt.webdetails.cpf.session.IUserSession;
import pt.webdetails.cpf.session.PentahoSessionUtils;
import pt.webdetails.cpf.utils.IPluginUtils;
import pt.webdetails.cpk.security.AccessControl;
import pt.webdetails.cpk.security.IAccessControl;

/**
 *
 * @author joao
 */
public class CpkPentahoEnvironment implements ICpkEnvironment{

    private IPluginUtils pluginUtils;
    private IRepositoryAccess repoAccess;
    private IAccessControl accessControl;
    
    public CpkPentahoEnvironment(IPluginUtils pluginUtils, IRepositoryAccess repoAccess){
        this.pluginUtils=pluginUtils;
        this.repoAccess=repoAccess;
        this.accessControl=new AccessControl(pluginUtils);
    }
    
    @Override
    public IPluginUtils getPluginUtils() {
        return pluginUtils;
    }

    @Override
    public IRepositoryAccess getRepositoryAccess() {
        return repoAccess;
    }

    @Override
    public IAccessControl getAccessControl() {
        return accessControl;
    }

    @Override
    public String getPluginName() {
        return pluginUtils.getPluginName();
    }

    @Override
    public ISessionUtils getSessionUtils() {
        return new PentahoSessionUtils();
    }

}

/* This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this file,
 * You can obtain one at http://mozilla.org/MPL/2.0/. */
package pt.webdetails.cpk;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.dom4j.Document;
import org.dom4j.DocumentException;
import org.pentaho.platform.api.engine.IPluginResourceLoader;
import org.pentaho.platform.engine.core.system.PentahoSystem;
import org.pentaho.platform.util.xml.dom4j.XmlDom4JHelper;
import pt.webdetails.cpf.plugins.Plugin;
import pt.webdetails.cpf.plugins.PluginsAnalyzer;
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
public class CpkPentahoEnvironment implements ICpkEnvironment {

    private IPluginUtils pluginUtils;
    private IRepositoryAccess repoAccess;
    private IAccessControl accessControl;

    public CpkPentahoEnvironment(IPluginUtils pluginUtils, IRepositoryAccess repoAccess) {
        this.pluginUtils = pluginUtils;
        this.repoAccess = repoAccess;
        this.accessControl = new AccessControl(pluginUtils);
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

    @Override
    public void reload() {
        PluginsAnalyzer pluginsAnalyzer = new PluginsAnalyzer();
        pluginsAnalyzer.refresh();
    }
}

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

package pt.webdetails.cpf;

import java.io.File;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.pentaho.platform.api.engine.IPluginManager;
import org.pentaho.platform.engine.core.system.PentahoSystem;
import org.pentaho.platform.plugin.services.pluginmgr.PluginClassLoader;
import org.springframework.beans.factory.config.ConfigurableListableBeanFactory;
import org.springframework.beans.factory.xml.XmlBeanDefinitionReader;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.context.support.FileSystemXmlApplicationContext;

/**
 * @author mg
 *
 */
@SuppressWarnings("serial")
public abstract class SpringEnabledContentGenerator extends SimpleContentGenerator {

    private static final Log logger = LogFactory
            .getLog(SpringEnabledContentGenerator.class);
    protected IPluginManager pm = PentahoSystem.get(IPluginManager.class);
    protected static ConfigurableApplicationContext pluginContext;

    public SpringEnabledContentGenerator() {
        if (pluginContext == null) {
            pluginContext = getSpringBeanFactory();
        }
    }

    private ConfigurableApplicationContext getSpringBeanFactory() {
        final PluginClassLoader loader = (PluginClassLoader) pm
                .getClassLoader(getPluginName());
        logger.warn(loader.getPluginDir());
        File f = new File(loader.getPluginDir(), "plugin.spring.xml"); //$NON-NLS-1$
        if (f.exists()) {
            logger.debug("Found plugin spring file @ " + f.getAbsolutePath()); //$NON-NLS-1$
            ConfigurableApplicationContext context = new FileSystemXmlApplicationContext(
                    "file:" + f.getAbsolutePath()) { //$NON-NLS-1$
                @Override
                protected void initBeanDefinitionReader(
                        XmlBeanDefinitionReader beanDefinitionReader) {

                    beanDefinitionReader.setBeanClassLoader(loader);
                }

                @Override
                protected void prepareBeanFactory(
                        ConfigurableListableBeanFactory clBeanFactory) {
                    super.prepareBeanFactory(clBeanFactory);
                    clBeanFactory.setBeanClassLoader(loader);
                }

                /**
                 * Critically important to override this and return the desired
                 * CL
				 *
                 */
                @Override
                public ClassLoader getClassLoader() {
                    return loader;
                }
            };
            return context;
        }
        throw new IllegalStateException("no plugin.spring.xml file found"); //$NON-NLS-1$
    }
}

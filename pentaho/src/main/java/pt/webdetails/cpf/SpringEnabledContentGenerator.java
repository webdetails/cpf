/*! ******************************************************************************
 *
 * Pentaho
 *
 * Copyright (C) 2024 by Hitachi Vantara, LLC : http://www.pentaho.com
 *
 * Use of this software is governed by the Business Source License included
 * in the LICENSE.TXT file.
 *
 * Change Date: 2029-07-20
 ******************************************************************************/


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

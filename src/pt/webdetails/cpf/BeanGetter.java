/* This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this file,
 * You can obtain one at http://mozilla.org/MPL/2.0/. */
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

public class BeanGetter {
  
  
  private static Log logger = LogFactory.getLog(BeanGetter.class);
  private static BeanGetter _instance;
  protected ConfigurableApplicationContext context;  
  
  
  public static BeanGetter getInstance(String pluginName) {
    if (_instance == null) {
      _instance = new BeanGetter(pluginName);
    }
    
    return _instance;
  }
  
  
  

  
  private ConfigurableApplicationContext getSpringBeanFactory(String pluginName) {

   final PluginClassLoader loader = (PluginClassLoader) PentahoSystem.get(IPluginManager.class)
                .getClassLoader(pluginName);
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
        throw new IllegalStateException("no plugin.spring.xml file found"); //$NON-NLS-1        
  }  
  
  
  
  private BeanGetter(String pluginName) {
    context = getSpringBeanFactory(pluginName);    
  }
  
  
  public Object getBean(String id) {
    return context.getBean(id);
  }
  
}

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
package pt.webdetails.cpf.bean;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.config.ConfigurableListableBeanFactory;
import org.springframework.beans.factory.xml.XmlBeanDefinitionReader;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;

import java.net.URL;

public abstract class AbstractBeanFactory implements IBeanFactory {

  private static final Log logger = LogFactory.getLog( IBeanFactory.class );

  protected static ConfigurableApplicationContext ctx;

  public abstract String getSpringXMLFilename();

  @Override public Object getBean( String id ) {
    return ( getCtx() != null && getCtx().containsBean( id ) ) ? getCtx().getBean( id ) : null;
  }

  @Override public boolean containsBean( String id ) {
    return getCtx() != null ? getCtx().containsBean( id ) : false;
  }

  @Override public String[] getBeanNamesForType( Class<?> clazz ) {
    return getCtx().getBeanNamesForType( clazz );
  }

  protected ConfigurableApplicationContext getCtx() {
    if ( ctx == null ) {
      ctx = getSpringBeanFactory( getSpringXMLFilename() );
    }

    return ctx;
  }

  protected ConfigurableApplicationContext getSpringBeanFactory( String config ) {
    getLogger().debug( "bean factory init" );

    try {
      // important: use the plugin's classloader
      final ClassLoader cl = getClass().getClassLoader();

      URL url = cl.getResource( config );
      if ( url != null ) {
        getLogger().debug( "Found spring file @ " + url );

        ConfigurableApplicationContext context = new ClassPathXmlApplicationContext( config ) {

          @Override protected void initBeanDefinitionReader( XmlBeanDefinitionReader beanDefinitionReader ) {
            beanDefinitionReader.setBeanClassLoader( cl );
          }

          @Override protected void prepareBeanFactory( ConfigurableListableBeanFactory clBeanFactory ) {
            super.prepareBeanFactory( clBeanFactory );
            clBeanFactory.setBeanClassLoader( cl );
          }

          /**
           * Critically important to override this and return the desired classloader
           **/
          @Override public ClassLoader getClassLoader() {
            return cl;
          }
        };
        getLogger().debug( "bean factory context" );
        return context;
      }
    } catch ( Exception e ) {
      getLogger().fatal( "Error loading " + config, e );
    }
    getLogger().fatal( "Spring definition file does not exist. " + "There should be a " + config
        + " file on the classpath " );
    return null;

  }

  protected Log getLogger() {
    return logger;
  }
}

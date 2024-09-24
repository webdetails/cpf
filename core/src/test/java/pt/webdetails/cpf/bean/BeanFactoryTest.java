/*!
 * Copyright 2002 - 2021 Webdetails, a Hitachi Vantara company.  All rights reserved.
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

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

public class BeanFactoryTest {

  /*
   * We tend to use BeanFactory class to load spring xml files that are
   * in the classloader's base path.
   *
   * But in unit testing, things differ: the test class
   * is in a /test-src dir, but it gets copied into /bin/test/classes
   *
   * So we count from that base dir onwards
   */

  private final String SPRING_XML_FILE = "test.spring.xml";

  IDummyBean dummyBean;
  IBeanFactory factory;

  @Before
  public void setUp() {

    factory = new AbstractBeanFactory() {
      @Override public String getSpringXMLFilename() {
        return SPRING_XML_FILE;
      }
    };
  }

  @Test
  public void testSpringXmlFileFound() {
    assertNotNull( factory );
    assertFalse( factory.containsBean( "IBogusBean" ) );
    assertTrue( factory.containsBean( IDummyBean.class.getSimpleName() ) );
  }

  @Test
  public void testBeanLoadingOK() {

    assertNotNull( factory );

    dummyBean = (IDummyBean) factory.getBean( IDummyBean.class.getSimpleName() );

    assertNotNull( dummyBean );
    assertTrue( dummyBean.isBeanOK() );
  }

  @After
  public void tearDown() {
    factory = null;
    dummyBean = null;
  }
}

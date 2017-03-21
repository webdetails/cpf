/*!
 * Copyright 2002 - 2017 Webdetails, a Pentaho company. All rights reserved.
 *
 * This software was developed by Webdetails and is provided under the terms
 * of the Mozilla Public License, Version 2.0, or any later version. You may not use
 * this file except in compliance with the license. If you need a copy of the license,
 * please go to http://mozilla.org/MPL/2.0/. The Initial Developer is Webdetails.
 *
 * Software distributed under the Mozilla Public License is distributed on an "AS IS"
 * basis, WITHOUT WARRANTY OF ANY KIND, either express or implied. Please refer to
 * the license for the specific language governing your rights and limitations.
 */

package pt.webdetails.cpf.utils;

import org.apache.commons.lang.StringUtils;
import org.junit.Assert;
import org.junit.Test;

import java.util.Collection;
import java.util.Iterator;

public class CsvUtilTest {

  @Test
  public void testNull() {
    final Collection<String> strings = CsvUtil.parseCsvString( null );
    Assert.assertNotNull( strings );
    Assert.assertTrue( strings.isEmpty() );
  }

  @Test
  public void testBlank() {
    final Collection<String> strings = CsvUtil.parseCsvString( "" );
    Assert.assertNotNull( strings );
    Assert.assertTrue( strings.isEmpty() );
  }

  @Test
  public void testNoCommas() {
    final String nocommas = "nocommas";
    final Collection<String> strings = CsvUtil.parseCsvString( nocommas );
    Assert.assertNotNull( strings );
    Assert.assertTrue( strings.size() == 1 );
    Assert.assertEquals( nocommas, strings.iterator().next() );
  }

  @Test
  public void testList() {
    final String[] array = { "one", "two", "three" };
    final String commas = StringUtils.join( array, ',' );
    final Collection<String> strings = CsvUtil.parseCsvString( commas );
    Assert.assertNotNull( strings );
    Assert.assertTrue( strings.size() == 3 );
    final Iterator<String> iterator = strings.iterator();

    for ( String el : array ) {
      Assert.assertEquals( el, iterator.next() );
    }
  }

}

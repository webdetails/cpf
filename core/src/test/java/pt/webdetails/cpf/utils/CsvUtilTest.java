/*! ******************************************************************************
 *
 * Pentaho
 *
 * Copyright (C) 2024 by Hitachi Vantara, LLC : http://www.pentaho.com
 *
 * Use of this software is governed by the Business Source License included
 * in the LICENSE.TXT file.
 *
 * Change Date: 2028-08-13
 ******************************************************************************/

package pt.webdetails.cpf.utils;

import org.apache.commons.lang.StringUtils;
import org.junit.Test;

import java.util.Collection;
import java.util.Iterator;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

public class CsvUtilTest {

  @Test
  public void testNull() {
    final Collection<String> strings = CsvUtil.parseCsvString( null );
    assertNotNull( strings );
    assertTrue( strings.isEmpty() );
  }

  @Test
  public void testBlank() {
    final Collection<String> strings = CsvUtil.parseCsvString( "" );
    assertNotNull( strings );
    assertTrue( strings.isEmpty() );
  }

  @Test
  public void testNoCommas() {
    final String nocommas = "nocommas";
    final Collection<String> strings = CsvUtil.parseCsvString( nocommas );
    assertNotNull( strings );
    assertEquals( 1, strings.size() );
    assertEquals( nocommas, strings.iterator().next() );
  }

  @Test
  public void testList() {
    final String[] array = { "one", "two", "three" };
    final String commas = StringUtils.join( array, ',' );
    final Collection<String> strings = CsvUtil.parseCsvString( commas );
    assertNotNull( strings );
    assertEquals( 3, strings.size() );
    final Iterator<String> iterator = strings.iterator();

    for ( String el : array ) {
      assertEquals( el, iterator.next() );
    }
  }
}

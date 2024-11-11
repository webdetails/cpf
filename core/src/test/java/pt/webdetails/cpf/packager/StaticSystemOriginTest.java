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

package pt.webdetails.cpf.packager;

import org.junit.Test;
import pt.webdetails.cpf.packager.origin.StaticSystemOrigin;

import static org.junit.Assert.assertEquals;

public class StaticSystemOriginTest {

  @Test
  public void testNullURLProvider() {
    StaticSystemOrigin instance = new StaticSystemOrigin("/home");
    String result = instance.getUrl("/admin", null);
    assertEquals( "/home/admin", result);
  }
}

/*!
 * Copyright 2018-2021 Webdetails, a Hitachi Vantara company.  All rights reserved.
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

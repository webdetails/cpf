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

package pt.webdetails.cpf;

import org.junit.Test;
import pt.webdetails.cpf.utils.MimeTypes;

import static org.junit.Assert.assertTrue;

public class MimeTypeHandlerTest {

  private static final String EXTENSION_ONLY_KNOW_IN_CPF = "cdfde";
  private static final String EXPECTED_OUTCOME_IN_CPF = MimeTypes.JSON;

  private static final String EXTENSION_ONLY_KNOW_IN_PLATFORM = "rtf";
  private static final String EXPECTED_OUTCOME_IN_PLATFORM = "application/rtf";

  private static final String EXTENSION_UNKNOWN_BY_ALL = "qwerty";
  private static final String EXPECTED_OUTCOME_UNKNOWN_BY_ALL = "application/unknown";

  @Test
  public void testGetExtensionOnlyKnownInPlatform() {

    String actualOutcome = MimeTypeHandler.getMimeTypeFromExtension( EXTENSION_ONLY_KNOW_IN_PLATFORM );

    assertTrue( EXPECTED_OUTCOME_IN_PLATFORM.equalsIgnoreCase( actualOutcome ) );
  }

  @Test
  public void testGetExtensionOnlyKnownInCpf() {

    String actualOutcome = MimeTypeHandler.getMimeTypeFromExtension( EXTENSION_ONLY_KNOW_IN_CPF );

    assertTrue( EXPECTED_OUTCOME_IN_CPF.equalsIgnoreCase( actualOutcome ) );
  }

  @Test
  public void testGetExtensionUnknownByAll() {

    String actualOutcome = MimeTypeHandler.getMimeTypeFromExtension( EXTENSION_UNKNOWN_BY_ALL );

    assertTrue( EXPECTED_OUTCOME_UNKNOWN_BY_ALL.equalsIgnoreCase( actualOutcome ) );
  }
}

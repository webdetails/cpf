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

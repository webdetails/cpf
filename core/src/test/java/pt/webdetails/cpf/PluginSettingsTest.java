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


package pt.webdetails.cpf;

import org.dom4j.Element;
import org.junit.Before;
import org.junit.Test;

import java.util.Arrays;

import static org.junit.Assert.assertArrayEquals;
import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.anyString;
import static org.mockito.Mockito.doCallRealMethod;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.mock;

public class PluginSettingsTest {

  private PluginSettings settings;

  @Before
  public void setup() {
    settings = mock( PluginSettings.class );
  }

  @Test
  public void testGetTagValueNull() {
    doCallRealMethod().when( settings ).getTagValue( anyString() );
    assertEquals( 0, settings.getTagValue( null ).size() );
  }

  @Test
  public void testGetTagValueNotNull() {
    Element element = mock( Element.class );
    doReturn( "x" ).when( element ).getText();

    doCallRealMethod().when( settings ).getTagValue( anyString() );
    doReturn( Arrays.asList( element ) ).when( settings ).getSettingsXmlSection( anyString() );

    assertArrayEquals( new String[] { "x" }, settings.getTagValue( "" ).toArray() );
  }
}

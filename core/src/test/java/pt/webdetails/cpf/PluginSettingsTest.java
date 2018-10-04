/*!
* Copyright 2018 Webdetails, a Hitachi Vantara company.  All rights reserved.
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

import org.dom4j.Element;
import org.junit.Before;
import org.junit.Test;

import java.util.Arrays;

import static org.junit.Assert.assertArrayEquals;
import static org.junit.Assert.assertEquals;
import static org.mockito.Matchers.anyString;
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

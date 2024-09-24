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

package pt.webdetails.cpf.plugins;

import org.dom4j.Document;
import org.dom4j.Element;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import pt.webdetails.cpf.utils.XmlDom4JUtils;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.any;
import static org.mockito.Mockito.anyString;
import static org.mockito.Mockito.doCallRealMethod;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;
import static org.mockito.Mockito.mockStatic;
import org.mockito.junit.MockitoJUnitRunner;

@RunWith( MockitoJUnitRunner.class )
public class PluginTest {

  @Before
  public void setup() throws IOException {
    String settings = "<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n"
      + "<settings>\n"
      + "        <cache>false</cache>\n"
      + "        <cache-messages>true</cache-messages>\n"
      + "        <max-age>2628001</max-age>\n"
      + "</settings>\n";

    mockStatic( XmlDom4JUtils.class );

    InputStream is = new ByteArrayInputStream( settings.getBytes() );
    when( XmlDom4JUtils.getDocumentFromStream( is ) ).thenCallRealMethod();

    Document document = XmlDom4JUtils.getDocumentFromStream( is );
    when( XmlDom4JUtils.getDocumentFromFile( any(), anyString() ) ).thenReturn( document );
  }

  @Test
  public void getSettingsSectionTest() {
    Plugin plugin = mock( Plugin.class );
    doCallRealMethod().when( plugin ).getSettingsSection( anyString() );
    doReturn( true ).when( plugin ).hasSettingsXML();

    List<Element> elements = plugin.getSettingsSection( "/cache" );
    // unfortunately DefaultElement doesn't implement an equals method
    assertEquals( "cache", elements.get( 0 ).getName() );
    assertEquals( "false", elements.get( 0 ).getText() );
  }
}

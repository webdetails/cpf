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

package pt.webdetails.cpf.plugins;

import org.dom4j.Document;
import org.dom4j.Element;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.powermock.core.classloader.annotations.PowerMockIgnore;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;
import pt.webdetails.cpf.repository.api.IReadAccess;
import pt.webdetails.cpf.utils.XmlDom4JUtils;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.anyString;
import static org.mockito.Mockito.doCallRealMethod;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;
import static org.powermock.api.mockito.PowerMockito.mockStatic;

@PowerMockIgnore( "jdk.internal.reflect.*" )
@RunWith( PowerMockRunner.class )
@PrepareForTest( XmlDom4JUtils.class )
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
    when( XmlDom4JUtils.getDocumentFromFile( any( IReadAccess.class ), anyString() ) ).thenReturn( document );
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

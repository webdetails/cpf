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

import org.junit.Before;
import org.junit.Test;
import pt.webdetails.cpf.AbstractPluginSystemTest;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.Collection;

import static org.junit.Assert.assertEquals;

public class PluginUtilsTest extends AbstractPluginSystemTest {

  private PluginUtils pluginUtils;

  @Before
  public void beforeEachTest() {
    /* parent class initializes the content of PLUGIN_DIR and PLUGIN_NAME */
    pluginUtils = new PluginUtilsForTesting( PLUGIN_NAME, PLUGIN_DIR );
  }

  @Test
  public void testGetPluginRelativeDirectory() throws FileNotFoundException {

    String pluginRelDir = pluginUtils.getPluginRelativeDirectory( PLUGIN_DIR + "/resources/stuff/stuff.csv", true );
    String pluginRelDirWithSpaces =
      pluginUtils.getPluginRelativeDirectory( PLUGIN_DIR + "/resources/dir with spaces/bogus.txt", true );
    String pluginRelDirWithEncodedSpaces =
      pluginUtils.getPluginRelativeDirectory( PLUGIN_DIR + "/resources/dir%20with%20spaces/bogus.txt", true );

    assertPathEquals( "/bogusPlugin/resources/stuff/", pluginRelDir );
    assertPathEquals( "/bogusPlugin/resources/dir with spaces/", pluginRelDirWithSpaces );
    assertPathEquals( "/bogusPlugin/resources/dir with spaces/", pluginRelDirWithEncodedSpaces );
  }

  @Test
  public void testGetPluginResources() {
    //allResources :
    //PLUGIN_DIR/resources/dir with spaces/bogus.txt
    //PLUGIN_DIR/resources/stuff/moreStuff/moreStuffedBogus.txt
    //PLUGIN_DIR/resources/stuff/stuffedBogus.txt
    //PLUGIN_DIR/resources/stuff/stuff.csv
    //PLUGIN_DIR/resources/.hidden
    //PLUGIN_DIR/resources/bogus.txt
    //PLUGIN_DIR/plugin.xml
    //PLUGIN_DIR/settings.xml
    Collection<File> allResources = pluginUtils.getPluginResources( "", true );
    Collection<File> onlyHidden = pluginUtils.getPluginResources( "", true, "\\..*" );
    Collection<File> onlyTxtInResourcesNoRecursive = pluginUtils.getPluginResources( "resources", false, ".*\\.txt" );

    assertEquals( 8, allResources.size() );
    assertEquals( 1, onlyHidden.size() );
    assertEquals( 1, onlyTxtInResourcesNoRecursive.size() );
  }

}

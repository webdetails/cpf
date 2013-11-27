/*!
* Copyright 2002 - 2013 Webdetails, a Pentaho company.  All rights reserved.
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

package pt.webdetails.cpf.utils;

import junit.framework.Assert;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.Collection;

public class PluginUtilsTest {

  private static PluginUtils pluginUtils;
  private static final String USER_DIR = System.getProperty( "user.dir" );
  private static final String PLUGIN_NAME = "bogusPlugin";
  private static final String PLUGIN_DIR = USER_DIR + "/test-resources/repo/system/bogusPlugin";


  @BeforeClass
  public static void setUp() {
    pluginUtils = new PluginUtilsForTesting( PLUGIN_NAME, PLUGIN_DIR );
  }

  @Before
  public void beforeEachTest() {

  }

  @Test
  public void testGetPluginRelativeDirectory() throws FileNotFoundException {

    String pluginRelDir = pluginUtils.getPluginRelativeDirectory( PLUGIN_DIR + "/resources/stuff/stuff.csv", true );
    String pluginRelDirWithSpaces =
      pluginUtils.getPluginRelativeDirectory( PLUGIN_DIR + "/resources/dir with spaces/bogus.txt", true );
    String pluginRelDirWithEncodedSpaces =
      pluginUtils.getPluginRelativeDirectory( PLUGIN_DIR + "/resources/dir%20with%20spaces/bogus.txt", true );

    Assert.assertTrue( pluginRelDir.equals( "/bogusPlugin/resources/stuff/" ) );
    Assert.assertTrue( pluginRelDirWithSpaces.equals( "/bogusPlugin/resources/dir with spaces/" ) );
    Assert.assertTrue( pluginRelDirWithEncodedSpaces.equals( "/bogusPlugin/resources/dir with spaces/" ) );

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

    Assert.assertTrue( allResources.size() == 8 );
    Assert.assertTrue( onlyHidden.size() == 1 );
    Assert.assertTrue( onlyTxtInResourcesNoRecursive.size() == 1 );
  }

}

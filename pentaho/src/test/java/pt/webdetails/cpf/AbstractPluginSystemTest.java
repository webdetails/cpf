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
package pt.webdetails.cpf;

import static org.junit.Assert.*;
import org.junit.Before;
import org.junit.Rule;
import org.junit.rules.TemporaryFolder;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

/**
 * Abstract base class for tests that require access to the on disk plugin system
 *
 * To avoid cross-test interference, this class provides a copy of the src/test/resources/repo content in a temporary
 * folder created for each individual test and automatically deleted after each test.
 *
 * It also provides the assertPathEquals function that overcomes the problem of comparing paths with separators for
 * different operating systems.
 */
public abstract class AbstractPluginSystemTest {
  private static final String USER_DIR = System.getProperty( "user.dir" );
  protected static final String PLUGIN_NAME = "bogusPlugin";
  protected String PLUGIN_DIR = null;

  /**
   * Rule causes JUnit to create a new temporary folder before each test and delete it afterwards.
   */
  @Rule
  public TemporaryFolder testFolder = new TemporaryFolder();

  private void copyFile( Path srcRoot, Path dstRoot, Path relativePath ) throws IOException {
    Path src = srcRoot.resolve( relativePath );
    Path dst = dstRoot.resolve( relativePath );
    Files.copy( src, dst );
  }

  @Before
  public void setupFilesystem() throws IOException {
    // Setup temporary file structure
    File tempPluginFolder = testFolder.newFolder( "repo", "system", PLUGIN_NAME );
    testFolder.newFolder( "repo", "system", PLUGIN_NAME, "resources", "dir with spaces" );
    testFolder.newFolder( "repo", "system", PLUGIN_NAME, "resources", "stuff", "moreStuff" );

    // Populate
    Path sourcePath = Paths.get( USER_DIR, "src", "test", "resources", "repo", "system", PLUGIN_NAME );
    Path targetPath = tempPluginFolder.toPath();

    copyFile( sourcePath, targetPath, Paths.get( "plugin.xml" ) );
    copyFile( sourcePath, targetPath, Paths.get( "settings.xml" ) );
    copyFile( sourcePath, targetPath, Paths.get( "resources", ".hidden" ) );
    copyFile( sourcePath, targetPath, Paths.get( "resources", "bogus.txt" ) );
    copyFile( sourcePath, targetPath, Paths.get( "resources", "dir with spaces", "bogus.txt" ) );
    copyFile( sourcePath, targetPath, Paths.get( "resources", "stuff", "stuff.csv" ) );
    copyFile( sourcePath, targetPath, Paths.get( "resources", "stuff", "stuffedBogus.txt" ) );
    copyFile( sourcePath, targetPath, Paths.get( "resources", "stuff", "moreStuff", "moreStuffedBogus.txt" ) );

    // set global variable and init pluginUtils
    PLUGIN_DIR = tempPluginFolder.getAbsolutePath();
  }

  public void assertPathEquals( String expected, String actual ) {
    Path pathExpected = Paths.get( expected );
    Path pathActual = Paths.get( actual );
    assertEquals( pathExpected, pathActual );
  }
}

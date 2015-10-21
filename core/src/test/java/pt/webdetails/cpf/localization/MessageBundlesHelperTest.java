/*!
 * Copyright 2002 - 2014 Webdetails, a Pentaho company.  All rights reserved.
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
package pt.webdetails.cpf.localization;


import junit.framework.Assert;
import junit.framework.TestCase;
import org.junit.Test;
import pt.webdetails.cpf.Util;
import pt.webdetails.cpf.localization.test.BasicFile;
import pt.webdetails.cpf.localization.test.PluginWriteAccess;
import pt.webdetails.cpf.localization.test.SomeFolderReadAccess;
import pt.webdetails.cpf.repository.api.IBasicFile;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class MessageBundlesHelperTest extends TestCase {

  private final Locale LOCALE = new Locale( "en", "US" );

  private final String MOCK_TEXT_FOR_PARAM_REPLACEMENT =
    "#{GLOBAL_MESSAGE_SET_NAME};#{GLOBAL_MESSAGE_SET_PATH};#{LANGUAGE_CODE}";

  private final String[] SOME_ACCEPTED_FILES_UNIX_SEPARATOR = new String[] {
    "messages.properties",
    "messages_en.properties",
    "messages_en" + MessageBundlesHelper.LANGUAGE_COUNTRY_SEPARATOR_UNIX + "US.properties",
    "messages_es" + MessageBundlesHelper.LANGUAGE_COUNTRY_SEPARATOR_UNIX + "ES.properties"
  };

  private final String[] SOME_ACCEPTED_FILES = new String[] {
    "messages.properties",
    "messages_en.properties",
    "messages_pt.properties",
    "messages_en" + MessageBundlesHelper.LANGUAGE_COUNTRY_SEPARATOR + "US.properties",
    "messages_pt" + MessageBundlesHelper.LANGUAGE_COUNTRY_SEPARATOR + "PT.properties"
  };

  private final String[] SOME_NON_ACCEPTED_FILES = new String[] {
    "some-dashboard.wcdf",
    "some-dashboard.cdfde",
    "some-dashboard.cda",
    "messages_with_non_matchable_regex.properties",
    "mEssAGeS_PT_br",
  };

  private final String SOME_FOLDER_PATH = Util.SEPARATOR;

  private final String MOCK_PLUGIN_URL = Util.SEPARATOR + "dummy" + Util.SEPARATOR + "url";

  @Test
  public void testAcceptedMessages() {

    List<IBasicFile> basicFiles = toBasicFileList( Util.SEPARATOR, SOME_ACCEPTED_FILES );

    SomeFolderReadAccess someFolderAccess = new SomeFolderReadAccess( basicFiles );
    PluginWriteAccess pluginAccess = new PluginWriteAccess();

    MessageBundlesHelper mbh = null;

    try {
      mbh = new MessageBundlesHelper( SOME_FOLDER_PATH, someFolderAccess, pluginAccess, LOCALE, MOCK_PLUGIN_URL );
    } catch ( Exception e ) {
      Assert.fail();
    }

    assertTrue( mbh != null );
    assertTrue( pluginAccess != null && pluginAccess.getStoredFiles() != null );
    assertTrue( pluginAccess.getStoredFiles().size() > 0 );
    assertTrue( pluginAccess.getStoredFiles().size() == SOME_ACCEPTED_FILES.length );

    for ( String file : SOME_ACCEPTED_FILES ) {

      String messageInCacheDir = Util.joinPath( Util.SEPARATOR, MessageBundlesHelper.BASE_CACHE_DIR, file );
      assertTrue( pluginAccess.getStoredFiles().contains( new BasicFile( messageInCacheDir ) ) );
    }
  }

  @Test
  public void testAcceptedMessagesUnixSeparator() {

    List<IBasicFile> basicFiles = toBasicFileList( Util.SEPARATOR, SOME_ACCEPTED_FILES_UNIX_SEPARATOR );

    SomeFolderReadAccess someFolderAccess = new SomeFolderReadAccess( basicFiles );
    PluginWriteAccess pluginAccess = new PluginWriteAccess();

    MessageBundlesHelper mbh = null;

    try {
      mbh = new MessageBundlesHelper( SOME_FOLDER_PATH, someFolderAccess, pluginAccess, LOCALE, MOCK_PLUGIN_URL );
    } catch ( Exception e ) {
      Assert.fail();
    }

    assertTrue( mbh != null );
    assertTrue( pluginAccess != null && pluginAccess.getStoredFiles() != null );
    assertTrue( pluginAccess.getStoredFiles().size() > 0 );
    assertTrue( pluginAccess.getStoredFiles().size() == SOME_ACCEPTED_FILES_UNIX_SEPARATOR.length );

    for ( String file : SOME_ACCEPTED_FILES_UNIX_SEPARATOR ) {

      String messageInCacheDir = Util.joinPath( Util.SEPARATOR, MessageBundlesHelper.BASE_CACHE_DIR, mbh.sanitize( file ) );
      assertTrue( pluginAccess.getStoredFiles().contains( new BasicFile( messageInCacheDir ) ) );
    }
  }

  @Test
  public void testNonAcceptedMessages() {

    List<IBasicFile> basicFiles = toBasicFileList( Util.SEPARATOR, SOME_NON_ACCEPTED_FILES );

    SomeFolderReadAccess someFolderAccess = new SomeFolderReadAccess( basicFiles );
    PluginWriteAccess pluginAccess = new PluginWriteAccess();

    MessageBundlesHelper mbh = null;

    try {
      mbh = new MessageBundlesHelper( SOME_FOLDER_PATH, someFolderAccess, pluginAccess, LOCALE, MOCK_PLUGIN_URL );
    } catch ( Exception e ) {
      Assert.fail();
    }

    assertTrue( mbh != null );
    assertTrue( pluginAccess != null && pluginAccess.getStoredFiles() != null );
    assertTrue( pluginAccess.getStoredFiles().size() > 0 );

    // 3 empty files created ( as a fallback ) and regarding the provided LOCALE: 'messages_en.properties'
    // and 'messages_en-US.properties' ( and one other which is 'messages.properties' )
    assertTrue( pluginAccess.getStoredFiles().size() == 3 );

    String fallback_file_en = "messages_" + LOCALE.getLanguage() + ".properties";
    String fallback_file_en_messageInCacheDir = Util.joinPath( Util.SEPARATOR, MessageBundlesHelper.BASE_CACHE_DIR,
      fallback_file_en );

    String fallback_file_en_US =
      "messages_" + LOCALE.getLanguage() + MessageBundlesHelper.LANGUAGE_COUNTRY_SEPARATOR + LOCALE.getCountry()
        + ".properties";

    String fallback_file_en_US_messageInCacheDir = Util.joinPath( Util.SEPARATOR, MessageBundlesHelper.BASE_CACHE_DIR,
      fallback_file_en_US );

    assertTrue( pluginAccess.getStoredFiles().contains( new BasicFile( fallback_file_en_messageInCacheDir ) ) );
    assertTrue( pluginAccess.getStoredFiles().contains( new BasicFile( fallback_file_en_US_messageInCacheDir ) ) );

    // check if none of the following files has been stored in cache dir
    for ( String file : SOME_NON_ACCEPTED_FILES ) {

      String messageInCacheDir = Util.joinPath( Util.SEPARATOR, MessageBundlesHelper.BASE_CACHE_DIR, file );
      assertFalse( pluginAccess.getStoredFiles().contains( new BasicFile( messageInCacheDir ) ) );
    }
  }

  @Test
  public void testTextReplacement() {

    List<IBasicFile> basicFiles = toBasicFileList( Util.SEPARATOR, SOME_ACCEPTED_FILES );

    SomeFolderReadAccess someFolderAccess = new SomeFolderReadAccess( basicFiles );
    PluginWriteAccess pluginAccess = new PluginWriteAccess();

    MessageBundlesHelper mbh = null;

    try {
      mbh = new MessageBundlesHelper( SOME_FOLDER_PATH, someFolderAccess, pluginAccess, LOCALE, MOCK_PLUGIN_URL );
    } catch ( Exception e ) {
      Assert.fail();
    }

    assertTrue( mbh != null && pluginAccess != null && pluginAccess.getStoredFiles() != null );
    assertTrue( pluginAccess.getStoredFiles().size() == SOME_ACCEPTED_FILES.length );

    String i18nReplacedText = null;

    try {
      i18nReplacedText = mbh.replaceParameters( MOCK_TEXT_FOR_PARAM_REPLACEMENT, null );
    } catch ( Exception e ) {
      Assert.fail();
    }

    assertNotNull( i18nReplacedText );

    String[] replacedText = i18nReplacedText.split( ";" );

    assertTrue( replacedText.length == 3 );
    assertTrue( replacedText[ 0 ].equals( MessageBundlesHelper.BASE_MESSAGES_FILENAME ) );
    assertTrue( replacedText[ 1 ]
      .equals( Util.joinPath( MOCK_PLUGIN_URL, MessageBundlesHelper.BASE_CACHE_DIR, SOME_FOLDER_PATH ) ) );
    assertTrue( replacedText[ 2 ]
      .equals( LOCALE.getLanguage() + MessageBundlesHelper.LANGUAGE_COUNTRY_SEPARATOR + LOCALE.getCountry() ) );
  }

  private List<IBasicFile> toBasicFileList( String path, String[] filesNames ) {

    List<IBasicFile> basicFiles = new ArrayList<IBasicFile>();

    if ( filesNames != null ) {

      for ( String fileName : filesNames ) {

        if ( path != null && fileName != null ) {

          IBasicFile file = new BasicFile( path + fileName );

          if ( !basicFiles.contains( file ) ) {
            basicFiles.add( file );
          }
        }
      }
    }

    return basicFiles;
  }
}

/*!
 * Copyright 2002 - 2017 Webdetails, a Hitachi Vantara company.  All rights reserved.
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

import org.apache.commons.io.FilenameUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import pt.webdetails.cpf.Util;
import pt.webdetails.cpf.repository.api.IBasicFile;
import pt.webdetails.cpf.repository.api.IBasicFileFilter;
import pt.webdetails.cpf.repository.api.IRWAccess;
import pt.webdetails.cpf.repository.api.IReadAccess;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class MessageBundlesHelper {

  public static final String BASE_CACHE_DIR = "tmp/.cache"; //$NON-NLS-1$
  public static final String BASE_MESSAGES_FILENAME = "messages"; //$NON-NLS-1$
  public static final String MESSAGES_EXTENSION = ".properties"; //$NON-NLS-1$


  // standard procedure is to *not* save message files to cache dir *if* they already exist there;
  // it keeps us from constantly overriding the existing ones ( and also speeds up the process );
  // new message files will be written when user clears the cache dir folder
  public static final boolean OVERRIDE_PROPERTIES_IN_CACHE_DIR_IF_EXIST = false; //$NON-NLS-1$

  /**
   * @link http://www.w3.org/International/articles/language-tags
   * <p/>
   * @link http://www.rfc-editor.org/rfc/rfc5646.txt  ( search for 'HYPHEN-MINUS' )
   * <p/>
   * @link http://www.oracle.com/technetwork/java/javase/javase7locales-334809.html
   */

  // the char that separates the language information from the country information:
  // messages_<language>LOCALE_COUNTRY_SEPARATOR_CHAR<country>.properties  ( ex: messages_en-US.properties )
  // FYI: jQuery.i18n.browserLang() returns 'en-US', 'pt-PT', .., therefore the separator char it uses is a hyphen
  public static final String LANGUAGE_COUNTRY_SEPARATOR = "-"; //$NON-NLS-1$

  // we also suppport underscore as separator ( ex: 'en_US', 'pt_PT', ... ), used internally in unix
  // check your /usr/share/locale or /usr/share/i18n/locales
  public static final String LANGUAGE_COUNTRY_SEPARATOR_UNIX = "_"; //$NON-NLS-1$

  // matches: messages_en.properties, messages_en-US.properties;
  // does not match: messages.properties, messages_EN.properties, messages_en_us.properties
  public final String REGEXP =
      BASE_MESSAGES_FILENAME + "_[a-z][a-z](" + LANGUAGE_COUNTRY_SEPARATOR + "[A-Z][A-Z]){0,1}" + MESSAGES_EXTENSION;

  // matches: messages_en.properties, messages_en_US.properties;
  public final String REGEXP_ALT =
      BASE_MESSAGES_FILENAME + "_[a-z][a-z](" + LANGUAGE_COUNTRY_SEPARATOR_UNIX + "[A-Z][A-Z]){0,1}" + MESSAGES_EXTENSION;


  private static final Log logger = LogFactory.getLog( MessageBundlesHelper.class );

  Locale locale;
  String dashboardFolderPath;
  IReadAccess dashboardAccess;
  IRWAccess pluginAccess;
  String pluginStaticBaseContentUrl;

  public MessageBundlesHelper( String dashboardFolderPath, IReadAccess dashboardAccess, IRWAccess pluginAccess,
                               Locale locale, String pluginStaticBaseContentUrl )
    throws IllegalArgumentException, IOException {

    if ( dashboardAccess == null ) {
      throw new IllegalArgumentException( "dashboardAccess is null" );

    } else if ( pluginAccess == null ) {
      throw new IllegalArgumentException( "pluginAccess is null" );

    } else if ( locale == null || StringUtils.isEmpty( locale.getLanguage() ) ) {
      throw new IllegalArgumentException( "locale language is null" );

    } else if ( StringUtils.isEmpty( pluginStaticBaseContentUrl ) ) {
      throw new IllegalArgumentException( "pluginStaticBaseContentUrl is null" );

    }

    setDashboardFolderPath( StringUtils.isEmpty( dashboardFolderPath ) ? Util.SEPARATOR : dashboardFolderPath );
    setDashboardAccess( dashboardAccess );
    setPluginAccess( pluginAccess );
    setPluginStaticBaseContentUrl( pluginStaticBaseContentUrl );
    setLocale( locale );

    init();
  }

  protected void init() throws IOException {

    boolean success = createCacheDirIfNotExists();

    if ( success ) {
      saveI18NMessageFilesToCacheDir();
    }
  }

  protected boolean createCacheDirIfNotExists() {

    if ( !getPluginAccess().fileExists( getBaseTempCacheFolder() ) ) {

      logger.info( "Attempting to create base base cache dir at " + getBaseTempCacheFolder() );

      if ( getPluginAccess().createFolder( getBaseTempCacheFolder(), false ) ) {
        logger.info( "Base cache dir created successfully" );

      } else {
        logger.error( "Unable to create base cache dir at " + getBaseTempCacheFolder() );
        return false;
      }
    }
    return true;
  }

  /**
   * This method is at the core of the MessageBundlesHelper engine.
   * <p/>
   * It fetches all properties files within a provided location, and stores each of them in a temp cache directory.
   * <p/>
   * 1rst - in the local folder ( local to the dashboard ) search for 'messages.properties'
   * <p/>
   * 2nd - in the same folder, search for messages_<language>.properties and
   * messages_<language>LOCALE_COUNTRY_SEPARATOR<country>.properties;
   */
  protected void saveI18NMessageFilesToCacheDir() throws IOException {

    // first let's check if there is a base properties file ( 'messages.properties' ) and save it to cache dir
    saveBaseMessageToCacheDir( OVERRIDE_PROPERTIES_IN_CACHE_DIR_IF_EXIST );

    // next: list all the properties files found in getDashboardFolder() that are meant to be
    // included in the bundled up properties file within the temp cache dir

    // search for messages_<language>.properties and messages_<language>_<country>.properties
    // a dashboard may have multiple localized properties files ( messages_en.properties, messages_pt.properties, ... )
    List<IBasicFile> localizedPropertiesFiles =
        getDashboardAccess().listFiles( getDashboardFolderPath(), new LocalizedMessageFilter(), 1, false, true );


    // standard procedure is to *not* save message files to cache dir *if* they already exist there;
    // it keeps us from constantly overriding the existing ones ( and also speeds up the process );
    // new message files will be written when user clears the cache dir folder
    if ( !OVERRIDE_PROPERTIES_IN_CACHE_DIR_IF_EXIST ) {
      localizedPropertiesFiles = filterOutMessagesAlreadySavedInCacheDir( localizedPropertiesFiles );
    }

    if ( localizedPropertiesFiles != null ) {

      for ( IBasicFile localizedPropertyFile : localizedPropertiesFiles ) {

        String fileInCacheDirPath =
            Util.joinPath( getBaseTempCacheFolder(), sanitize( localizedPropertyFile.getName() ) );

        try {

          getPluginAccess().saveFile( fileInCacheDirPath, localizedPropertyFile.getContents() );

        } catch ( IOException e ) {
          logger.error( "Unable to save " + localizedPropertyFile.getName() + " in " + getBaseTempCacheFolder(), e );
            /* log this as error and continue on to the next localized properties file  */
        }
      }
    }


    // all is done; now, we just need to ensure there are properties files in cache dir pertaining the specific
    // locale the user has provided in the constructor; ideally, this should have already been taken care of
    // in the FOR cycle above; but in the off-chance they haven't been stored in the cache dir, we'll go ahead
    // and store them as empty files;
    String localizedMsgPath = Util.joinPath( getBaseTempCacheFolder(),
        ( BASE_MESSAGES_FILENAME + "_" + getLocale().getLanguage() + MESSAGES_EXTENSION ) );

    String localizedCountryMsgPath = Util.joinPath( getBaseTempCacheFolder(),
        ( BASE_MESSAGES_FILENAME + "_" + getLocale().getLanguage() + LANGUAGE_COUNTRY_SEPARATOR
          + getLocale().getCountry() + MESSAGES_EXTENSION ) );

    if ( !getPluginAccess().fileExists( localizedMsgPath ) ) {
      getPluginAccess().saveFile( localizedMsgPath, new ByteArrayInputStream( new String().getBytes() ) );
    }

    if ( !getPluginAccess().fileExists( localizedCountryMsgPath ) ) {
      getPluginAccess().saveFile( localizedCountryMsgPath, new ByteArrayInputStream( new String().getBytes() ) );
    }
  }

  protected boolean saveBaseMessageToCacheDir( boolean overrideIfExists ) throws IOException {
    String baseMsgPath = Util.joinPath( getDashboardFolderPath(), ( BASE_MESSAGES_FILENAME + MESSAGES_EXTENSION ) );

    IBasicFile basePropertiesFile = null;

    String basePropertiesCachePath =
        Util.joinPath( getBaseTempCacheFolder(), ( BASE_MESSAGES_FILENAME + MESSAGES_EXTENSION ) );

    // first let's check if there is a base properties file ( 'messages.properties' )
    if ( getDashboardAccess().fileExists( baseMsgPath ) ) {

      basePropertiesFile = getDashboardAccess().fetchFile( baseMsgPath );

      if ( !getPluginAccess().fileExists( basePropertiesCachePath ) || overrideIfExists ) {
        getPluginAccess().saveFile( basePropertiesCachePath, basePropertiesFile.getContents() );
      }
    }

    // all is done; now, we just need to ensure there is a message.properties file in cache dir; in the off-chance
    // there isn't ( and to safeguard us from those pesky '404 not found' when the dashboard tries to fetch it ) we'll
    // go ahead and store it as an empty file;
    if ( !getPluginAccess().fileExists( basePropertiesCachePath ) ) {
      getPluginAccess().saveFile( basePropertiesCachePath, new ByteArrayInputStream( new byte[ 0 ] ) );
    }

    return true;
  }

  protected List<IBasicFile> filterOutMessagesAlreadySavedInCacheDir( List<IBasicFile> localizedMessages ) {

    List<IBasicFile> filteredLocalizedMessages = new ArrayList<IBasicFile>();

    if ( localizedMessages != null ) {

      for ( IBasicFile message : localizedMessages ) {

        if ( !getPluginAccess()
            .fileExists( Util.joinPath( getBaseTempCacheFolder(), sanitize( message.getName() ) ) ) ) {

          filteredLocalizedMessages.add( message );
        }
      }
    }

    return filteredLocalizedMessages;
  }

  public String getMessageFilesCacheUrl() {
    return FilenameUtils.separatorsToUnix( FilenameUtils.normalize(
      Util.joinPath( getPluginStaticBaseContentUrl(), BASE_CACHE_DIR, getDashboardFolderPath(), Util.SEPARATOR ) ) );
  }

  public String replaceParameters( String text, ArrayList<String> i18nTagsList ) throws IOException {

    if ( i18nTagsList == null ) {
      i18nTagsList = new ArrayList<String>();
    }

    // ex: en-GB, en-US, pt-PT, pt-BR, fr-FR,  ..
    String languageCode = getLocale().getLanguage() + LANGUAGE_COUNTRY_SEPARATOR + getLocale().getCountry();

    text = text.replaceAll( "\\{load\\}", "onload=\"load()\"" ); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
    text = text.replaceAll( "\\{body-tag-unload\\}", "" ); //$NON-NLS-1$
    text = text.replaceAll( "#\\{GLOBAL_MESSAGE_SET_NAME\\}", BASE_MESSAGES_FILENAME ); //$NON-NLS-1$
    text = text.replaceAll( "#\\{GLOBAL_MESSAGE_SET_PATH\\}", getMessageFilesCacheUrl() ); //$NON-NLS-1$
    text = text.replaceAll( "#\\{GLOBAL_MESSAGE_SET\\}", buildMessageSetCode( i18nTagsList ) ); //$NON-NLS-1$
    text = text.replaceAll( "#\\{LANGUAGE_CODE\\}", languageCode );
    return text;
  }

  public IReadAccess getDashboardAccess() {
    return dashboardAccess;
  }

  public void setDashboardAccess( IReadAccess dashboardAccess ) {
    this.dashboardAccess = dashboardAccess;
  }

  public IRWAccess getPluginAccess() {
    return pluginAccess;
  }

  public void setPluginAccess( IRWAccess pluginAccess ) {
    this.pluginAccess = pluginAccess;
  }

  public Locale getLocale() {
    return locale;
  }

  public void setLocale( Locale locale ) {
    this.locale = locale;
  }

  public String getPluginStaticBaseContentUrl() {
    return pluginStaticBaseContentUrl;
  }

  public void setPluginStaticBaseContentUrl( String pluginStaticBaseContentUrl ) {
    this.pluginStaticBaseContentUrl = pluginStaticBaseContentUrl;
  }

  public String getDashboardFolderPath() {
    return dashboardFolderPath;
  }

  public void setDashboardFolderPath( String dashboardFolderPath ) {
    this.dashboardFolderPath = dashboardFolderPath;
  }

  public String getBaseTempCacheFolder() {
    return Util.joinPath( BASE_CACHE_DIR, getDashboardFolderPath() );
  }

  /**
   * If the name uses a unix format, sanitize when storing it in BASE_CACHE_DIR, using ISO 3361 / RFC5646 norm
   * <p/>
   *
   * @link http://www.rfc-editor.org/rfc/rfc5646.txt  ( search for 'HYPHEN-MINUS' )
   */
  public String sanitize( String name ) {

    final String REGEX_SEPARATOR_UNIX =
        BASE_MESSAGES_FILENAME + "_[a-z][a-z](" + LANGUAGE_COUNTRY_SEPARATOR_UNIX + "[A-Z][A-Z])" + MESSAGES_EXTENSION;

    if ( !StringUtils.isEmpty( name ) && name.matches( REGEX_SEPARATOR_UNIX ) ) {

      int idx = name.lastIndexOf( LANGUAGE_COUNTRY_SEPARATOR_UNIX );

      StringBuffer sb = new StringBuffer( name.substring( 0, idx ) );
      sb.append( MessageBundlesHelper.LANGUAGE_COUNTRY_SEPARATOR );
      sb.append( name.substring( idx + MessageBundlesHelper.LANGUAGE_COUNTRY_SEPARATOR_UNIX.length() ) );

      return sb.toString();
    }
    return name;
  }

  private String buildMessageSetCode( ArrayList<String> tagsList ) {
    StringBuffer messageCodeSet = new StringBuffer();
    for ( String tag : tagsList ) {
      messageCodeSet
        .append( "\\$('#" ).append( updateSelectorName( tag ) ).append( "').html(jQuery.i18n.prop('" ).append( tag )
        .append( "'));\n" ); //$NON-NLS-1$
    }
    return messageCodeSet.toString();
  }

  private String updateSelectorName( String name ) {
    // If we have the char '.' in the message key substitute it conventionally to '_'
    // when dynamically generating the selector name. The "." character is not permitted in the selector id name
    return name.replace( ".", "_" );
  }

  class LocalizedMessageFilter implements IBasicFileFilter {

    @Override
    public boolean accept( IBasicFile file ) {
      return file != null && !StringUtils.isEmpty( file.getName() )
        && ( file.getName().matches( REGEXP ) || file.getName().matches( REGEXP_ALT ) );
    }
  }
}

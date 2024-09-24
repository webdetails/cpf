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

package pt.webdetails.cpf;

import org.apache.commons.io.FilenameUtils;
import org.apache.commons.io.IOUtils;
import org.apache.commons.io.output.NullOutputStream;
import org.apache.commons.lang.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import pt.webdetails.cpf.repository.api.IContentAccessFactory;
import pt.webdetails.cpf.repository.api.IRWAccess;
import pt.webdetails.cpf.repository.api.IReadAccess;
import pt.webdetails.cpf.repository.util.RepositoryHelper;
import pt.webdetails.cpf.utils.CharsetHelper;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.SequenceInputStream;
import java.io.UnsupportedEncodingException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URLEncoder;
import java.security.DigestInputStream;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.text.DecimalFormat;
import java.util.Enumeration;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;

public abstract class Util {

  private static Log logger = LogFactory.getLog( Util.class );

    /* Detecting whether we were loaded with the PluginClassLoader is a decent
     * proxy for determining whether we are inside Hitachi Vantara. If so, we can go
     * look for the global CPF settings in the solution
     */
  //    private static boolean isPlugin = Util.class.getClassLoader() instanceof PluginClassLoader;

  //
  public static final DecimalFormat DEFAULT_DURATION_FORMAT_SEC = new DecimalFormat( "0.00s" );

  public static final String SEPARATOR = String.valueOf( RepositoryHelper.SEPARATOR );

  /**
   * {@link IOUtils#toString(InputStream)} with null tolerance and ensure the stream is closed.<br>
   */
  public static String toString( InputStream input ) throws IOException {
    if ( input == null ) {
      return null;
    }
    try {
      return IOUtils.toString( input, CharsetHelper.getEncoding() );
    } finally {
      IOUtils.closeQuietly( input );
    }
  }

  public static String toString( byte[] bytes ) {
    try {
      return new String( bytes, CharsetHelper.getEncoding() );
    } catch ( UnsupportedEncodingException e ) {
      logNoEncoding();
      return new String( bytes );
    }
  }

  public static String getExceptionDescription( final Exception e ) {

    final StringBuilder out = new StringBuilder();
    out.append( "[ " ).append( e.getClass().getName() ).append( " ] - " );
    out.append( e.getMessage() );

    if ( e.getCause() != null ) {
      out.append( " .( Cause [ " ).append( e.getCause().getClass().getName() ).append( " ] " );
      out.append( e.getCause().getMessage() );
    }

    return out.toString();
  }

  /**
   * Extracts a string between after the first occurrence of begin, and before the last occurence of end
   *
   * @param source From where to extract
   * @param begin
   * @param end
   * @return
   */
  public static String getContentsBetween( final String source, final String begin, final String end ) {
    if ( source == null ) {
      return null;
    }

    int startIdx = source.indexOf( begin ) + begin.length();
    int endIdx = source.lastIndexOf( end );
    if ( startIdx < 0 || endIdx < 0 ) {
      return null;
    }

    return source.substring( startIdx, endIdx );
  }

  public static String joinPath( String... paths ) {
    List<String> normalizedPaths = new LinkedList<String>();
    for ( String path : paths ) {
      normalizedPaths.add( FilenameUtils.separatorsToUnix( path ) );
    }

    return RepositoryHelper.joinPaths( normalizedPaths );
  }

  private static String bytesToHex( byte[] bytes ) {
    StringBuffer hexString = new StringBuffer();
    for ( int i = 0; i < bytes.length; i++ ) {
      String byteValue = Integer.toHexString( 0xFF & bytes[ i ] );
      hexString.append( byteValue.length() == 2 ? byteValue : "0" + byteValue );
    }
    return hexString.toString();
  }


  public static InputStream toInputStream( String contents ) {
    try {
      return new ByteArrayInputStream( contents.getBytes( CharsetHelper.getEncoding() ) );
    } catch ( UnsupportedEncodingException e ) {
      logNoEncoding();
      return null;
    }
  }

  public static InputStream joinStreams( final InputStream... streams ) {
    return new SequenceInputStream( toEnumeration( streams ) );
  }

  public static String getMd5Digest( String contents ) throws IOException {
    return getMd5Digest( toInputStream( contents ) );
  }

  public static String getMd5Digest( InputStream input ) throws IOException {
    try {
      MessageDigest digest = MessageDigest.getInstance( "MD5" );
      DigestInputStream digestStream = new DigestInputStream( input, digest );
      IOUtils.copy( digestStream, NullOutputStream.NULL_OUTPUT_STREAM );
      return bytesToHex( digest.digest() );
    } catch ( NoSuchAlgorithmException e ) {
      logger.fatal( "No MD5!", e );
      return null;
    }
  }

  public static boolean appendToFile( IRWAccess access, String filePath, InputStream toAppend ) throws IOException {
    if ( !access.fileExists( filePath ) ) {
      return false;
    }
    // be paranoid and make sure the whole file is fully read before there's a chance of being overwritten
    String fileContents = toString( access.getFileInputStream( filePath ) );
    return access.saveFile( filePath, joinStreams( toInputStream( fileContents ), toAppend ) );
  }

  public static String urlEncode( String toEncode ) {
    try {
      return URLEncoder.encode( toEncode, CharsetHelper.getEncoding() );
    } catch ( UnsupportedEncodingException e ) {
      logNoEncoding();
      return null;
    }
  }

  public static String normalizeUri( String path ) {
    try {
      URI uri = new URI( path );
      return uri.normalize().getPath();
    } catch ( URISyntaxException e ) {
      logger.error( "normalizeUri: cannot process path " + path, e );
      return path;
    }
  }

  public static <T> Enumeration<T> toEnumeration( final T[] array ) {
    return new Enumeration<T>() {
      int idx = 0;

      public boolean hasMoreElements() {
        return idx < array.length;
      }

      @Override
      public T nextElement() {
        return array[ idx++ ];
      }
    };
  }

  public static <T> Iterable<T> toIterable( final T[] array ) {
    return new Iterable<T>() {

      public Iterator<T> iterator() {
        return new Iterator<T>() {

          private int idx = 0;

          public boolean hasNext() {
            return idx < array.length;
          }

          public T next() {
            return array[ idx++ ];
          }

          public void remove() {
            throw new UnsupportedOperationException();
          }
        };
      }
    };
  }

  private static void logNoEncoding() {
    logger.fatal( "Encoding " + CharsetHelper.getEncoding() + " not supported!!" );
  }

  /**
   * @return Pretty print elapsed time in seconds, rounded to 2 decimal places, eg "2.34s"
   */
  public static String getElapsedSeconds( long startTime ) {
    return DEFAULT_DURATION_FORMAT_SEC.format( ( System.currentTimeMillis() - startTime ) / 1000.0 );
  }

  /**
   * Helper method that given a path of a resource, allows us to infer on the appropriate IReadAccess object for it
   *
   * @param resourcePath    the path to a resource
   * @param factory         the IReadAccess factory
   * @param pluginId        the plugin's id
   * @param pluginSystemDir the plugin's base system directory
   * @param pluginRepoDir   the plugin's base repository directory
   * @return appropriate IReadAccess object for the given resource
   */
  public static IReadAccess getAppropriateReadAccess( final String resourcePath, final IContentAccessFactory factory,
                                                      final String pluginId, final String pluginSystemDir,
                                                      final String pluginRepoDir ) {

    if ( StringUtils.isEmpty( resourcePath ) || StringUtils.isEmpty( pluginId )
        || StringUtils.isEmpty( pluginSystemDir ) || StringUtils.isEmpty( pluginRepoDir ) || factory == null ) {
      return null;
    }

    String id = pluginId.endsWith( SEPARATOR ) ? pluginId : pluginId + SEPARATOR;
    String repoDir = pluginRepoDir.endsWith( SEPARATOR ) ? pluginRepoDir : pluginRepoDir + SEPARATOR;
    String systemDir = pluginSystemDir.endsWith( SEPARATOR ) ? pluginSystemDir : pluginSystemDir + SEPARATOR;

    // remove leading and trailing SEPARATOR *and* also performs String.trim()
    String resource = StringUtils.strip( resourcePath, SEPARATOR );

    if ( resource.regionMatches( true, 0, systemDir, 0, systemDir.length() ) ) {

      resource = resource.replaceFirst( systemDir, "" ); // trim the 'system'

      if ( resource.regionMatches( true, 0, id, 0, id.length() ) ) {
        // system dir - this plugin id
        return factory.getPluginSystemReader( null );

      } else {
        // system dir - some other plugin id; lets find out which one
        String otherPluginId = resource.substring( 0, resource.indexOf( SEPARATOR ) );
        return factory.getOtherPluginSystemReader( otherPluginId, null );
      }

    } else if ( resource.regionMatches( true, 0, repoDir, 0, repoDir.length() ) ) {

      // plugin repository dir
      return factory.getPluginRepositoryReader( null );

    } else {

      // one of two:
      // A - already trimmed system resource (ex: 'resources/templates/1-empty-structure.cdfde') for the pluginId
      // B - user solution resource (ex: 'public/plugin-samples/pentaho-cdf-dd/styles/my-style.css')

      if ( factory.getPluginSystemReader( null ).fileExists( resourcePath ) ) {
        return factory.getPluginSystemReader( null );

      } else if ( factory.getUserContentAccess( null ).fileExists( resourcePath ) ) {
        // user solution dir
        return factory.getUserContentAccess( null );
      }
    }

    return null; // reaching this point, there's not much more left to be done, and null is returned
  }

}

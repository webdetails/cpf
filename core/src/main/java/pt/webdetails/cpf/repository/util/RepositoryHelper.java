package pt.webdetails.cpf.repository.util;

import org.apache.commons.io.FilenameUtils;
import org.apache.commons.io.IOUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import pt.webdetails.cpf.repository.api.IBasicFile;
import pt.webdetails.cpf.repository.api.IBasicFileFilter;
import pt.webdetails.cpf.repository.api.IRWAccess;
import pt.webdetails.cpf.repository.api.IReadAccess;

import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.util.Enumeration;
import java.util.List;

/**
 * Utilities around files and paths.
 */
public class RepositoryHelper {

  private static Log logger = LogFactory.getLog( RepositoryHelper.class );

  private RepositoryHelper() {
  }

  /**
   * All classes in repository namespace should work with slashes.
   */
  public static final char SEPARATOR = '/';

  /**
   * differs from {@link FilenameUtils#concat(String, String)} in that the second path will never be treated as
   * absolute;
   *
   * @param first  can be null
   * @param second should be something if first is null!
   * @return normalized concatenation of both paths
   */
  public static String appendPath( String first, String second ) {
    if ( StringUtils.isEmpty( first ) ) {
      return second;
    } else if ( StringUtils.isEmpty( second ) ) {
      return first;
    }
    int sepCount = 0;
    if ( first.charAt( first.length() - 1 ) == SEPARATOR ) {
      sepCount++;
    }
    if ( second.charAt( 0 ) == SEPARATOR ) {
      sepCount++;
    }
    switch ( sepCount ) {
      case 0:
        return first + SEPARATOR + second;
      case 2:
        return first + second.substring( 1 );
      default:
        return first + second;
    }
  }

  public static StringBuilder appendPath( StringBuilder builder, String path ) {
    int sepCount = 0;
    if ( builder.length() > 0 && builder.charAt( builder.length() - 1 ) == SEPARATOR ) {
      sepCount++;
    }
    if ( path.charAt( 0 ) == SEPARATOR ) {
      sepCount++;
    }
    switch ( sepCount ) {
      case 2:
        return builder.append( path.substring( 1 ) );
      case 0:
        builder.append( SEPARATOR ); //fall
      default:
        return builder.append( path );
    }
  }

  public static String joinPaths( String... paths ) {
    StringBuilder builder = new StringBuilder();
    for ( String path : paths ) {
      if ( !StringUtils.isEmpty( path ) ) {
        appendPath( builder, path );
      }
    }
    return builder.toString();
  }

  public static String joinPaths( List<String> paths ) {
    StringBuilder builder = new StringBuilder();
    for ( String path : paths ) {
      if ( !StringUtils.isEmpty( path ) ) {
        appendPath( builder, path );
      }
    }
    return builder.toString();
  }

  /**
   * Similar to {@link ClassLoader#getResource(String)}, except the lookup is in reverse order.<br> i.e. returns the
   * resource from the supplied classLoader or the one closest to it in the hierarchy, instead of the closest to the
   * root class loader
   *
   * @param classLoader The class loader to fetch from
   * @param path        The resource path relative to url base
   * @return A URL object for reading the resource, or null if the resource could not be found or the invoker doesn't
   * have adequate privileges to get the resource.
   * @see ClassLoader#getResource(String)
   * @see ClassLoader#getResources(String)
   */

  public static URL getClosestResource( ClassLoader classLoader, String path ) {
    URL resource = null;
    try {
      // The last resource will be from the nearest ClassLoader.
      Enumeration<URL> resourceCandidates = classLoader.getResources( path );
      while ( resourceCandidates.hasMoreElements() ) {
        resource = resourceCandidates.nextElement();
      }
    } catch ( IOException ioe ) {
      // ignore exception - it's OK if file is not found
    }
    return resource;
  }

  /**
   * Make one path relative to the other.
   *
   * @param basePath base directory from which
   * @param fullPath targetPath is calculated to this file
   * @return fullPath as a relative path to basePath
   */
  public static String relativizePath( String basePath, String fullPath, boolean assumeCommon ) {

    // Normalize the paths
    String normalizedTargetPath = FilenameUtils.separatorsToUnix( FilenameUtils.normalizeNoEndSeparator( fullPath ) );
    String normalizedBasePath = FilenameUtils.separatorsToUnix( FilenameUtils.normalizeNoEndSeparator( basePath ) );

    if ( StringUtils.isEmpty( normalizedBasePath ) ) {
      return fullPath;
    }
    if ( StringUtils.isEmpty( normalizedTargetPath ) ) {
      return basePath;
    }

    String[] base = StringUtils.split( normalizedBasePath, SEPARATOR );
    String[] target = StringUtils.split( normalizedTargetPath, SEPARATOR );

    // First get all the common elements. Store them as a string,
    // and also count how many of them there are.
    StringBuilder common = new StringBuilder();

    int commonIndex = 0;

    while ( commonIndex < target.length && commonIndex < base.length
      && target[ commonIndex ].equals( base[ commonIndex ] ) ) {
      common.append( target[ commonIndex ] );
      common.append( SEPARATOR );
      commonIndex++;
    }

    if ( commonIndex == 0 && !assumeCommon ) {
      // No single common path element.
      // These paths cannot be relativized.
      throw new IllegalArgumentException(
        "No common path element found for '" + normalizedTargetPath
          + "' and '" + normalizedBasePath + "'" );
    } else if ( normalizedBasePath.startsWith( "" + SEPARATOR ) && normalizedTargetPath.startsWith( "" + SEPARATOR ) ) {
      // starting slash if had one
      common.insert( 0, SEPARATOR );
    }

    StringBuilder relative = new StringBuilder();
    if ( base.length != commonIndex ) {
      int numDirsUp = base.length - commonIndex;

      for ( int i = 0; i < numDirsUp; i++ ) {
        relative.append( ".." + SEPARATOR );
      }
    }
    if ( common.length() < normalizedTargetPath.length() ) {
      relative.append( normalizedTargetPath.substring( common.length() ) );
    }

    return relative.toString();
  }

  /**
   * {@link FilenameUtils#normalize(String)} forcing /.
   */
  public static String normalize( String path ) {
    return FilenameUtils.separatorsToUnix( FilenameUtils.normalize( path ) );
  }

  /**
   * {@link FilenameUtils#getPathNoEndSeparator(String)} forcing /.
   */
  public static String getPathNoEndSeparator( String path ) {
    return FilenameUtils.separatorsToUnix( FilenameUtils.getPathNoEndSeparator( path ) );
  }

  /**
   * Like {@link FilenameUtils#getExtension(String)} but won't interpret unix hidden files as extensions
   *
   * @param fileName
   * @return
   */
  public static String getExtension( String fileName ) {
    if ( fileName == null ) {
      return null;
    }

    if ( fileName.startsWith( "." ) ) {
      return StringUtils.lowerCase( FilenameUtils.getExtension( fileName.substring( 1 ) ) );
    }
    return StringUtils.lowerCase( FilenameUtils.getExtension( fileName ) );
  }

  public static String toJQueryFileTree( String baseDir, IBasicFile[] files ) {
    StringBuilder out = new StringBuilder();
    out.append( "<ul class=\"jqueryFileTree\" style=\"display: none;\">" );

    for ( IBasicFile file : files ) {
      if ( file.isDirectory() ) {
        out.append( "<li class=\"directory collapsed\"><a href=\"#\" rel=\"" + file.getPath() + "/\">" + file.getName()
            + "</a></li>" );
      }
    }

    for ( IBasicFile file : files ) {
      if ( !file.isDirectory() ) {
        out.append(
            "<li class=\"file ext_" + file.getExtension() + "\"><a href=\"#\" rel=\"" + file.getPath() + "\">" + file
              .getName() + "</a></li>" );
      }
    }
    out.append( "</ul>" );
    return out.toString();
  }

  public static String toJSON( String baseDir, IBasicFile[] files ) throws JSONException {

    JSONArray arr = new JSONArray();

    for ( IBasicFile file : files ) {
      JSONObject json = new JSONObject();
      json.put( "path", baseDir );
      json.put( "name", file.getName() );
      json.put( "label", file.getName() );

      if ( file.isDirectory() ) {
        json.put( "type", "dir" );
      } else {
        int dotIndex = file.getName().lastIndexOf( '.' );
        String ext = dotIndex > 0 ? file.getName().substring( dotIndex + 1 ) : "";
        json.put( "ext", ext );
        json.put( "type", "file" );
      }
      arr.put( json );
    }

    return arr.toString();
  }

  /**
   * Copy a file accross different sources
   *
   * @param reader  where to fetch file
   * @param inFile  path to file to read
   * @param writer  where to write file
   * @param outFile path to file to be written
   * @return if saved ok
   */
  public static boolean copy( IReadAccess reader, String inFile, IRWAccess writer, String outFile ) {
    InputStream input = null;
    try {
      input = reader.getFileInputStream( inFile );
      return writer.saveFile( outFile, input );
    } catch ( IOException e ) {
      logger.error( "Couldn't read " + inFile + " in " + reader );
      return false;
    } finally {
      IOUtils.closeQuietly( input );
    }
  }

  public static IBasicFileFilter getSimpleExtensionFilter( final String extension ) {
    return new IBasicFileFilter() {
      @Override
      public boolean accept( IBasicFile file ) {
        return StringUtils.equals( extension, file.getExtension() );
      }
    };
  }

}

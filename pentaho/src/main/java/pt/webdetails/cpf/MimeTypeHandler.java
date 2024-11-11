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

package pt.webdetails.cpf;

import org.apache.commons.lang.StringUtils;
import org.pentaho.platform.util.web.MimeHelper;
import pt.webdetails.cpf.utils.MimeTypes;

/**
 * This class handles all incoming mime type resolve requests, using the following logic:
 * <p/>
 * 1 - call (@link org.pentaho.platform.util.web.MimeHelper ) to resolve the mime type for the provided file extension
 * <p/>
 * 2 - if the above class not able to resolve / is not available / whatever,  then fallback to (@link
 * pt.webdetails.cpf.utils.MimeHelper) for resolve
 */
public class MimeTypeHandler {

  public static String getMimeTypeFromFileType( final MimeTypes.FileType fileType ) {
    return getMimeTypeFromFileType( fileType, null );
  }

  public static String getMimeTypeFromFileType( final MimeTypes.FileType fileType, String defaultMimeType ) {
    return getMimeTypeFromExtension( fileType.toString(), defaultMimeType );
  }

  public static String getMimeTypeFromExtension( final String extension ) {
    return getMimeTypeFromExtension( extension, null );
  }

  public static String getMimeTypeFromExtension( final String extension, String defaultMimeType ) {

    if ( StringUtils.isEmpty( extension ) ) {
      return defaultMimeType;
    }

    // use org.pentaho.platform.util.web.MimeHelper (expects a dot + file extension)
    String resolvedMimeType =
      MimeHelper.getMimeTypeFromExtension( extension.startsWith( "." ) ? extension : "." + extension );

    if ( StringUtils.isEmpty( resolvedMimeType ) ) {

      //fallback to pt.webdetails.cpf.utils.MimeTypes ( does *not* expect a dot )
      resolvedMimeType =
        MimeTypes.getMimeTypeFromExt( extension.startsWith( "." ) ? extension.replaceFirst( ".", "" ) : extension );
    }

    return !StringUtils.isEmpty( resolvedMimeType ) ? resolvedMimeType : defaultMimeType;
  }

  public static String getMimeTypeFromFileName( final String filename ) {
    return getMimeTypeFromFileName( filename, null );
  }

  public static String getMimeTypeFromFileName( final String filename, String defaultMimeType ) {

    // use org.pentaho.platform.util.web.MimeHelper
    String resolvedMimeType = MimeHelper.getMimeTypeFromFileName( filename );

    if ( StringUtils.isEmpty( resolvedMimeType ) ) {

      //fallback to pt.webdetails.cpf.utils.MimeTypes
      resolvedMimeType = MimeTypes.getMimeType( filename );
    }

    return !StringUtils.isEmpty( resolvedMimeType ) ? resolvedMimeType : defaultMimeType;
  }
}

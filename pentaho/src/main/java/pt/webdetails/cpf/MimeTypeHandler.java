/*!
* This program is free software; you can redistribute it and/or modify it under the
* terms of the GNU Lesser General Public License, version 2.1 as published by the Free Software
* Foundation.
*
* You should have received a copy of the GNU Lesser General Public License along with this
* program; if not, you can obtain a copy at http://www.gnu.org/licenses/old-licenses/lgpl-2.1.html
* or from the Free Software Foundation, Inc.,
* 51 Franklin Street, Fifth Floor, Boston, MA 02110-1301 USA.
*
* This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY;
* without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.
* See the GNU Lesser General Public License for more details.
*
* Copyright (c) 2002-2017 Hitachi Vantara..  All rights reserved.
*/
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

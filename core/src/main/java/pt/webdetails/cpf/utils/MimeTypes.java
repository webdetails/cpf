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

import org.apache.commons.lang.StringUtils;
import pt.webdetails.cpf.repository.util.RepositoryHelper;

import java.util.EnumMap;

/**
 * Utilities class for creating MIME content type strings
 */
public class MimeTypes {


  public static final String CSS = "text/css";
  public static final String JAVASCRIPT = "text/javascript";
  public static final String PLAIN_TEXT = "text/plain";
  public static final String HTML = "text/html";
  public static final String XML = "text/xml";
  public static final String JPEG = "img/jpeg";
  public static final String PNG = "image/png";
  public static final String GIF = "image/gif";
  public static final String BMP = "image/bmp";
  public static final String SVG = "image/svg+xml";
  public static final String JSON = "application/json";
  public static final String PDF = "application/pdf";
  public static final String DOC = "application/msword";
  public static final String DOCX = "application/msword";
  public static final String XLS = "application/msexcel";
  public static final String XLSX = "application/msexcel";
  public static final String PPT = "application/mspowerpoint";
  public static final String PPTX = "application/mspowerpoint";
  public static final String ZIP = "application/zip";
  public static final String CSV = "text/csv";


  public enum FileType {

    JPG, JPEG, PNG, GIF, BMP, SVG, JS, CSS, HTML, HTM, XML,
    PDF, TXT, DOC, DOCX, XLS, XLSX, PPT, PPTX, ZIP, CSV,
    CDA, CDFDE, WCDF, XCDF;

    public static FileType parse( String value ) {
      return parse( value, null );
    }

    public static FileType parse( String value, FileType defaultType ) {
      try {
        return valueOf( StringUtils.upperCase( value ) );
      } catch ( IllegalArgumentException e ) {
        return defaultType;
      }
    }

  }

  protected static final EnumMap<FileType, String> mimeTypes = new EnumMap<>( FileType.class );

  static {
        /*
         * Image types
         */
    mimeTypes.put( FileType.JPG, JPEG );
    mimeTypes.put( FileType.JPEG, JPEG );
    mimeTypes.put( FileType.PNG, PNG );
    mimeTypes.put( FileType.GIF, GIF );
    mimeTypes.put( FileType.BMP, BMP );
    mimeTypes.put( FileType.SVG, SVG );

        /*
         * HTML (and related) types
         */
    // Deprecated, should be application/javascript, but IE doesn't like that
    mimeTypes.put( FileType.JS, JAVASCRIPT );
    mimeTypes.put( FileType.HTM, HTML );
    mimeTypes.put( FileType.HTML, HTML );
    mimeTypes.put( FileType.CSS, CSS );
    mimeTypes.put( FileType.CSV, CSV );
    mimeTypes.put( FileType.XML, XML );
    mimeTypes.put( FileType.TXT, PLAIN_TEXT );

    // CTools types
    mimeTypes.put( FileType.CDA, XML );
    mimeTypes.put( FileType.CDFDE, JSON );
    mimeTypes.put( FileType.XCDF, XML );
    mimeTypes.put( FileType.WCDF, XML );

    // Other types
    mimeTypes.put( FileType.PDF, PDF );
  }

  public static String getMimeType( String fileName ) {

    String extension = RepositoryHelper.getExtension( fileName );
    return getMimeTypeFromExt( extension );

  }


  /**
   * @param extension file extension, lowercase, no dot
   * @return mimetype, defaults to unknown
   */
  public static String getMimeTypeFromExt( String extension ) {
    FileType type = FileType.parse( extension, null );
    return getMimeType( type );
  }

  public static String getMimeType( FileType fileType ) {
    return getMimeType( fileType, "application/unknown" ); //TODO: default to plain text?
  }

  public static String getMimeType( FileType fileType, String defaultMimeType ) {
    if ( fileType == null ) {
      return defaultMimeType;
    }
    String mimeType = mimeTypes.get( fileType );
    return mimeType == null ? defaultMimeType : mimeType;
  }

}

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

import java.util.EnumMap;
import org.apache.commons.lang.StringUtils;
import org.apache.commons.logging.LogFactory;

/**
 *
 * @author Luís Paulo Silva
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

        JPG, JPEG, PNG, GIF, BMP, JS, CSS, HTML, HTM, XML,
        SVG, PDF, TXT, DOC, DOCX, XLS, XLSX, PPT, PPTX, ZIP,CSV;

        public static FileType parse(String value) {
            return valueOf(StringUtils.upperCase(value));
        }
        
        
    }
    
    protected static final EnumMap<FileType, String> mimeTypes = new EnumMap<FileType, String>(FileType.class);

    static {
        /*
         * Image types
         */
        mimeTypes.put(FileType.JPG, JPEG);
        mimeTypes.put(FileType.JPEG, JPEG);
        mimeTypes.put(FileType.PNG, PNG);
        mimeTypes.put(FileType.GIF, GIF);
        mimeTypes.put(FileType.BMP, BMP);

        /*
         * HTML (and related) types
         */
        // Deprecated, should be application/javascript, but IE doesn't like that
        mimeTypes.put(FileType.JS, JAVASCRIPT);
        mimeTypes.put(FileType.HTM, HTML);
        mimeTypes.put(FileType.HTML, HTML);
        mimeTypes.put(FileType.CSS, CSS);
        mimeTypes.put(FileType.CSV, CSV);
        mimeTypes.put(FileType.XML, XML);
        mimeTypes.put(FileType.TXT, PLAIN_TEXT);
    }

    public static String getMimeType(String fileName) {
        String[] fileNameSplit = StringUtils.split(fileName, '.');// fileName.split("\\.");
        try {
            return getMimeType(FileType.valueOf(fileNameSplit[fileNameSplit.length - 1].toUpperCase()));
        } catch (Exception e) {
            LogFactory.getLog(MimeTypes.class).error("Unrecognized extension", e);
            return "";
        }
    }

    public static String getMimeType(FileType fileType) {
        if (fileType == null) {
            return null;
        }
        String mimeType = mimeTypes.get(fileType);
        return mimeType == null ? "" : mimeType;
    }
    
}

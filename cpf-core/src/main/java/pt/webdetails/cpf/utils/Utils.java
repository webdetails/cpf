/* This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this file,
 * You can obtain one at http://mozilla.org/MPL/2.0/. */

package pt.webdetails.cpf.utils;

import java.util.EnumMap;
import org.apache.commons.lang.StringUtils;
import org.apache.commons.logging.LogFactory;

/**
 *
 * @author Sammy Guergachi <sguergachi at gmail.com>
 */
public class Utils {

    public enum FileType {

        JPG, JPEG, PNG, GIF, BMP, JS, CSS, HTML, HTM, XML,
        SVG, PDF, TXT, DOC, DOCX, XLS, XLSX, PPT, PPTX;

        public static FileType parse(String value) {
            return valueOf(StringUtils.upperCase(value));
        }
    }

    public static class MimeType {

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
    }
    public static final EnumMap<FileType, String> mimeTypes = new EnumMap<FileType, String>(FileType.class);

    static {
        /*
         * Image types
         */
        mimeTypes.put(FileType.JPG, MimeType.JPEG);
        mimeTypes.put(FileType.JPEG, MimeType.JPEG);
        mimeTypes.put(FileType.PNG, MimeType.PNG);
        mimeTypes.put(FileType.GIF, MimeType.GIF);
        mimeTypes.put(FileType.BMP, MimeType.BMP);

        /*
         * HTML (and related) types
         */
        // Deprecated, should be application/javascript, but IE doesn't like that
        mimeTypes.put(FileType.JS, MimeType.JAVASCRIPT);
        mimeTypes.put(FileType.HTM, MimeType.HTML);
        mimeTypes.put(FileType.HTML, MimeType.HTML);
        mimeTypes.put(FileType.CSS, MimeType.CSS);
        mimeTypes.put(FileType.XML, MimeType.XML);
        mimeTypes.put(FileType.TXT, MimeType.PLAIN_TEXT);
    }

    public static String getMimeType(String fileName) {
        String[] fileNameSplit = StringUtils.split(fileName, '.');
        try {
            return getMimeType(FileType.valueOf(fileNameSplit[fileNameSplit.length - 1].toUpperCase()));
        } catch (Exception e) {
            LogFactory.getLog(Utils.class).error("Unrecognized extension", e);
            return "";
        }
    }

    public static String getMimeType(FileType fileType) {
        if (fileType == null) {
            return "";
        }
        String mimeType = mimeTypes.get(fileType);
        return mimeType == null ? "" : mimeType;
    }
}

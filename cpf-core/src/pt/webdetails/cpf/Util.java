/* This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this file,
 * You can obtain one at http://mozilla.org/MPL/2.0/. */

package pt.webdetails.cpf;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.security.DigestInputStream;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

import org.apache.commons.io.IOUtils;
import org.apache.commons.io.output.NullOutputStream;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import pt.webdetails.cpf.repository.util.RepositoryHelper;
import pt.webdetails.cpf.utils.CharsetHelper;

public abstract class Util {

  private static Log logger = LogFactory.getLog(Util.class);

    /* Detecting whether we were loaded with the PluginClassLoader is a decent
     * proxy for determining whether we are inside Pentaho. If so, we can go
     * look for the global CPF settings in the solution
     */
//    private static boolean isPlugin = Util.class.getClassLoader() instanceof PluginClassLoader;

  /**
   * {@link IOUtils#toString(InputStream)} with null tolerance and ensure the stream is closed.<br>
   */
    public static String toString(InputStream input) throws IOException {
      if (input == null) {
        return null;
      }
      try {
        return IOUtils.toString( input, CharsetHelper.getEncoding() );
      }
      finally {
        IOUtils.closeQuietly( input );
      }
    }

    
    public static String getExceptionDescription(final Exception e) {

        final StringBuilder out = new StringBuilder();
        out.append("[ ").append(e.getClass().getName()).append(" ] - ");
        out.append(e.getMessage());

        if (e.getCause() != null) {
            out.append(" .( Cause [ ").append(e.getCause().getClass().getName()).append(" ] ");
            out.append(e.getCause().getMessage());
        }

        return out.toString();
    }

    /**
     * Extracts a string between after the first occurrence of begin, and before
     * the last occurence of end
     *
     * @param source From where to extract
     * @param begin
     * @param end
     * @return
     */
    public static String getContentsBetween(final String source, final String begin, final String end) {
        if (source == null) {
            return null;
        }

        int startIdx = source.indexOf(begin) + begin.length();
        int endIdx = source.lastIndexOf(end);
        if (startIdx < 0 || endIdx < 0) {
            return null;
        }

        return source.substring(startIdx, endIdx);
    }

    public static String joinPath(String... paths) {
      return RepositoryHelper.joinPaths( paths );
      //return StringUtils.defaultString(StringUtils.join(paths, "/")).replaceAll("\\\\", "/").replaceAll("/+", "/");
    }

    private static String bytesToHex(byte[] bytes) {
      StringBuffer hexString = new StringBuffer();
      for (int i = 0; i < bytes.length; i++)
      {
        String byteValue = Integer.toHexString(0xFF & bytes[i]);
        hexString.append(byteValue.length() == 2 ? byteValue : "0" + byteValue);
      }
      return hexString.toString();
    }


    public static InputStream toInputStream(String contents) {
      try {
        return new ByteArrayInputStream(contents.getBytes( CharsetHelper.getEncoding() ));
      } catch ( UnsupportedEncodingException e ) {
        logNoEncoding();
        return null;
      }
    }

    public static String getMd5Digest(String contents) throws IOException {
      return getMd5Digest( toInputStream( contents ) );
    }

    public static String getMd5Digest(InputStream input) throws IOException {
      try {
        MessageDigest digest = MessageDigest.getInstance("MD5");
        DigestInputStream digestStream = new DigestInputStream(input, digest);
        IOUtils.copy(digestStream, NullOutputStream.NULL_OUTPUT_STREAM);
        return bytesToHex(digest.digest());
      } catch ( NoSuchAlgorithmException e ) {
        logger.fatal("No MD5!", e);
        return null;
      }
    }

    public static String urlEncode ( String toEncode ) {
      try {
        return URLEncoder.encode( toEncode, CharsetHelper.getEncoding() );
      } catch ( UnsupportedEncodingException e ) {
        logNoEncoding();
        return null;
      }
    }

    private static void logNoEncoding() {
      logger.fatal("Encoding " + CharsetHelper.getEncoding() + " not supported!!" );
    }
}

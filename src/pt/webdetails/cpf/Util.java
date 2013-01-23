/* This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this file,
 * You can obtain one at http://mozilla.org/MPL/2.0/. */

package pt.webdetails.cpf;

import org.apache.commons.lang.StringUtils;
import org.pentaho.platform.plugin.services.pluginmgr.PluginClassLoader;

public class Util {

    /* Detecting whether we were loaded with the PluginClassLoader is a decent
     * proxy for determining whether we are inside Pentaho. If so, we can go
     * look for the global CPF settings in the solution
     */
    private static boolean isPlugin = Util.class.getClassLoader() instanceof PluginClassLoader;

    public static String getExceptionDescription(final Exception e) {

        final StringBuilder out = new StringBuilder();
        out.append("[ ").append(e.getClass().getName()).append(" ] - ");
        out.append(e.getMessage());

        if (e.getCause() != null) {
            out.append(" .( Cause [ ").append(e.getCause().getClass().getName()).append(" ] ");
            out.append(e.getCause().getMessage());
        }

        e.printStackTrace();
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

    public static final boolean IsNullOrEmpty(final String str) {
        return (str == null || str.trim().length() == 0);
    }

    public static String joinPath(String... paths) {
        return StringUtils.defaultString(StringUtils.join(paths, "/")).replaceAll("\\\\", "/").replaceAll("/+", "/");
    }

    public static boolean isPlugin() {
        return isPlugin;
    }
}

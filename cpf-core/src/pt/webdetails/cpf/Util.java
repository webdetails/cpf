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

package pt.webdetails.cpf;

import org.apache.commons.lang.StringUtils;

public abstract class Util {

    /* Detecting whether we were loaded with the PluginClassLoader is a decent
     * proxy for determining whether we are inside Pentaho. If so, we can go
     * look for the global CPF settings in the solution
     */
//    private static boolean isPlugin = Util.class.getClassLoader() instanceof PluginClassLoader;

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

    public static String joinPath(String... paths) {
        return StringUtils.defaultString(StringUtils.join(paths, "/")).replaceAll("\\\\", "/").replaceAll("/+", "/");
    }

}

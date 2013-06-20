/* This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this file,
 * You can obtain one at http://mozilla.org/MPL/2.0/. */

package pt.webdetails.cpf;

import java.util.Iterator;

import org.pentaho.platform.api.engine.IParameterProvider;

import pt.webdetails.cpf.http.CommonParameterProvider;
import pt.webdetails.cpf.http.ICommonParameterProvider;

/**
 * XXX depends on CommonParameterProvider, which isn't done
 */
public class WrapperUtils {

    @SuppressWarnings("unchecked")
    public static ICommonParameterProvider wrapParamProvider(
            IParameterProvider requestParams) {

        //dont hardcode the implementation
        ICommonParameterProvider result = new CommonParameterProvider();

        Iterator<String> names = requestParams.getParameterNames();

        while (names.hasNext()) {
            String name = names.next();
            Object value = requestParams.getParameter(name);
            result.put(name, value);
        }

        return result;
    }
}

/*!
* Copyright 2002 - 2017 Webdetails, a Hitachi Vantara company.  All rights reserved.
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

import java.util.Iterator;

import org.pentaho.platform.api.engine.IParameterProvider;

import pt.webdetails.cpf.http.CommonParameterProvider;
import pt.webdetails.cpf.http.ICommonParameterProvider;

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

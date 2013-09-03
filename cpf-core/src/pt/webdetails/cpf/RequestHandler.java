/* This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this file,
 * You can obtain one at http://mozilla.org/MPL/2.0/. */

package pt.webdetails.cpf;

import java.io.OutputStream;
import pt.webdetails.cpf.http.ICommonParameterProvider;

/**
 *
 * @author pdpi
 */
public interface RequestHandler {
    public void call(OutputStream out, ICommonParameterProvider pathParams, ICommonParameterProvider requestParams);
}

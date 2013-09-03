/* This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this file,
 * You can obtain one at http://mozilla.org/MPL/2.0/. */

package pt.webdetails.cpf;

import java.io.OutputStream;
import org.pentaho.platform.api.engine.IParameterProvider;

/**
 *
 * @author pdpi
 */
public interface RequestHandler {
    public void call(OutputStream out, IParameterProvider pathParams, IParameterProvider requestParams);
}

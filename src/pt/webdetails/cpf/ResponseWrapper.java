/* This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this file,
 * You can obtain one at http://mozilla.org/MPL/2.0/. */

package pt.webdetails.cpf;

import javax.servlet.http.HttpServletResponse;

/**
 *
 * @author pdpi
 */
public class ResponseWrapper {

    private HttpServletResponse response;

    public ResponseWrapper(HttpServletResponse response) {
        this.response = response;
    }

    public HttpServletResponse getResponse() {
        return response;
    }

    public void setResponseHeader(final String header, String value) {
        if (response != null) {
            response.setHeader(header, value);
        }
    }

    public void setOutputType(String type) {
        setResponseHeader("Content-Type", type);
    }
}

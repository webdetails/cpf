/* This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this file,
 * You can obtain one at http://mozilla.org/MPL/2.0/. */

package pt.webdetails.cpf.datasources;

import java.util.Date;
import java.util.List;

/**
 *
 * @author pdpi
 */
public interface Datasource {

    public String execute();

    public void setParameter(String param, String val);

    public void setParameter(String param, String[] val);

    public void setParameter(String param, Date val);

    public void setParameter(String param, List<Object> val);
}

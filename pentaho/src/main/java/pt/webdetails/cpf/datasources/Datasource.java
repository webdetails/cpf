/*! ******************************************************************************
 *
 * Pentaho
 *
 * Copyright (C) 2024 by Hitachi Vantara, LLC : http://www.pentaho.com
 *
 * Use of this software is governed by the Business Source License included
 * in the LICENSE.TXT file.
 *
 * Change Date: 2028-08-13
 ******************************************************************************/


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

/*! ******************************************************************************
 *
 * Pentaho
 *
 * Copyright (C) 2024 by Hitachi Vantara, LLC : http://www.pentaho.com
 *
 * Use of this software is governed by the Business Source License included
 * in the LICENSE.TXT file.
 *
 * Change Date: 2029-07-20
 ******************************************************************************/


package pt.webdetails.cpf.datasources;

public class DatasourceFactory {

    public DatasourceFactory() {
    }

    public static Datasource createDatasource(String type) {

        if (type.toUpperCase().equals("CDA")) {
            return new CdaDatasource();
        } else {
            return null;
        }

    }
}
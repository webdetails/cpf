/* This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this file,
 * You can obtain one at http://mozilla.org/MPL/2.0/. */
package pt.webdetails.cpk.elements.impl.kettleOutputs;

import java.util.ArrayList;

/**
 *
 * @author Luís Paulo Silva
 */
public class RowsJson {
    private ArrayList<Object[]> rows;
    
    public RowsJson(ArrayList<Object[]> rows){
    }
    
    private void init(ArrayList<Object[]> rows){
        this.rows = rows;
    }
    
}

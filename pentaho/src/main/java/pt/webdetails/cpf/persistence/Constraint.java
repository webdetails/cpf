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

package pt.webdetails.cpf.persistence;

import java.text.MessageFormat;

/**
 *
 * @author pdpi
 */
public class Constraint implements Item {

    static final String BETWEEN = "({0} > {1} and {0} < {2})",
            GT = "{0} > {1}",
            LT = "{0} < {1}",
            EQ = "{0} = {1}",
            NE = "{0} != {1}";
    Filter parent;
    String parameter, filter;

    public Constraint(Filter parent, String parameter) {
        this.parent = parent;
        this.parameter = parameter;
    }

    private Filter constraint(String templ, Object... values) {
        filter = MessageFormat.format(templ, values);
        parent.addConstraint(this);
        return parent;
    }

    public Filter between(Number min, Number max) {
        return constraint(BETWEEN, parameter, min, max);
    }

    public Filter greaterThan(Number min) {
        return constraint(GT, parameter, min);
    }

    public Filter lessThan(Number min) {
        return constraint(LT, parameter, min);
    }

    public Filter equalTo(Object obj) {
        String v = obj.toString();
        v = "\"" + v.replaceAll("\"", "\\\"") + "\"";
        return constraint(EQ, parameter, v);
    }

    public Filter notEqualTo(Object obj) {
        String v = obj.toString();
        v = "\"" + v.replaceAll("\"", "\\\\\"") + "\"";
        return constraint(NE, parameter, v);
    }

    @Override
    public String toString() {
        return filter;
    }
}

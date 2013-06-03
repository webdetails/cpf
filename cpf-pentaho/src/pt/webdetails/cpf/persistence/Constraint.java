/* This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this file,
 * You can obtain one at http://mozilla.org/MPL/2.0/. */

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
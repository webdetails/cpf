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


package pt.webdetails.cpf.persistence;

import java.util.ArrayList;
import java.util.List;

/**
 *
 * @author pdpi
 */
public class Filter implements Item {

    private List<Item> constraints;

    public Filter() {
        constraints = new ArrayList<Item>();
    }

    public Constraint where(String param) {
        return new Constraint(this, param);
    }

    void addConstraint(Item c) {
        constraints.add(c);
    }

    public Filter or() {
        constraints.add(Chain.OR);
        return this;
    }

    public Filter or(Filter f) {
        constraints.add(Chain.OR);
        constraints.add(f);
        return this;
    }

    public Filter and() {
        constraints.add(Chain.AND);
        return this;
    }

    public Filter and(Filter f) {
        constraints.add(Chain.AND);
        constraints.add(f);
        return this;
    }

    @Override
    public String toString() {
        StringBuilder builder = new StringBuilder();
        builder.append("(");
        for (Item constraint : constraints) {
            builder.append(constraint.toString());
        }
        builder.append(")");
        return builder.toString();
    }

    public Filter either(Item c) {
        addConstraint(c);
        return this;
    }

    public Filter both(Item c) {
        addConstraint(c);
        return this;
    }

}

interface Item {
}

class Chain implements Item {

    static final Chain OR = new Chain("or");
    static final Chain AND = new Chain("and");
    String particle;

    public Chain(String part) {
        particle = part;
    }

    @Override
    public String toString() {
        return " " + particle + " ";
    }
}


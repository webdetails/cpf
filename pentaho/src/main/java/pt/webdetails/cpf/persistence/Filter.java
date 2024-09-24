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


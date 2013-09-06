/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package pt.webdetails.cpf.web;

/**
 *
 * @author diogomariano
 */

import java.util.Collection;
import java.util.Collections;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import org.springframework.util.CollectionUtils;


class HeaderValueHolder {

    private final List values = new LinkedList();

    public void setValue(Object value) {
       this.values.clear();
       this.values.add(value);
    }

    public void addValue(Object value) {
       this.values.add(value);
    }

    public void addValues(Collection values) {
       this.values.addAll(values);
    }

    public void addValueArray(Object values) {
       CollectionUtils.mergeArrayIntoCollection(values, this.values);
    }

    public List getValues() {
       return Collections.unmodifiableList(this.values);
    }

    public Object getValue() {
       return (!this.values.isEmpty() ? this.values.get(0) : null);
    }


    /**
     * Find a HeaderValueHolder by name, ignoring casing.
     * @param headers the Map of header names to HeaderValueHolders
     * @param name the name of the desired header
     * @return the corresponding HeaderValueHolder,
     * or <code>null</code> if none found
     */
     public static HeaderValueHolder getByName(Map headers, String name) {
         for (Iterator it = headers.keySet().iterator(); it.hasNext();) {
            String headerName = (String) it.next();
            if (headerName.equalsIgnoreCase(name)) {
                return (HeaderValueHolder) headers.get(headerName);
            }
         }
         return null;
     }
}
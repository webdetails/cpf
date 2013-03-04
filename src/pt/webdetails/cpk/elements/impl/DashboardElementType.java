
/* This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this file,
 * You can obtain one at http://mozilla.org/MPL/2.0/. */
package pt.webdetails.cpk.elements.impl;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import pt.webdetails.cpk.elements.AbstractElementType;
import pt.webdetails.cpk.elements.IElement;

/**
 *
 * @author Pedro Alves<pedro.alves@webdetails.pt>
 */
public class DashboardElementType extends AbstractElementType{

    protected Log logger = LogFactory.getLog(this.getClass());
    
    public DashboardElementType() {
    }

    
    @Override
    public String getType() {
        return "Dashboard";
    }

    @Override
    public void processRequest() {
        logger.warn("Dashboard process not implemented yet");
    }


    @Override
    public IElement registerElement(String elementLocation) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }


}

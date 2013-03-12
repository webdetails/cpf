
/* This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this file,
 * You can obtain one at http://mozilla.org/MPL/2.0/. */
package pt.webdetails.cpk.elements.impl;

import java.util.Map;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.pentaho.platform.api.engine.IParameterProvider;
import pt.webdetails.cpf.SimpleContentGenerator;
import pt.webdetails.cpk.elements.AbstractElementType;
import pt.webdetails.cpk.elements.ElementInfo;
import pt.webdetails.cpk.elements.IElement;

/**
 *
 * @author Pedro Alves<pedro.alves@webdetails.pt>
 */
public class KettleElementType extends AbstractElementType {

    protected Log logger = LogFactory.getLog(this.getClass());

    public KettleElementType() {
    }

    @Override
    public String getType() {
        return "Kettle";
    }


    @Override
    public void processRequest(Map<String, IParameterProvider> parameterProviders, IElement element) {
        logger.warn("Kettle's endpoint - not done yet, but it's good to be here!");
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    protected ElementInfo createElementInfo() {
        return new ElementInfo(SimpleContentGenerator.MimeType.JSON, 0);
    }
}

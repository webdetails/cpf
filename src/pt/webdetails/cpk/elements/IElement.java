/* This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this file,
 * You can obtain one at http://mozilla.org/MPL/2.0/. */
package pt.webdetails.cpk.elements;

import java.util.Map;
import org.pentaho.platform.api.engine.IParameterProvider;

/**
 *
 * @author Pedro Alves<pedro.alves@webdetails.pt>
 */
public interface IElement {
    
    public String getId();
    
    public String getName();
    
    public String getLocation();
    
    public String getElementType();
 
    public void processRequest(Map<String, IParameterProvider> parameterProviders);
    
    public boolean getAdminOnly();
    
    public ElementInfo getElementInfo();
    
    
}

/* This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this file,
 * You can obtain one at http://mozilla.org/MPL/2.0/. */
package pt.webdetails.cpk.elements;

import java.util.List;
import org.dom4j.Node;

/**
 *
 * @author Pedro Alves<pedro.alves@webdetails.pt>
 */
public interface IElementType {

    public String getType();

    public List<IElement> scanElements(Node node);
    
    public IElement registerElement(String elementLocation);
    
    public void processRequest();
}

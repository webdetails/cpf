/* This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this file,
 * You can obtain one at http://mozilla.org/MPL/2.0/. */
package pt.webdetails.cpk.plugins;

import org.dom4j.Node;
import pt.webdetails.cpf.plugins.Plugin;

/**
 *
 * @author Luis Paulo Silva<luis.silva@webdetails.pt>
 */
public class CpkPlugin extends Plugin{
    
    public CpkPlugin(String path){        
        super(path);
    }
    
    public boolean isCpkPlugin(){
        boolean is = false;
        Node documentNode = super.getXmlFileContent(super.getPath()+"plugin.xml");
        if(documentNode.valueOf("/plugin/content-generator/@class").equals("pt.webdetails.cpk.CpkContentGenerator")){
            is = true;
        }
        return is; 
    }
}

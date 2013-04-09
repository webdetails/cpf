/* This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this file,
 * You can obtain one at http://mozilla.org/MPL/2.0/. */

package pt.webdetails.cpk.sitemap;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import pt.webdetails.cpk.elements.IElement;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.codehaus.jackson.JsonNode;
import org.codehaus.jackson.map.ObjectMapper;
import pt.webdetails.cpk.security.AccessControl;

/**
 *
 * @author Luís Paulo Silva
 */
public class LinkGenerator{
    private ArrayList<Link> dashboardLinks;
    private ArrayList<Link> kettleLinks;
    private Collection<IElement> elements;
    protected Log logger = LogFactory.getLog(this.getClass());
    
    


    public LinkGenerator(Collection<IElement> e) {
        elements = e;
        generateLinks();
    }
    
    public LinkGenerator(){
        //Dummy constructor
    }
    


    private void generateLinks(){
        dashboardLinks = new ArrayList<Link>();
        kettleLinks = new ArrayList<Link>();
        AccessControl accessControl = new AccessControl();

        for(IElement e : elements){
            if(accessControl.isAllowed(e)){
                Link link = new Link(e,false, getElements());
                if(isDashboard(e)){
                    if(!dashboardLinks.isEmpty() && !linkExists(dashboardLinks, link) && link.getName() != null){
                        
                        dashboardLinks.add(link);

                    }else if(dashboardLinks.isEmpty() && link.getName() != null){
                        dashboardLinks.add(link);
                    }
                }else if(isKettle(e)){
                    kettleLinks.add(link);
                }
            }
        }

    }

    private boolean linkExists(ArrayList<Link> lnks, Link lnk){
        boolean exists = false;

        for(Link l : lnks){
            try{
                if(l.getName() == null){
                }else if(l.getName().equals(lnk.getName())){
                    exists=true;
                }
            }catch(Exception e){
                exists = true;
            }
        }

        return exists;
    }

    

    public JsonNode getLinksJson(){
        ObjectMapper mapper = new ObjectMapper();
        JsonNode jnode = null;
        ArrayList<String> json = new ArrayList<String>();

        for(Link l: dashboardLinks){
            json.add(l.getLinkJson());
        }
        try {
            jnode = mapper.readTree(json.toString());
        } catch (IOException ex) {
           logger.error(ex);
        }

        return jnode;
    }

    public boolean isDashboard(IElement e){
        boolean is=false;

        if(e.getElementType().equalsIgnoreCase("dashboard")){
            is = true;
        }

        return is;
    }

    public boolean isKettle(IElement e){
        boolean is=false;

        if(e.getElementType().equalsIgnoreCase("kettle")){
            is = true;
        }

        return is;
    }
    
    private Collection<IElement> getElements(){
        return this.elements;
    }

}
  

/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package pt.webdetails.cpf.utils;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import pt.webdetails.cpk.elements.IElement;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.codehaus.jackson.JsonNode;
import org.codehaus.jackson.annotate.JsonIgnore;
import org.codehaus.jackson.map.ObjectMapper;
import org.dom4j.Document;
import org.dom4j.DocumentException;
import org.dom4j.io.SAXReader;
import pt.webdetails.cpk.CpkEngine;

/**
 *
 * @author bandjalah
 */
public class LinkGenerator{
    private ArrayList<Link> dashboardLinks;
    private ArrayList<Link> kettleLinks;
    private Collection<IElement> elements;
    protected Log logger = LogFactory.getLog(this.getClass());
    private AccessControl accessControl = new AccessControl();
    


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

        for(IElement e : elements){
            if(accessControl.isAllowed(e.getLocation())){
                Link link = new Link(e,false, getElements());
                if(isDashboard(e)){
                    if(!dashboardLinks.isEmpty()){
                        if(!linkExists(dashboardLinks, link) && link.getName() != null){
                            dashboardLinks.add(link);

                        }else if(link.getName() == null){
                            logger.error("There was a problem with a link creation. It's name is 'null'");
                        }

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
  

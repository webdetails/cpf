/* This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this file,
 * You can obtain one at http://mozilla.org/MPL/2.0/. */

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
public class Link{
    
    private String name, id, link;
    private IElement element;
    private boolean isSublink;
    private List<Link> subLinks;
    private Collection<IElement> elements;
    
    protected Log logger = LogFactory.getLog(this.getClass());

    Link(IElement e, boolean sublnk, Collection<IElement> elements){
        
        if(this.elements == null){
            this.elements = elements;
        }
        init(e,sublnk);
    }

    private void init(IElement e, boolean sublnk){
        this.element = e;
        this.isSublink = sublnk;
        subLinks = new ArrayList<Link>();
        buildLink();

    }

    public void buildLink(){
        if(isDashboard(element) && !isSublink){
            if(hasSublink()){
                createSublinks();
            }else{
                this.name = getTextFromWcdf(element.getLocation(),"description");
                this.link = "/pentaho/content/"+PluginUtils.getInstance().getPluginName()+"/"+element.getId();
                this.id = element.getLocation().split("/")[element.getLocation().split("/").length-1];
                //this.sublink = null;
            }

        }else if(isKettle(element) && !isSublink){
            if(hasSublink()){
                createSublinks();
            }else{
                this.name = element.getName();
                this.id = element.getLocation().split("/")[element.getLocation().split("/").length-1];
                //this.sublink = null;
                this.link = "/pentaho/content/"+PluginUtils.getInstance().getPluginName()+"/"+element.getId();
            }


        }else if(isSublink){
            if(isDashboard(element)){
                this.name = getTextFromWcdf(element.getLocation(),"description");
                this.link = "/pentaho/content/"+PluginUtils.getInstance().getPluginName()+"/"+element.getId();
                this.id = element.getLocation().split("/")[element.getLocation().split("/").length-1];
                //this.sublink = null;
            }

        }
    }

    public String getName(){
        String n;
        if(this.name==null){n="null";}
        else {n = this.name;}
        return n;
    }

    public String getId(){
        String i;
        if(this.id==null){i="null";}
        else {i = this.id;}
        return i;
    }

    public List<Link> getSublinks() {
        return subLinks;
    }

    public String getLink(){
        String l;
        if(this.link==null){l="null";}
        else {l = this.link;}
        return l;
    }

    private String getTextFromWcdf(String path,String text){
        File xml = new File(path);
        SAXReader reader = new SAXReader();
        Document doc = null;
        try {
            doc = reader.read(xml);
        } catch (DocumentException documentException) {
        }

        org.dom4j.Element root = doc.getRootElement();

        return root.elementText(text);
    }

    private boolean hasSublink(){
        boolean has = false;
        String primaryDir = null;
        String [] dirs = element.getLocation().split("/");
        int nrDirs = dirs.length;

        if(isDashboard(element)){
            primaryDir = "dashboards";
            if(!dirs[nrDirs-2].equals(primaryDir)){
                has = true;
            }

        }else if(isKettle(element)){
            primaryDir = "kettle";
            if(!dirs[nrDirs-2].equals(primaryDir)){
                has = true;
            }

        }
        return has;
    }

    private void createSublinks(){
        String [] dirs = element.getLocation().split("/");
        int nrDirs = dirs.length;


        this.name = dirs[nrDirs-2];
        this.link = "";
        ArrayList<IElement> elementsList = getAllElementsOnFolder(name);

        for(IElement e: elementsList){
            Link l = new Link(e, true, getElements());
            subLinks.add(l);
        }

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
    
    private ArrayList<IElement> getAllElementsOnFolder(String folderName){

        ArrayList<IElement> sublinkElements = new ArrayList<IElement>();
        boolean has = false;
        String primaryFolder = folderName;
        String [] dirs;
        int nrDirs;
        

        for(IElement e: getElements()){
            dirs = e.getLocation().split("/");
            nrDirs = dirs.length;

            if(dirs[nrDirs-2].equals(primaryFolder)){
                sublinkElements.add(e);
            }
        }

        return sublinkElements;
    }

    @JsonIgnore
    public String getLinkJson(){
        ObjectMapper mapper = new ObjectMapper();
        try {
            logger.info(mapper.writeValueAsString(this));
            return mapper.writeValueAsString(this);
        } catch (IOException ex) {
            Logger.getLogger(CpkEngine.class.getName()).log(Level.SEVERE, null, ex);
        }
        return null;
    }
    
    private Collection<IElement> getElements(){
        return this.elements;
    }


}

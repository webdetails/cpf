/* This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this file,
 * You can obtain one at http://mozilla.org/MPL/2.0/. */
package pt.webdetails.cpf.plugins;

import java.util.List;
import java.util.concurrent.ConcurrentMap;

/**
 *
 * @author Luis Paulo Silva
 */
public class Plugin {
    private String id;
    private String name;
    private String description;
    private String company;
    private String companyUrl;
    private ConcurrentMap<Enum,List<Entity>> elementTypePath;
    
    public Plugin(String id, String name, String description, String company,  String companyUrl, ConcurrentMap map) {
        setId(id);
        setName(name);
        setDescription(description);
        setCompany(company);
        setCompanyUrl(companyUrl);
        setElementTypePath(map);
    }

    public ConcurrentMap<Enum, List<Entity>> getElementTypePath() {
        return elementTypePath;
    }

    private void setElementTypePath(ConcurrentMap<Enum, List<Entity>> elementTypePath) {
        this.elementTypePath = elementTypePath;
    }


    public String getCompany() {
        return company;
    }

    private void setCompany(String company) {
        this.company = company;
    }

    public String getCompanyUrl() {
        return companyUrl;
    }

    private void setCompanyUrl(String companyUrl) {
        this.companyUrl = companyUrl;
    }

    public String getDescription() {
        return description;
    }

    private void setDescription(String description) {
        this.description = description;
    }

    public String getId() {
        return id;
    }

    private void setId(String id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    private void setName(String name) {
        this.name = name;
    }
    
    public List<Entity> getEntityByType(EntityTypeEnum type){
        return null;
    }
}

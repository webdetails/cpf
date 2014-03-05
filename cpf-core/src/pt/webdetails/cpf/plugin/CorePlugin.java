/*!
* Copyright 2002 - 2013 Webdetails, a Pentaho company.  All rights reserved.
* 
* This software was developed by Webdetails and is provided under the terms
* of the Mozilla Public License, Version 2.0, or any later version. You may not use
* this file except in compliance with the license. If you need a copy of the license,
* please go to  http://mozilla.org/MPL/2.0/. The Initial Developer is Webdetails.
*
* Software distributed under the Mozilla Public License is distributed on an "AS IS"
* basis, WITHOUT WARRANTY OF ANY KIND, either express or  implied. Please refer to
* the license for the specific language governing your rights and limitations.
*/

package pt.webdetails.cpf.plugin;

import org.codehaus.jackson.annotate.JsonIgnore;
import org.codehaus.jackson.annotate.JsonProperty;
import pt.webdetails.cpf.repository.IRepositoryFile;
import pt.webdetails.cpf.repository.IRepositoryFileFilter;

public class CorePlugin {

    public final static CorePlugin CDA = new CorePlugin("cda", new String[]{"cda"});
    public final static CorePlugin CDB = new CorePlugin("cdb", new String[]{"cdb"});
    public final static CorePlugin CDC = new CorePlugin("cdc");
    public final static CorePlugin CDE = new CorePlugin("pentaho-cdf-dd", new String[]{"cdfde"});
    public final static CorePlugin CDF = new CorePlugin("pentaho-cdf", new String[]{"wcdf"});
    public final static CorePlugin CDV = new CorePlugin("cdv");
    protected String name;
    protected String id;//title
    private String[] fileExtensions;

    @JsonProperty("name")
    public String getName() {
        return name;
    }

    @JsonIgnore
    public String getTitle() {
        return getId();
    }

    public CorePlugin(String id, String[] fileExtensions) {
        this(id);
        this.fileExtensions = fileExtensions;
    }

    public CorePlugin(String name, String title) {
        this.name = name;
        this.id = title;
    }

    public CorePlugin(String id) {
        this.name = id;
        this.id = id;
    }

    public CorePlugin() {
        this.name = "defaultName";
        this.id = "defaultId";
    }

    @JsonIgnore
    public IRepositoryFileFilter getPluginFileFilter() {
        return new IRepositoryFileFilter() {
            @Override
            public boolean accept(IRepositoryFile irf) {
                if (getFileExtensions() != null) {
                    for (String extension : getFileExtensions()) {
                        if (irf.getExtension().contains(extension)) {
                            return true;
                        }
                    }
                }
                return false;
            }
        };
    }

    /**
     * @param name the name to set
     */
    public void setName(String name) {
        this.name = name;
    }

    /**
     * @return the id
     */
    @JsonProperty("id")
    public String getId() {
        return id;
    }

    /**
     * @param id the id to set
     */
    public void setId(String id) {
        this.id = id;
    }

    /**
     * @return the fileExtensions
     */
    @JsonIgnore
    public String[] getFileExtensions() {
        return fileExtensions;
    }

    /**
     * @param fileExtensions the fileExtensions to set
     */
    public void setFileExtensions(String[] fileExtensions) {
        this.fileExtensions = fileExtensions;
    }
}

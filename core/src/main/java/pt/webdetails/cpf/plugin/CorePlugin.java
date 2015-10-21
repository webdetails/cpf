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

import org.codehaus.jackson.annotate.JsonProperty;

//TODO: best just use id, tbd
public class CorePlugin {

    public final static CorePlugin CDA = new CorePlugin("cda");
    public final static CorePlugin CDB = new CorePlugin("cdb");
    public final static CorePlugin CDC = new CorePlugin("cdc");
    public final static CorePlugin CDE = new CorePlugin("pentaho-cdf-dd");
    public final static CorePlugin CDF = new CorePlugin("pentaho-cdf");
    public final static CorePlugin CDV = new CorePlugin("cdv");
    protected String name;
    protected String id;//title

    @JsonProperty("name")
    public String getName() {
        return name;
    }

    public CorePlugin(String id) {
        this.name = id;
        this.id = id;
    }

    public void setName(String name) {
        this.name = name;
    }

    @JsonProperty("id")
    public String getId() {
        return id;
    }

}

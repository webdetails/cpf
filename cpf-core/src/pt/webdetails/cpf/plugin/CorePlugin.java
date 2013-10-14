/* This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this file,
 * You can obtain one at http://mozilla.org/MPL/2.0/. */
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
/* This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this file,
 * You can obtain one at http://mozilla.org/MPL/2.0/. */
package pt.webdetails.cpf.plugin;

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

    public String getName() {
        return name;
    }

    public String getTitle() {
        return id;
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

    public IRepositoryFileFilter getPluginFileFilter() {
        return new IRepositoryFileFilter() {
            @Override
            public boolean accept(IRepositoryFile irf) {
                if (fileExtensions != null) {
                    for (String extension : fileExtensions) {
                        if (irf.getExtension().contains(extension)) {
                            return true;
                        }
                    }
                }
                return false;
            }
        };
    }
}
/* This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this file,
 * You can obtain one at http://mozilla.org/MPL/2.0/. */
package pt.webdetails.cpf.plugin;

import pt.webdetails.cpf.repository.IRepositoryFile;
import pt.webdetails.cpf.repository.IRepositoryFileFilter;

public class Plugin {

  public final static Plugin CDA = new Plugin("cda", new String[]{"cda"});
  public final static Plugin CDB = new Plugin("cdb", new String[]{"cdb"});
  public final static Plugin CDC = new Plugin("cdc");
  public final static Plugin CDE = new Plugin("pentaho-cdf-dd", new String[]{"cdfde"});
  public final static Plugin CDF = new Plugin("pentaho-cdf", new String[]{"wcdf"});
  public final static Plugin CDV = new Plugin("cdv");
  private String name;
  private String title;
  private String[] fileExtensions;

  public String getName() {
    return name;
  }

  public String getTitle() {
    return title;
  }

  public Plugin(String id, String[] fileExtensions) {
    this(id);
    this.fileExtensions = fileExtensions;
  }

  public Plugin(String name, String title) {
    this.name = name;
    this.title = title;
  }

  public Plugin(String id) {
    this.name = id;
    this.title = id;
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



/*
 
package pt.webdetails.cpf.plugin;

import pt.webdetails.cpf.repository.IRepositoryFile;
import pt.webdetails.cpf.repository.IRepositoryFileFilter;

public class Plugin {

  public final static Plugin CDA = new Plugin("cda", new String[]{"cda"});
  public final static Plugin CDB = new Plugin("cdb", new String[]{"cdb"});
  public final static Plugin CDC = new Plugin("cdc");
  public final static Plugin CDE = new Plugin("pentaho-cdf-dd", new String[]{"cdfde"});
  public final static Plugin CDF = new Plugin("pentaho-cdf", new String[]{"wcdf"});
  public final static Plugin CDV = new Plugin("cdv");
  private String name;
  private String title;
  private String[] fileExtensions;

  public String getName() {
    return name;
  }

  public String getTitle() {
    return title;
  }

  public Plugin(String id, String[] fileExtensions) {
    this(id);
    this.fileExtensions = fileExtensions;
  }

  public Plugin(String name, String title) {
    this.name = name;
    this.title = title;
  }

  public Plugin(String id) {
    this.name = id;
    this.title = id;
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
 
 
 
 
 */
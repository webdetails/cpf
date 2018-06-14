/*!
* Copyright 2002 - 2018 Webdetails, a Hitachi Vantara company.  All rights reserved.
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

import com.fasterxml.jackson.annotation.JsonProperty;

//TODO: best just use id, tbd
public class CorePlugin {

  public static final CorePlugin CDA = new CorePlugin( "cda" );
  public static final CorePlugin CDB = new CorePlugin( "cdb" );
  public static final CorePlugin CDC = new CorePlugin( "cdc" );
  public static final CorePlugin CDE = new CorePlugin( "pentaho-cdf-dd" );
  public static final CorePlugin CDF = new CorePlugin( "pentaho-cdf" );
  public static final CorePlugin CDV = new CorePlugin( "cdv" );
  protected String name;
  protected String id; //title

  @JsonProperty( "name" )
  public String getName() {
    return name;
  }

  public CorePlugin( String id ) {
    this.name = id;
    this.id = id;
  }

  public void setName( String name ) {
    this.name = name;
  }

  @JsonProperty( "id" )
  public String getId() {
    return id;
  }

}

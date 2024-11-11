/*! ******************************************************************************
 *
 * Pentaho
 *
 * Copyright (C) 2024 by Hitachi Vantara, LLC : http://www.pentaho.com
 *
 * Use of this software is governed by the Business Source License included
 * in the LICENSE.TXT file.
 *
 * Change Date: 2029-07-20
 ******************************************************************************/


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

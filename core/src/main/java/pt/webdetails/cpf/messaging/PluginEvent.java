/*! ******************************************************************************
 *
 * Pentaho
 *
 * Copyright (C) 2024 by Hitachi Vantara, LLC : http://www.pentaho.com
 *
 * Use of this software is governed by the Business Source License included
 * in the LICENSE.TXT file.
 *
 * Change Date: 2028-08-13
 ******************************************************************************/

package pt.webdetails.cpf.messaging;

import org.json.JSONException;
import org.json.JSONObject;
import pt.webdetails.cpf.persistence.Persistable;

public class PluginEvent implements Persistable {

  public static class Fields {
    public static final String TIMESTAMP = "timestamp";
    public static final String EVENT_TYPE = "eventType";
    public static final String PLUGIN = "plugin";
    public static final String NAME = "name";
    public static final String KEY = "key";
  }

  private long timeStamp;
  private String eventType;
  private String plugin;
  private String name;

  private String key;

  @Override
  public void setKey( String key ) {
    this.key = key;
  }

  public PluginEvent( JSONObject json ) throws JSONException {
    this.fromJSON( json );
  }

  public PluginEvent( String plugin, String eventType, String name ) throws JSONException {
    this.timeStamp = System.currentTimeMillis(); //TODO: global getTime!
    this.plugin = plugin;
    this.eventType = eventType;
    this.name = name;
  }

  public long getTimeStamp() {
    return timeStamp;
  }

  public void setTimeStamp( long timeStamp ) {
    this.timeStamp = timeStamp;
  }

  public String getEventType() {
    return eventType;
  }

  public void setEventType( String eventType ) {
    this.eventType = eventType;
  }

  public String getPlugin() {
    return plugin;
  }

  public void setPlugin( String plugin ) {
    this.plugin = plugin;
  }

  public String getName() {
    return name;
  }

  public void setName( String name ) {
    this.name = name;
  }

  @Override
  public JSONObject toJSON() throws JSONException {
    JSONObject obj = new JSONObject();
    obj.put( Fields.EVENT_TYPE, getEventType() );
    obj.put( Fields.PLUGIN, getPlugin() );
    obj.put( Fields.NAME, getName() );
    obj.put( Fields.TIMESTAMP, getTimeStamp() );
    return obj;
  }

  @Override
  public String toString() {
    try {
      return toJSON().toString( 2 );
    } catch ( JSONException e ) {
      return "bad json: " + e.getMessage();
    }
  }

  @Override
  public String getKey() {
    return key;
  }

  @Override
  public void fromJSON( JSONObject json ) throws JSONException {
    setTimeStamp( json.getLong( Fields.TIMESTAMP ) );
    setPlugin( json.getString( Fields.PLUGIN ) );
    setName( json.getString( Fields.NAME ) );
    setEventType( json.getString( Fields.EVENT_TYPE ) );
    try {
      setKey( json.getString( Fields.KEY ) );
    } catch ( JSONException jse ) {
      setKey( null );
    }
  }
}

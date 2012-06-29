package pt.webdetails.cpf.messaging;

import org.json.JSONException;
import org.json.JSONObject;

import pt.webdetails.cpf.persistence.Persistable;

public class PluginEvent implements Persistable {
  
  public static class Fields {
    public static final String TIMESTAMP = "timestamp";
    public static final String EVENT_TYPE = "eventType";
    public static final String PLUGIN = "plugin";
    public static final String KEY = "key";
//    public static final String EVENT = "event";
  }

  private long timeStamp;
  private String eventType;
  private String plugin;
//  private JSONObject event;
  private JSONObject key;

  protected void setKey(JSONObject key) {
    this.key = key;
  }

  public PluginEvent(JSONObject json) throws JSONException {
    setTimeStamp(json.getLong(Fields.TIMESTAMP));
    setPlugin(json.getString(Fields.PLUGIN));
    setEventType(json.getString(Fields.EVENT_TYPE));
    setKey(json.getJSONObject(Fields.KEY));
//    try{
//      setEvent(json.getJSONObject(Fields.EVENT));
//    }
//    catch(JSONException e){
//      setEvent(new JSONObject(json.getString(Fields.EVENT)));
//    }
  }
  
  public PluginEvent(String plugin, String eventType) throws JSONException{
    this.timeStamp = System.currentTimeMillis();//TODO: global getTime!
    this.plugin = plugin;
    this.eventType = eventType;
//    this.event = event == null ? null : event.toJSON();
  }
  
  public long getTimeStamp(){
    return timeStamp;
  }
  
  public void setTimeStamp(long timeStamp) {
    this.timeStamp = timeStamp;
  }

  public String getEventType() {
    return eventType;
  }

  public void setEventType(String eventType) {
    this.eventType = eventType;
  }

  public String getPlugin() {
    return plugin;
  }

  public void setPlugin(String plugin) {
    this.plugin = plugin;
  }
  
//  protected void setEvent(JSONObject eventInfo){
//    event = eventInfo;
//  }
//  
//  public JSONObject getEvent(){
//    return event;
//  }
  
  @Override
  public JSONObject toJSON() throws JSONException {
    JSONObject obj = new JSONObject(); 
//    obj.put(Fields.EVENT, getEvent());
    obj.put(Fields.EVENT_TYPE, getEventType());
    obj.put(Fields.PLUGIN, getPlugin());
    obj.put(Fields.TIMESTAMP, getTimeStamp());
    obj.put(Fields.KEY, getKey());
    return obj;
  }
  
  @Override 
  public String toString(){
    try {
      return toJSON().toString(2);
    } catch (JSONException e) {
      return "bad json: " + e.getMessage();
    }
  }

  @Override
  public JSONObject getKey() {
    return key;
  }

  @Override
  public String getPersistenceClass() {
    return plugin + "_" +  eventType;
  }

}

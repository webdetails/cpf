package pt.webdetails.cpf.messaging;

import org.json.JSONException;
import org.json.JSONObject;

import pt.webdetails.cpf.JsonSerializable;

public class PluginEvent implements JsonSerializable {
  
  public static class Fields {
    public static final String TIMESTAMP = "timestamp";
    public static final String EVENT_TYPE = "eventType";
    public static final String PLUGIN = "plugin";
    public static final String EVENT = "event";
  }

  private long timeStamp;
  private String eventType;
  private String plugin;
  private JSONObject event;

  public PluginEvent(JSONObject json) throws JSONException {
    setTimeStamp(json.getLong(Fields.TIMESTAMP));
    setPlugin(json.getString(Fields.PLUGIN));
    setEventType(json.getString(Fields.EVENT_TYPE));
    setEvent(json.getJSONObject(Fields.EVENT));
  }
  
  public PluginEvent(String plugin, String eventType, JsonSerializable event) throws JSONException{
    this.timeStamp = System.currentTimeMillis();//TODO: global getTime!
    this.plugin = plugin;
    this.eventType = eventType;
    this.event = event == null ? null : event.toJSON();
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
  
  protected void setEvent(JSONObject eventInfo){
    event = eventInfo;
  }
  
  public JSONObject getEvent(){
    return event;
  }
  
  @Override
  public JSONObject toJSON() throws JSONException {
    JSONObject obj = new JSONObject(); 
    obj.put(Fields.EVENT, getEvent());
    obj.put(Fields.EVENT_TYPE, getEventType());
    obj.put(Fields.PLUGIN, getPlugin());
    obj.put(Fields.TIMESTAMP, getTimeStamp());
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

}

package pt.webdetails.cpk.datasources;

import com.fasterxml.jackson.annotation.JsonProperty;

public class CpkDataSourceMetadata extends DataSourceMetadata {

  @JsonProperty("pluginId")
  private String pluginId;

  //  @JsonProperty("endpointname")
  //  private String endpointName;

  /**
   * @param pluginId the pluginId to set
   */
  protected void setPluginId(String pluginId) {
    this.pluginId = pluginId;
  }

  /**
   * @return the pluginId
   */
  public String getPluginId() {
    return pluginId;
  }

  public CpkDataSourceMetadata(String pluginId, String name) {
    setName(name);
    setPluginId(pluginId);
    /*
     *  which data type should be declared?
     *  is it plugin implementation dependable?
     */
    setDataType("cpk");
    setGroup("CPK");
    setGroupDescription("CPK Endpoints");
  }

}

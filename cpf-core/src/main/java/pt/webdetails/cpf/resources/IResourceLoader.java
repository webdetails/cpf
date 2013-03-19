package pt.webdetails.cpf.resources;

/**
 *
 * @author dfscm
 */
public interface IResourceLoader {

    public String getResourceAsString(Class<? extends Object> type, String string);
    
    public String getPluginSetting(Class<?> type, String string);
}

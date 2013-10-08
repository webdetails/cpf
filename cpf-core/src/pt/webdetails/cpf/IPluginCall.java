package pt.webdetails.cpf;

import java.util.Map;

import pt.webdetails.cpf.plugin.CorePlugin;

/**
 * @deprecated gonna break this one too
 */
public interface IPluginCall {

	public final static String DEFAULT_ENCODING = "UTF-8";
	  
	public void init(CorePlugin plugin, String method, Map<String, Object> params);

	public String getMethod();

	public void setMethod(String method);

	public String call();

}
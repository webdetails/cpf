package pt.webdetails.cpf;

import java.util.Map;

import pt.webdetails.cpf.plugin.Plugin;

public interface IPluginCall {

	public final static String DEFAULT_ENCODING = "UTF-8";
	  
	public void init(Plugin plugin, String method, Map<String, Object> params);

	public String getMethod();

	public void setMethod(String method);

	public String call();

}
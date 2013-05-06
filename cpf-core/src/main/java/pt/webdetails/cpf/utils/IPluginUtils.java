/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package pt.webdetails.cpf.utils;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.OutputStream;
import java.util.Collection;
import java.util.Map;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.dom4j.DocumentException;
import pt.webdetails.cpf.http.ICommonParameterProvider;

/**
 *
 * @author joao
 */
public interface IPluginUtils {
      
    public File getPluginDirectory();

    public void setPluginDirectory(File pluginDirectory);

    public String getPluginName();

    public void setPluginName(String pluginName);
    
    public void initialize() throws IOException, DocumentException;

    public Collection<File> getPluginResources(String elementPath, Boolean recursive, String pattern);
      
    public String getPluginRelativeDirectory(String fullPath, boolean includePluginDir) throws FileNotFoundException;
    
    public Collection<File> getPluginResources(String elementPath, Boolean recursive);

    public Collection<File> getPluginResources(String elementPath, String pattern);

    public void setResponseHeaders(Map<String, ICommonParameterProvider> parameterProviders, final String mimeType);
    
    public void setResponseHeaders(Map<String, ICommonParameterProvider> parameterProviders, final String mimeType, final String attachmentName);
    
    public void setResponseHeaders(Map<String, ICommonParameterProvider> parameterProviders, final String mimeType, final String attachmentName, long attachmentSize);

    public void setResponseHeaders(Map<String, ICommonParameterProvider> parameterProviders, final String mimeType, final int cacheDuration, final String attachmentName, long attachmentSize);

    public void copyParametersFromProvider(Map<String, Object> params, ICommonParameterProvider provider);
    
    public void redirect(Map<String, ICommonParameterProvider> parameterProviders, String url);
    
    public HttpServletRequest getRequest(Map<String, ICommonParameterProvider> parameterProviders);

    public HttpServletResponse getResponse(Map<String, ICommonParameterProvider> parameterProviders);

    public ICommonParameterProvider getRequestParameters(Map<String, ICommonParameterProvider> parameterProviders);
    
    public ICommonParameterProvider getPathParameters(Map<String, ICommonParameterProvider> parameterProviders);

    public OutputStream getResponseOutputStream(Map<String, ICommonParameterProvider> parameterProviders) throws IOException ;
    
    public OutputStream getOutputStream(Map<String, ICommonParameterProvider> parameterProviders) throws IOException ;
    
}

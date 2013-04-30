package pt.webdetails.cpf;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Map.Entry;
import javax.servlet.http.HttpServletRequest;

import org.pentaho.platform.api.engine.IParameterProvider;
import pt.webdetails.cpf.RestRequestHandler;
import pt.webdetails.cpf.RestRequestHandler.HttpMethod;
import pt.webdetails.cpf.http.CommonParameterProvider;
import pt.webdetails.cpf.http.ICommonParameterProvider;
import pt.webdetails.cpf.utils.IPluginUtils;

public abstract class RestContentGenerator extends SimpleContentGenerator {

    private static final long serialVersionUID = 1L;
    private ICommonParameterProvider commonParameterProviders=new CommonParameterProvider();
    private ICommonParameterProvider commonParameterProvider;
    private Map<String, ICommonParameterProvider> map;
    private IPluginUtils pluginUtils;
    public void initParams(){//XXX review
        
        Iterator it =  parameterProviders.entrySet().iterator();
        map = new HashMap<String, ICommonParameterProvider>();
        while(it.hasNext()){
            Entry<String,IParameterProvider> e = (Entry<String,IParameterProvider>) it.next();
            commonParameterProvider=new CommonParameterProvider();
           commonParameterProvider.put(e.getKey(), e.getValue());
           map.put(e.getKey(), commonParameterProvider);
        }
        
    }

    
    
    public abstract RestRequestHandler getRequestHandler();
    
  
    //XXX removed override
    public void createContent(Map<String,ICommonParameterProvider> parameterProviders) throws Exception {

        RestRequestHandler router = getRequestHandler();
        

        String path = pluginUtils.getPathParameters(parameterProviders).getStringParameter("path", null);
        if (router.canHandle(getHttpMethod(), path)) {
            router.route(getHttpMethod(), path, getResponseOutputStream(router.getResponseMimeType()),
                    pluginUtils.getPathParameters(parameterProviders),
                    pluginUtils.getRequestParameters(parameterProviders));
        } else {
            super.createContent();
        }
    }

    public HttpMethod getHttpMethod() {
        HttpServletRequest request = pluginUtils.getRequest(map);
        String method = (request == null) ? null : request.getMethod();
        return (method != null) ? HttpMethod.valueOf(method) : HttpMethod.GET;
    }
}
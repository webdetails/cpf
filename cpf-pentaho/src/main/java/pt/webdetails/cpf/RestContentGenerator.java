package pt.webdetails.cpf;

import javax.servlet.http.HttpServletRequest;

import pt.webdetails.cpf.RestRequestHandler;
import pt.webdetails.cpf.RestRequestHandler.HttpMethod;
import pt.webdetails.cpf.utils.PluginUtils;

public abstract class RestContentGenerator extends SimpleContentGenerator {//XXX to pentaho prob?

    private static final long serialVersionUID = 1L;

    
    
    public abstract RestRequestHandler getRequestHandler();
    
  
    @Override
    public void createContent(Map<String,ICommonParameterProvider> parameterProviders) throws Exception {

        RestRequestHandler router = getRequestHandler();
        PluginUtils pluginUtils = PluginUtils.getInstance();

        String path = pluginUtils.getPathParameters(parameterProviders).getStringParameter("path", null);
        if (router.canHandle(getHttpMethod(), path)) {
            router.route(getHttpMethod(), path, getResponseOutputStream(router.getResponseMimeType()),//XXX how to solve this one
                    pluginUtils.getPathParameters(parameterProviders),
                    pluginUtils.getRequestParameters(parameterProviders));
        } else {
            super.createContent();
        }
    }

    public HttpMethod getHttpMethod() {
        HttpServletRequest request = PluginUtils.getInstance().getRequest(parameterProviders);
        String method = (request == null) ? null : request.getMethod();
        return (method != null) ? HttpMethod.valueOf(method) : HttpMethod.GET;
    }
}
package pt.webdetails.cpf;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Map.Entry;
import javax.servlet.http.HttpServletRequest;


import org.pentaho.platform.api.engine.IParameterProvider;
import pt.webdetails.cpf.RestRequestHandler.HttpMethod;
import pt.webdetails.cpf.http.CommonParameterProvider;
import pt.webdetails.cpf.http.ICommonParameterProvider;
import pt.webdetails.cpf.utils.PluginUtils;

public abstract class RestContentGenerator extends SimpleContentGenerator {

    private static final long serialVersionUID = 1L;
    protected Map<String, ICommonParameterProvider> map;
    protected PluginUtils pluginUtils;

    public void initParams() {
        pluginUtils = new PluginUtils();
        if (parameterProviders != null) {
            Iterator it = parameterProviders.entrySet().iterator();
            map = new HashMap<String, ICommonParameterProvider>();
            while (it.hasNext()) {
                Entry<String, IParameterProvider> e = (Entry<String, IParameterProvider>) it.next();
                map.put(e.getKey(), WrapperUtils.wrapParamProvider(e.getValue()));
            }
        }

    }

    public abstract RestRequestHandler getRequestHandler();


    @Override
    public void createContent() throws Exception {

        RestRequestHandler router = getRequestHandler();


        String path = pluginUtils.getPathParameters(map).getStringParameter("path", null);
        if (router.canHandle(getHttpMethod(), path)) {
            router.route(getHttpMethod(), path, getResponseOutputStream(router.getResponseMimeType()),
                    pluginUtils.getPathParameters(map),
                    pluginUtils.getRequestParameters(map));
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
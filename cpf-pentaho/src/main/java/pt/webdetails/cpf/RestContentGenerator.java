package pt.webdetails.cpf;

import java.util.Map;
import javax.servlet.http.HttpServletRequest;
import pt.webdetails.cpf.RestRequestHandler.HttpMethod;
import pt.webdetails.cpf.http.ICommonParameterProvider;
import pt.webdetails.cpf.utils.IPluginUtils;
import pt.webdetails.cpf.utils.PluginUtils;

public abstract class RestContentGenerator extends SimpleContentGenerator {

    private static final long serialVersionUID = 1L;
    protected Map<String, ICommonParameterProvider> map;
    protected IPluginUtils pluginUtils;


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
        HttpServletRequest request = ((PluginUtils) pluginUtils).getRequest(map);
        String method = (request == null) ? null : request.getMethod();
        return (method != null) ? HttpMethod.valueOf(method) : HttpMethod.GET;
    }
}
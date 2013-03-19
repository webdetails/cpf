package pt.webdetails.cpf;

import java.util.Iterator;

import org.pentaho.platform.api.engine.IParameterProvider;

import pt.webdetails.cpf.http.CommonParameterProvider;
import pt.webdetails.cpf.http.ICommonParameterProvider;

public class WrapperUtils {

    @SuppressWarnings("unchecked")
    public static ICommonParameterProvider wrapParamProvider(
            IParameterProvider requestParams) {

        //dont hardcode the implementation
        ICommonParameterProvider result = new CommonParameterProvider();

        Iterator<String> names = requestParams.getParameterNames();

        while (names.hasNext()) {
            String name = names.next();
            Object value = requestParams.getParameter(name);
            result.put(name, value);
        }

        return result;
    }
}

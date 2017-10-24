package nl.programon.ias.microservice.client;

import okhttp3.Interceptor;
import okhttp3.Request;
import okhttp3.Response;

import java.io.IOException;

public abstract class CorrelationValidator implements Interceptor{

    protected abstract void validateResponse(Response response);

    @Override
    public Response intercept(Chain chain) throws IOException {
        Request request = chain.request();

        Response response = chain.proceed(request);
        validateResponse(response);

        return response;
    }
}

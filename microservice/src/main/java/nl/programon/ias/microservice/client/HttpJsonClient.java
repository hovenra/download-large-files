package nl.programon.ias.microservice.client;

import com.fasterxml.jackson.databind.ObjectMapper;
import io.reactivex.*;
import okhttp3.*;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import javax.ws.rs.WebApplicationException;
import java.io.*;


public abstract class HttpJsonClient<REQUEST_TYPE, RESPONSE_TYPE> {
    private static final Logger LOG = LoggerFactory.getLogger(HttpJsonClient.class);



    protected abstract boolean responseValidator(RESPONSE_TYPE response) ;

    public Flowable<RESPONSE_TYPE> process(String url, String method, REQUEST_TYPE requestPayload, Class<?> responseType) {
        return Flowable.create(emitter -> {

            final OkHttpClient httpclient = new OkHttpClient().newBuilder()
                    .addInterceptor(new LoggingInterceptor())
                    .build();

                Request.Builder request = new Request.Builder();
                request.url(url);



                if (method.equals("GET")){
                    request.get()
                            .build();
                }

            InputStreamReader reader = null;
            BufferedReader br = null;


            try (Response response = httpclient.newCall(request.build()).execute()) {
                RESPONSE_TYPE returnValue = (RESPONSE_TYPE)new ObjectMapper().readValue(response.body().byteStream(), responseType);
                if (!responseValidator(returnValue)) {
                    emitter.onNext(returnValue);
                }
            } catch (IOException | WebApplicationException e) {
                emitter.onError(e);
                LOG.info(e.getMessage());
            } finally {
                br.close();
                reader.close();
            }
            emitter.onComplete();

        }, BackpressureStrategy.BUFFER);


    }

}

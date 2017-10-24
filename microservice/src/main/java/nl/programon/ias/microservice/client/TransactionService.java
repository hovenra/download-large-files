package nl.programon.ias.microservice.client;

import io.reactivex.BackpressureStrategy;
import io.reactivex.Flowable;
import nl.programon.ias.microservice.domain.Transaction;
import okhttp3.ConnectionPool;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Service;

import javax.ws.rs.WebApplicationException;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URI;
import java.net.URISyntaxException;
import java.time.LocalDateTime;
import java.util.concurrent.TimeUnit;

@Service
@ConfigurationProperties
public class TransactionService {

    private static final Logger LOG = LoggerFactory.getLogger(TransactionService.class);

    private static final ConnectionPool connectionPool = new ConnectionPool(2, 10, TimeUnit.SECONDS);
    private static final OkHttpClient client = new OkHttpClient().newBuilder()
            .addInterceptor(new LoggingInterceptor())
            .connectionPool(connectionPool)
            .build();


    private URI uri;

    public TransactionService() {
        try {
            uri = new URI("http://localhost:9000/file/csv");
        } catch (URISyntaxException e) {
            LOG.error(e.getMessage());
        }
    }

    public Flowable<String> getTransactions() {
        return Flowable.create(emitter -> {

            Request request = new Request.Builder()
                    .url(uri.toURL())
                    .build();

            try (final Response response = client.newCall(request).execute();
                 InputStreamReader reader = new InputStreamReader(response.body().byteStream());
                 BufferedReader bReader = new BufferedReader(reader);
            ) {

                String s;
                while ((s = bReader.readLine()) != null) {
                    emitter.onNext(s);
                }
            } catch (IOException | WebApplicationException e) {
                emitter.onError(e);
                LOG.info(e.getMessage());
            }
            emitter.onComplete();

        }, BackpressureStrategy.BUFFER);

    }

    //Not used for now, but may come in handy
    private static Transaction csvToTransaction(String csvLine) {
        final String[] p = csvLine.split(",");
        return new Transaction(p[0], p[1], p[2], p[3], LocalDateTime.now(), p[4], p[5], p[6], p[7], p[8], p[9], p[10], p[11], p[12], p[13]);
    }

}
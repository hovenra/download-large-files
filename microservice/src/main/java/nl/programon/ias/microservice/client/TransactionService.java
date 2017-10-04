package nl.programon.ias.microservice.client;

import io.reactivex.BackpressureStrategy;
import io.reactivex.Flowable;
import nl.programon.ias.microservice.domain.Transaction;
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

@Service
@ConfigurationProperties
public class TransactionService {

    private static final Logger LOG = LoggerFactory.getLogger(TransactionService.class);

    private final OkHttpClient client = new OkHttpClient();


    private URI uri;

    public TransactionService() {
        try {
            uri = new URI("http://localhost:9000/file/csv");
        } catch (URISyntaxException e) {
            LOG.error(e.getMessage());
        }
    }

    public Flowable<String> getTransactions2() {
        return Flowable.create(emitter -> {

            Request request = new Request.Builder()
                    .url(uri.toURL())
                    .build();

            InputStreamReader reader = null;
            BufferedReader br = null;

            try (final Response response = client.newCall(request).execute()) {
                reader = new InputStreamReader(response.body().byteStream());

                br = new BufferedReader(reader);
                while ((br.readLine()) != null) {
                    if (!emitter.isCancelled()) {
                        emitter.onNext(br.readLine());
                    } else {
                        break;
                    }
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


    private Transaction mapToTransaction(String line) {
        final String[] p = line.split(",");
        return new Transaction(p[0], p[1], p[2], p[3], LocalDateTime.now(), p[4], p[5], p[6], p[7], p[8], p[9], p[10], p[11], p[12], p[13]);
    }

}
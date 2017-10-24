package nl.programon.filetransfer.endpoints;


import com.google.common.base.Stopwatch;
import io.reactivex.Flowable;
import io.reactivex.functions.BiFunction;
import io.reactivex.functions.Function;
import io.reactivex.schedulers.Schedulers;
import nl.programon.filetransfer.domain.Transaction;
import nl.programon.filetransfer.service.TransactionService;
import org.reactivestreams.Publisher;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.StreamingOutput;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

@Component
@Path("/file")
public class FileEndpoint {

    private static final String COMMA_VALUE = ",";
    private static final int NUMBER_OF_ACCOUNTS = 10;
    private static final DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss.SSS");
    private static final Logger LOG = LoggerFactory.getLogger(FileEndpoint.class);
    private final TransactionService transactionService;


    static final byte CR = 13;
    // Line feed character
    static final byte LF = 10;


    private final ExecutorService threadPoolExecutor = Executors.newFixedThreadPool(6);

    @Autowired
    public FileEndpoint(final TransactionService transactionService) {
        this.transactionService = transactionService;
    }

    private static Publisher<String> transactionToCsv(Transaction transaction) throws Exception{
        final StringBuilder csvBuilder = new StringBuilder();
        csvBuilder.append(transaction.getId());
        csvBuilder.append(COMMA_VALUE);
        csvBuilder.append(transaction.getAccountNumber());
        csvBuilder.append(COMMA_VALUE);
        csvBuilder.append(transaction.getDescription());
        csvBuilder.append(COMMA_VALUE);
        csvBuilder.append(formatter.format(transaction.getField2()));
        csvBuilder.append(COMMA_VALUE);
        csvBuilder.append(transaction.getDescription());
        csvBuilder.append(COMMA_VALUE);
        csvBuilder.append(transaction.getDescription());
        csvBuilder.append(COMMA_VALUE);
        csvBuilder.append(transaction.getDescription());
        csvBuilder.append(COMMA_VALUE);
        csvBuilder.append(transaction.getDescription());
        csvBuilder.append(COMMA_VALUE);
        csvBuilder.append(transaction.getDescription());
        csvBuilder.append(COMMA_VALUE);
        csvBuilder.append(transaction.getDescription());
        csvBuilder.append(COMMA_VALUE);
        csvBuilder.append(transaction.getDescription());
        csvBuilder.append(COMMA_VALUE);
        csvBuilder.append(transaction.getDescription());
        csvBuilder.append(COMMA_VALUE);
        csvBuilder.append(transaction.getDescription());
        csvBuilder.append(COMMA_VALUE);
        csvBuilder.append(transaction.getDescription());
        csvBuilder.append("\n");
        return Flowable.just(csvBuilder.toString());
    }

    @Path("/csv")
    @GET
    @Produces("application/octet-stream")
    public Response getDirectCsvFile() {
        try {
            final Stopwatch stopwatch = Stopwatch.createStarted();
            final List<Flowable<Transaction>> observableList = new ArrayList();
            //simulate the concurrent traversal of accounts
            for (int i = 0; i < NUMBER_OF_ACCOUNTS; i++) {
                observableList.add(transactionService.getConnectableTransactions());
            }

            final Flowable<Transaction> observable = Flowable.concat(observableList);

            final StreamingOutput output = output1 -> {
                observable
                        .flatMap((Function<Transaction, Publisher<String>>) FileEndpoint::transactionToCsv, 10)
                        .subscribeOn(Schedulers.from(threadPoolExecutor))
                        .blockingSubscribe(
                                transaction -> output1.write(transaction.getBytes()),
                                e -> {
                                    LOG.error("Something went wrong, disconnected client: {}", e.getMessage());
                                },
                                () -> LOG.debug("Transactions written to stream in {} milliseconds", stopwatch.elapsed(TimeUnit.MILLISECONDS))
                        );

            };

            stopwatch.stop();
            return Response.ok(output).build();
        } catch (WebApplicationException e) {
            return Response.noContent().build();
        }
    }


}
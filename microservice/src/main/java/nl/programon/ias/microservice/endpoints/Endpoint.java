package nl.programon.ias.microservice.endpoints;


import io.reactivex.Flowable;
import io.reactivex.schedulers.Schedulers;
import nl.programon.ias.microservice.client.TransactionService;
import nl.programon.ias.microservice.domain.DownloadStatus;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.StreamingOutput;
import java.time.format.DateTimeFormatter;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

@Component
@Path("/file")
public class Endpoint {

    public static final String COMMA_VALUE = ",";
    private static final Logger LOG = LoggerFactory.getLogger(Endpoint.class);
    private static final String TRXID = "5948ae3b-d279-422d-9fb0-a13ebf1dbeb8";
    private static final DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss.SSS");


    private final ExecutorService threadPoolExecutor = Executors.newFixedThreadPool(1);


    private TransactionService transactionService;

    @Autowired
    public Endpoint(final TransactionService transactionService) {
        this.transactionService = transactionService;


    }


    @Path("/csv/status")
    @GET
    @Produces("application/json")
    //@ResponseStatus(value = HttpStatus.OK)
    public Response getCsvDownloadStatus(@Context HttpServletRequest request) {
        HttpSession session = request.getSession();
        DownloadStatus error = (DownloadStatus) session.getAttribute("status");
        return Response.ok(error).status(Response.Status.OK).build();

    }

    @Path("/csv")
    @GET
    @Produces("text/csv")
    //@ResponseStatus(value = HttpStatus.OK)
    public Response getCsv(@Context HttpServletRequest request) {
        try {

            Flowable<String> flowable = transactionService.getTransactions2();

            final StreamingOutput output = output1 -> flowable
                    .subscribeOn(Schedulers.from(threadPoolExecutor))
                    .blockingSubscribe(
                            transaction -> output1.write(transaction.getBytes()),
                            e -> LOG.error("Something went wrong, disconnected client, please delete the file on the server now: {}", e.getMessage()),
                            () -> LOG.debug("Transactions written to stream")
                    );

            return Response.status(Response.Status.OK).entity(output)
                    .header("Content-Disposition", "attachment; filename=transactions.csv")
                    .header("Content-Type", "text/csv").build();
        } catch (Throwable t) {
            LOG.info(t.getMessage());
        }
        return Response.status(Response.Status.OK).build();

    }


}



package nl.programon.filetransfer.service;

import io.reactivex.BackpressureStrategy;
import io.reactivex.Flowable;
import nl.programon.filetransfer.domain.Transaction;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Service;

import java.io.*;
import java.net.URL;
import java.time.LocalDateTime;
import java.util.Iterator;
import java.util.stream.Stream;

@Service
@ConfigurationProperties
public class TransactionService {

    private static final int NUMBEROFPAGES = 5;
    private static final Logger LOG = LoggerFactory.getLogger(TransactionService.class);

    private static Transaction mapToTransaction(String line) {
        final String[] p = line.split(",");
        return new Transaction(p[0], p[1], p[2], p[3], LocalDateTime.now(), p[4], p[5], p[6], p[7], p[8], p[9], p[10], p[11], p[12], p[13]);
    }

    public Flowable<Transaction> getTransactions() {
        return Flowable.create(emitter -> {
            try {
                for (int i = 0; i < NUMBEROFPAGES; i++) {
                    Reader r = null;
                    BufferedReader br = null;
                    try {
                        URL url = this.getClass().getResource("/transactions_" + i + ".csv");
                        final File f = new File(url.toURI());
                        r = new InputStreamReader(new FileInputStream(f));
                        br = new BufferedReader(r);
                        //Convert to iterator to discover the last item
                        Stream<String> stream = br.lines();
                        Iterator<String> it = stream.iterator();
                        while (it.hasNext()) {
                            Transaction transaction = mapToTransaction(it.next());
                            emitter.onNext(transaction);
                        }
                    } catch (FileNotFoundException e) {
                        LOG.error(e.getMessage());
                    } finally {
                        br.close();
                        r.close();

                    }
                }
                emitter.onComplete();
            } catch (Exception e) {
                emitter.onError(e);
            }
        }, BackpressureStrategy.BUFFER);

    }

}
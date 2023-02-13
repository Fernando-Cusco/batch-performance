package cusco.mejia.service;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;

import cusco.mejia.http.RestClient;
import cusco.mejia.repository.BookRepository;
import io.quarkus.scheduler.Scheduled;
import lombok.extern.slf4j.Slf4j;

@ApplicationScoped
@Slf4j
public class BatchService {

    @Inject
    BookRepository bookRepository;

    @Inject
    RestClient restClient;

    
    @Scheduled(every = "200s")
    public void process() {
        log.info("Processing...");
        long start = System.currentTimeMillis();
        processBooks();
        long end = System.currentTimeMillis();
        log.info("TIME FINISHED PROCESS: {} ms", (end - start));
    }

    private void processBooks() {
        bookRepository.getAllBooks();
    }

    // implement garbage collector to delete books of the list
    // Runtime runtime =  Runtime.getRuntime();
    // long memory = runtime.totalMemory() -  runtime.freeMemory();
    // double usedMemoryInMB = (double) memory / (1024 * 1024);
    // log.info("Memory used: {}", usedMemoryInMB);
    // books = null;
    // System.gc();
    // // get memory after garbage collector
    // memory = runtime.totalMemory() -  runtime.freeMemory();
    // usedMemoryInMB = (double) memory / (1024 * 1024);
    // log.info("Memory used after GC: {}", usedMemoryInMB);

}

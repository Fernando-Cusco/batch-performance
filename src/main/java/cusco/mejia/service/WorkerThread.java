package cusco.mejia.service;

import java.sql.Connection;

import cusco.mejia.dto.BookDto;
import cusco.mejia.http.RestClient;
import cusco.mejia.repository.BookRepository;
import io.vertx.core.json.JsonObject;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class WorkerThread implements Runnable {

    private BookDto book;
    private Object lock = new Object();
    private final BookRepository bookRepository;
    private final RestClient restClient;
    private final Connection connection;

    public WorkerThread(BookDto book, BookRepository bookRepository, RestClient restClient, Connection connection) {
        this.book = book;
        this.bookRepository = bookRepository;
        this.restClient = restClient;
        this.connection = connection;
    }

    @Override
    public void run() {
        log.info("Thread name: {}", Thread.currentThread().getName());
        log.info("Empezo a procesar, llega con: {}", book.getId());
        JsonObject res = restClient.sendRequest("https://reqres.in/api/users/2");
        log.info("Response: {} para {}", res, book.getId());
        // synchronized (lock) {
            long start = System.currentTimeMillis();
                log.info("Objeto actual para actualizar: {}, hilo contenedor: {}", book.getId(), Thread.currentThread().getName());
                String data = res.getJsonObject("data").getString("first_name");
                boolean bookUpdated = bookRepository.updateConnection(book.getId(), Thread.currentThread().getName().concat("-").concat(book.getId()+"-"+data), connection);
                if (bookUpdated) {
                    log.info("Book updated: {}", book.getId());
                } else {
                    log.info("Book not updated: {}", book.getId());
                }
                long end = System.currentTimeMillis();
                log.info("TIME FINISHED: {} ms {} {}", (end - start), book.getId(), Thread.currentThread().getName());
        // }
        
    }
}

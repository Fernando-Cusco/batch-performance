package cusco.mejia.repository;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;

import cusco.mejia.datasource.Jdbc;
import cusco.mejia.dto.BookDto;
import cusco.mejia.http.RestClient;
import cusco.mejia.service.WorkerThread;
import lombok.extern.slf4j.Slf4j;

@ApplicationScoped
@Slf4j
public class BookRepository {

    @Inject
    Jdbc jdbc;

    @Inject
    RestClient restClient;


    private static final String SQL_SELECT = "select id, title, author, isbn, price from books";
    private static final String SQL_UPDATE = "update books set title = ?, update_at = ?, identificador = ? where id = ?";

    public void getAllBooks() {
        ExecutorService executorService = Executors.newFixedThreadPool(10);
        Connection connection = jdbc.getConnection();
        try (PreparedStatement ps = connection.prepareStatement(SQL_SELECT)) {
            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                BookDto book = new BookDto();
                book.setId(rs.getLong("id"));
                book.setTitle(rs.getString("title"));
                book.setAuthor(rs.getString("author"));
                book.setIsbn(rs.getString("isbn"));
                book.setPrice(rs.getBigDecimal("price"));
                executorService.execute(new WorkerThread(book, this, restClient, connection));
            }
            executorService.shutdown();
            while (!executorService.isTerminated()) {
            }
            log.info("All threads finished");
        } catch (Exception e) {
            log.error("Error al obtener los libros", e);
        } finally{
            log.info("Closing connection");
            jdbc.closeConnection(connection);
        }
    }

    public boolean updateConnection(Long id, String title, Connection connection) {
        try (PreparedStatement ps = connection.prepareStatement(SQL_UPDATE)) {
            connection.setAutoCommit(false);
            Date date = new Date();
            String pattern = "yyyy-MM-dd HH:mm:ss";
            SimpleDateFormat simpleDateFormat = new SimpleDateFormat(pattern);
            String dateStr = simpleDateFormat.format(date);
            ps.setString(1, title);
            ps.setString(2, dateStr.concat("-").concat(id+""));
            ps.setLong(3, id);
            ps.setLong(4, id);
            int rows = ps.executeUpdate();
            if (rows > 0) {
                log.info("Libro actualizado: {} con fecha: {}", id, dateStr);
                return true;
            }
            return false;

        } catch (Exception e) {
            try {
                connection.rollback();
                log.error("Aplicando Rollback", e);
            } catch (SQLException e1) {
                e1.printStackTrace();
                log.error("Error al hacer rollback", e1);
            }
            log.error("Error al obtener los libros", e);
        } finally{
            try {
                connection.commit();
                connection.setAutoCommit(true);
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
        return false;
    }
    
}

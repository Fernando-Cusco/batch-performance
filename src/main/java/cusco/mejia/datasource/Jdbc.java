package cusco.mejia.datasource;
import java.sql.Connection;
import java.sql.SQLException;

import javax.inject.Inject;
import javax.inject.Singleton;

import io.agroal.api.AgroalDataSource;
import lombok.extern.slf4j.Slf4j;

@Singleton
@Slf4j
public class Jdbc {
    
    @Inject
    AgroalDataSource dataSource;

    public Connection getConnection() {
        try {
            log.info("Getting connection");
            return dataSource.getConnection();
        } catch (SQLException e) {
            log.error("Error getting connection", e);
            throw new RuntimeException(e);
        }
    }

    public void closeConnection(Connection connection) {
        try {
            connection.close();
            log.info("Connection closed");
        } catch (SQLException e) {
            log.error("Error closing connection", e);
            throw new RuntimeException(e);
        }
    }
    

}

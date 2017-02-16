package verification.base;

import org.springframework.jdbc.core.JdbcTemplate;

import java.io.IOException;

/**
 * Created by user on 16.02.2017.
 */
public interface InputDataVerification {

    public JdbcTemplate checkDatabaseConnection(String filePath) throws IOException, Exception;
    public void checkDatabaseData(String filePath);

}

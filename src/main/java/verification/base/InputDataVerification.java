package verification.base;

import org.springframework.jdbc.core.JdbcTemplate;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;

/**
 * Created by user on 16.02.2017.
 */
public interface InputDataVerification {

    public JdbcTemplate getJdbcTemplate(String propPath) throws Exception;
    public void checkDatabaseTables();

}

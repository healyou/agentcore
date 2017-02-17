package inputdata.inputdataverification;

import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.springframework.jdbc.core.JdbcTemplate;

import java.io.*;
import java.sql.*;

/**
 * Created by user on 16.02.2017.
 */
public class InputVerificationImplTest extends Assert {

    private InputDataVerificationImpl dataVerification;

    private Connection dbConnection;
    private Statement statmt;

    @Before
    public void setUpData() {
        dataVerification = new InputDataVerificationImpl();
        createDatabase();
    }

    @After
    public void removeData() {
        clearAndCloseDatabase();
    }

    @Test
    public void testGetJdbcTemplate() {
        try {
            JdbcTemplate jdbcTemplate =
                    dataVerification.getJdbcTemplate(getClass().getResource("testdb.properties").toURI().getPath());
            assertNotNull(jdbcTemplate);
        } catch (Exception e) {
            fail(e.toString());
        }
    }

    private void createDatabase() {
        try {
            String dbPath = getClass().getResource("testDatabase.s3db").toURI().getPath();
            dbConnection = DriverManager.getConnection("jdbc:sqlite:" + dbPath);
            statmt = dbConnection.createStatement();

            // create tables
            String filePath = getClass().getResource("createTables.sql").toURI().getPath();
            BufferedReader br = new BufferedReader(new FileReader(filePath));
            StringBuilder sql = new StringBuilder();
            while (br.ready())
                sql.append(br.readLine());
            statmt.execute(sql.toString());

            // setupdata
            sql.setLength(0);
            filePath = getClass().getResource("testDbData.sql").toURI().getPath();
            br = new BufferedReader(new FileReader(filePath));
            while (br.ready())
                sql.append(br.readLine());
            statmt.execute(sql.toString());
        } catch (Exception e) {
            fail(e.toString());
        }
    }

    private void clearAndCloseDatabase() {
        try {
            // remove db data
            statmt.execute("drop table intsedent;");

            statmt.close();
            dbConnection.close();
        } catch (SQLException e) {
            fail(e.toString());
        }
    }

}

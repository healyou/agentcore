package verification;

import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.springframework.jdbc.core.JdbcTemplate;
import org.sqlite.SQLiteConnection;

import java.io.*;
import java.net.URISyntaxException;
import java.nio.file.Files;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;

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

        try {
            //Class.forName("org.sqlite.JDBC");
            dbConnection = DriverManager.getConnection("jdbc:sqlite:testDatabase.s3db");
            statmt = dbConnection.createStatement();
        } catch (SQLException e) {
            fail(e.toString() + "dbConnection");
        }
        try {
            String sql = new BufferedReader(new FileReader(getClass().getResource("createTables.sql").toURI().getPath())).
                    lines().toString();
            /*sql = "CREATE TABLE if not exists intsedent " +
                    "(id INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL," +
                    " shortinfo      TEXT              NOT NULL, " +
                    " info           TEXT              NOT NULL, " +
                    " extrainfo      TEXT);";*/
            statmt.execute(sql);

            sql = "INSERT INTO intsedent (shortinfo, info, extrainfo) " +
                    "VALUES  ('1','1','1'); ";
            statmt.execute(sql);
        } catch (SQLException e) {
            fail(e.toString() + "create table");
        } catch (Exception e) {
            fail(e.toString() + "create table");
        }
    }

    @After
    public void removeData() {
        dataVerification = new InputDataVerificationImpl();
        try {
            //dbConnection.close();
            statmt.close();
        } catch (SQLException e) {
            fail(e.toString());
        }
    }

    @Test
    public void testCheckDatabase() {
        try {
            JdbcTemplate jdbcTemplate =
                    dataVerification.checkDatabaseConnection(getClass().getResource("testdb.properties").toURI().getPath());
        } catch (Exception e) {
            fail(e.toString());
        }
    }

}

package verification;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.springframework.jdbc.core.JdbcTemplate;

/**
 * Created by user on 16.02.2017.
 */
public class InputVerificationImplTest extends Assert {

    private InputDataVerificationImpl dataVerification;

    @Before
    public void setUpData() {
        dataVerification = new InputDataVerificationImpl();
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

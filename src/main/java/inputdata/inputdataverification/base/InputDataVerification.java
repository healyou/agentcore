package inputdata.inputdataverification.base;

import inputdata.inputdataverification.inputdata.InputTableDesc;
import org.springframework.jdbc.core.JdbcTemplate;

/**
 * Created by user on 16.02.2017.
 */
public interface InputDataVerification {

    public JdbcTemplate getJdbcTemplate(String propPath) throws Exception;
    public InputTableDesc getDatabaseTables(String descFileName) throws Exception;
    public void testReadDbData(InputTableDesc tableDesc, String dbPath) throws Exception;

}

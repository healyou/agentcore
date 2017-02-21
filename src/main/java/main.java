import inputdata.inputdataverification.InputDataVerificationImpl;
import inputdata.inputdataverification.inputdata.TableDesc;
import org.springframework.jdbc.core.JdbcTemplate;

/**
 * Created on 17.02.2017 12:03
 *
 * @autor Nikita Gorodilov
 */
public class main {

    public static void main(String[] args) {
        InputDataVerificationImpl dataVerification = new InputDataVerificationImpl();
        try {
            TableDesc tableDesc = dataVerification.
                    getDatabaseTables("C:\\Users\\lappi\\Intellij IDEA\\Projects\\agentcore\\src\\test\\resources\\inputdata\\inputdataverification\\tableDescription.xml");
            JdbcTemplate jdbcTemplate = dataVerification.getJdbcTemplate("src\\test\\resources\\inputdata\\inputdataverification\\testdb.properties");
            dataVerification.testReadDbData(jdbcTemplate, tableDesc);
        } catch (Exception e) {
            System.out.println(e.toString());
        }
    }

}

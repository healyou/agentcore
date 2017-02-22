import agentfoundation.localdatabase.AgentDatabaseImpl;
import database.dto.DtoEntityImpl;
import inputdata.inputdataverification.InputDataVerificationImpl;
import inputdata.inputdataverification.inputdata.InputDataTableDesc;
import inputdata.inputdataverification.inputdata.LocalDataTableDesc;
import inputdata.inputdataverification.inputdata.TableColumn;
import org.springframework.jdbc.core.JdbcTemplate;

import java.util.HashMap;

/**
 * Created on 17.02.2017 12:03
 *
 * @autor Nikita Gorodilov
 */
public class main {

    public static void main(String[] args) {
        agentlocaldatabase();
        //inputdataverif();
    }

    public static void agentlocaldatabase() {
        InputDataVerificationImpl dataVerification = new InputDataVerificationImpl();
        try {
            InputDataTableDesc tableDesc = dataVerification.
                    getDatabaseTables("C:\\Users\\lappi\\IdeaProjects\\agentcore\\src\\test\\resources\\inputdata\\inputdataverification\\tableDescription.xml");

            AgentDatabaseImpl agentDb = new AgentDatabaseImpl(tableDesc,
                    AgentDatabaseImpl.class.getResource("localsqlitedb.properties").toURI().getPath());
            LocalDataTableDesc localDataTD = agentDb.getLocalDbTableDesc();

            HashMap<String, String> paramType = new HashMap<>();
            for (TableColumn column : localDataTD.getColumns()) {
                paramType.put(column.getColumnName(), column.getColumnType());
            }
            HashMap<String, Object> paramValue = new HashMap<>();
            for (TableColumn column : localDataTD.getColumns()) {
                if (!column.getColumnName().equals("id"))
                    paramValue.put(column.getColumnName(), "1");
                else
                    paramValue.put(column.getColumnName(), "");
            }

            DtoEntityImpl entity = new DtoEntityImpl(paramType, paramValue);
            agentDb.addSolution(entity);

            paramValue.put("id", 2);
            paramValue.put("info", 2);
            DtoEntityImpl updateEntity = new DtoEntityImpl(paramType, paramValue);
            agentDb.updateSolution(updateEntity);
        } catch (Exception e) {
            System.out.println(e.toString());
        }
    }

    public static void inputdataverif() {
        InputDataVerificationImpl dataVerification = new InputDataVerificationImpl();
        try {
            InputDataTableDesc tableDesc = dataVerification.
                    getDatabaseTables("C:\\Users\\lappi\\Intellij IDEA\\Projects\\agentcore\\src\\test\\resources\\inputdata\\inputdataverification\\tableDescription.xml");
            JdbcTemplate jdbcTemplate = dataVerification.getJdbcTemplate("src\\test\\resources\\inputdata\\inputdataverification\\testdb.properties");
            dataVerification.testReadDbData(jdbcTemplate, tableDesc);
        } catch (Exception e) {
            System.out.println(e.toString());
        }
    }

}

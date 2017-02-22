package inputdata.inputdataverification;

import agentfoundation.localdatabase.AgentDatabaseImpl;
import database.dto.DtoEntityImpl;
import inputdata.inputdataverification.inputdata.InputDataTableDesc;
import inputdata.inputdataverification.inputdata.LocalDataTableDesc;
import inputdata.inputdataverification.inputdata.TableColumn;
import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.springframework.jdbc.datasource.DriverManagerDataSource;

import java.sql.SQLException;
import java.util.HashMap;
import java.util.Properties;

/**
 * Created by lappi on 22.02.2017.
 */
public class AgentDatabaseImplTest extends Assert {

    private AgentDatabaseImpl agentDb;

    @Before
    public void setUpData() {
        InputDataVerificationImpl dataVerification = new InputDataVerificationImpl();
        try {
            InputDataTableDesc tableDesc = dataVerification.getDatabaseTables(
                    InputDataVerificationImpl.class.getResource("tableDescription.xml").toURI().getPath());

            agentDb = new AgentDatabaseImpl(tableDesc,
                    InputDataVerificationImpl.class.getResource("testdb.properties").toURI().getPath());
        } catch (Exception e) {
            fail(e.toString());
        }
    }

    @After
    public void removeData() {
        try {
            agentDb.clearDatabase();
        } catch (SQLException e) {
            fail(e.toString());
        }
    }

    @Test
    public void testErrorCreateTableDesc() {
        InputDataVerificationImpl dataVerification = new InputDataVerificationImpl();
        try {
            InputDataTableDesc tableDesc = dataVerification.getDatabaseTables(
                    InputDataVerificationImpl.class.getResource("errorTestData/errorTD.xml").toURI().getPath());
        } catch (Exception e) {
            fail(e.toString());
        }
    }

    @Test(expected=Exception.class)
    public void testErrorCreateAgentDb() throws Exception {
        InputDataVerificationImpl dataVerification = new InputDataVerificationImpl();
        try {
            InputDataTableDesc tableDesc = dataVerification.getDatabaseTables(
                    InputDataVerificationImpl.class.getResource("tableDescription123.xml").toURI().getPath());

            AgentDatabaseImpl agentDb = new AgentDatabaseImpl(tableDesc,
                    InputDataVerificationImpl.class.getResource("errorTestData/errordb.properties").toURI().getPath());
        } catch (Exception e) {
            throw new Exception(e.toString());
        }
    }

    @Test
    public void testWriteDbData() {
        try {
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
        } catch (SQLException e) {
            fail(e.toString());
        }
    }

    @Test
    public void testUpdateDbData() {
        try {
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

            if (!paramValue.containsKey("info"))
                throw new SQLException("нет поля 'info'");

            paramValue.put("id", 1);
            paramValue.put("info", 2);
            DtoEntityImpl updateEntity = new DtoEntityImpl(paramType, paramValue);
            agentDb.updateSolution(updateEntity);
        } catch (SQLException e) {
            fail(e.toString());
        }
    }

    private void clearDbTable() {
        DriverManagerDataSource ds = new DriverManagerDataSource();

        Properties dbprop = new Properties();
        try {
            dbprop.load(getClass().getResource(
                    InputDataVerificationImpl.class.getResource("testdb.properties").toURI().getPath()).openStream());

            String driverClassName = dbprop.getProperty("driverClassName");
            String url = dbprop.getProperty("url");

            ds.setDriverClassName(driverClassName);
            ds.setUrl(url);

            // clear test table
            ds.getConnection().createStatement().execute("");
        } catch (Exception e) {
            fail(e.toString());
        }
    }

}

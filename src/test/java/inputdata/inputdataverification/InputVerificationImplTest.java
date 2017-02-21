package inputdata.inputdataverification;

import inputdata.inputdataverification.inputdata.TableDesc;
import inputdata.inputdataverification.inputdata.TableColumn;
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
    private JdbcTemplate jdbcTemplate;

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

    @Test
    public void testTableDesc() {
        try {
            String propFilePath = getClass().getResource("testdb.properties").toURI().getPath();
            String tableDescFileName = getClass().getResource("tableDescription.xml").toURI().getPath();

            JdbcTemplate jdbcTemplate = dataVerification.getJdbcTemplate(propFilePath);
            TableDesc tableDesc = dataVerification.getDatabaseTables(tableDescFileName);

            assertNotNull(tableDesc);
            assertNotNull(jdbcTemplate);
            assertEquals("intsedent", tableDesc.getTableName());
            assertEquals(1000, tableDesc.getPeriodicityMS());
            assertNotNull(tableDesc.getColumns());
            // смотрим на обязательную колонку в таблице
            assertTrue(isTableColumn(tableDesc, TableDesc.ID_COLUMN_NAME, TableDesc.ID_COLUMN_TYPE));
        } catch (Exception e) {
            fail(e.toString());
        }
    }

    @Test
    public void testReadDbData() {
        try {
            String propFilePath = getClass().getResource("testdb.properties").toURI().getPath();
            String tableDescFileName = getClass().getResource("tableDescription.xml").toURI().getPath();

            JdbcTemplate jdbcTemplate = dataVerification.getJdbcTemplate(propFilePath);
            TableDesc tableDesc = dataVerification.getDatabaseTables(tableDescFileName);

            assertNotNull(tableDesc);
            assertNotNull(jdbcTemplate);

            dataVerification.testReadDbData(jdbcTemplate, tableDesc);
        } catch (Exception e) {
            fail(e.toString());
        }
    }

    @Test(expected=AssertionError.class)
    public void testErrorTableDesc() {
        try {
            String propFilePath = getClass().getResource("testdb.properties").toURI().getPath();
            String tableDescFileName = getClass().getResource("errorTestData/errorTD.xml").toURI().getPath();

            JdbcTemplate jdbcTemplate = dataVerification.getJdbcTemplate(propFilePath);
            TableDesc tableDesc = dataVerification.getDatabaseTables(tableDescFileName);

            assertNotNull(tableDesc);
            assertNotNull(jdbcTemplate);
            assertEquals("intsedent", tableDesc.getTableName());
            assertEquals(1000, tableDesc.getPeriodicityMS());
            assertNotNull(tableDesc.getColumns());
            // смотрим на обязательную колонку в таблице
            assertTrue(isTableColumn(tableDesc, TableDesc.ID_COLUMN_NAME, TableDesc.ID_COLUMN_TYPE));
        } catch (Exception e) {
            fail(e.toString());
        }
    }

    @Test(expected=AssertionError.class)
    public void testErrorReadDbData() {
        try {
            String propFilePath = getClass().getResource("errorTestData/errordb.properties").toURI().getPath();
            String tableDescFileName = getClass().getResource("tableDescription.xml").toURI().getPath();

            JdbcTemplate jdbcTemplate = dataVerification.getJdbcTemplate(propFilePath);
            TableDesc tableDesc = dataVerification.getDatabaseTables(tableDescFileName);

            assertNotNull(tableDesc);
            assertNotNull(jdbcTemplate);

            dataVerification.testReadDbData(jdbcTemplate, tableDesc);
        } catch (Exception e) {
            fail(e.toString());
        }
    }

    /**
     * Проверяет есть ли такой столбец в таблице
     * @param tableDesc описание таблицы
     * @param idColumnName название столбца
     * @param idColumnType тип данных столбца
     * @return да, если такой столбец есть
     */
    private boolean isTableColumn(TableDesc tableDesc, String idColumnName, String idColumnType) {
        for (TableColumn column : tableDesc.getColumns()) {
            if (column.getColumnName().equals(idColumnName) &&
                    column.getColumnType().equals(idColumnType))
                return true;
        }

        return false;
    }

    /**
     * Создаём бд и заполняем её тестовыми данными
     */
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

    /**
     * Очищаем бд
     */
    private void clearAndCloseDatabase() {
        try {
            // remove db data
            //statmt.execute("drop table intsedent;");

            statmt.close();
            dbConnection.close();
        } catch (SQLException e) {
            fail(e.toString());
        }
    }

}

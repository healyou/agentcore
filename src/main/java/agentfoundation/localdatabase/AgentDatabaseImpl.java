package agentfoundation.localdatabase;

import agentfoundation.localdatabase.base.IAgentDatabase;
import inputdata.inputdataverification.inputdata.TableDesc;
import inputdata.inputdataverification.inputdata.TableColumn;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.datasource.DriverManagerDataSource;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Observable;
import java.util.Properties;

/**
 * Created by user on 21.02.2017.
 */
public class AgentDatabaseImpl extends Observable implements IAgentDatabase {

    private final static String DB_PROPERTIES_PATH = "localsqlitedb.properties";
    private final static String TABLE_NAME = "localdata";

    private JdbcTemplate jdbcTemplate;
    private TableDesc localdbTableDesc;

    public AgentDatabaseImpl(TableDesc tableDesc) {
        jdbcTemplate = getJdbcTemplate();
        createOrOpenDatabase(jdbcTemplate, tableDesc);
    }

    @Override
    public void addSolution() throws SQLException {

    }

    @Override
    public void addCollectiveSolution() throws SQLException {
        // стоит ли использовать старый класс и надо ли новый создавать для доступа к 2 полям отдельно?
    }

    /**
     * Создаём таблицы в локальной бд, если их ещё нет
     * @param tableDesc структура таблицы входных данных
     */
    private void createOrOpenDatabase(JdbcTemplate jdbcTemplate, TableDesc tableDesc) {
        try {
            Statement statement = jdbcTemplate.getDataSource().getConnection().createStatement();

            // создание таблицы в локальной бд
            StringBuilder sql = new StringBuilder();
            sql.append("CREATE TABLE if not exists " + TABLE_NAME);
            sql.append("    (id INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL,");

            sql.append(getInputTableColumnSql(tableDesc));
            sql.append(getNewColumnSql());
            // замена последней запятой
            sql.replace(sql.length() - 1, sql.length() - 1, ");");

            statement.execute(sql.toString());
        } catch (SQLException e) {
            System.out.println(e.toString());
        }

    }

    private String getInputTableColumnSql(TableDesc tableDesc) {
        StringBuilder ret = new StringBuilder();

        for (TableColumn tableColumn : tableDesc.getColumns()) {
            String columnName = tableColumn.getColumnName();
            String columnType = tableColumn.getColumnType();

            ret.append(columnName + ' ' + columnType + ',');
        }

        return ret.toString();
    }

    private String getNewColumnSql() {
        // доделать
        // новые поля добавляем какие - они тут указаны будут
        return "";
    }

    private JdbcTemplate getJdbcTemplate() {
        DriverManagerDataSource ds = new DriverManagerDataSource();

        Properties dbprop = new Properties();
        try {
            dbprop.load(getClass().getResource(DB_PROPERTIES_PATH).openStream());

            String driverClassName = dbprop.getProperty("driverClassName");
            String url = dbprop.getProperty("url");

            ds.setDriverClassName(driverClassName);
            ds.setUrl(url);
        } catch (FileNotFoundException e) {
            System.out.println(e.toString());
        } catch (IOException e) {
            System.out.println(e.toString());
        }

        return new JdbcTemplate(ds);
    }

}

package agentfoundation.localdatabase;

import com.google.common.collect.ImmutableList;
import database.dao.LocalDataDao;
import database.dto.DtoEntityImpl;
import inputdata.inputdataverification.inputdata.InputDataTableDesc;
import inputdata.inputdataverification.inputdata.LocalDataTableDesc;
import inputdata.inputdataverification.inputdata.ATableDesc;
import inputdata.inputdataverification.inputdata.TableColumn;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.datasource.DriverManagerDataSource;

import javax.annotation.Nonnull;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;
import java.util.Observable;
import java.util.Properties;

/**
 * Created by user on 21.02.2017.
 */
public class AgentDatabaseImpl extends Observable implements IAgentDatabase {

    // TEXT - String type for table value
    public final static String ANSWER_COLUMN_NAME = "answer";
    public final static String COLLECTIVEANSWER_COLUMN_NAME = "collectiveanswer";
    public final static String TABLE_NAME = "localdata";

    private static String DB_PROPERTIES_PATH;

    private JdbcTemplate jdbcTemplate;
    private LocalDataTableDesc localdbTableDesc;
    private LocalDataDao localDataDao;

    public AgentDatabaseImpl(@Nonnull InputDataTableDesc inputDataTD, @Nonnull String localdbPropPath) {
        DB_PROPERTIES_PATH = localdbPropPath;
        jdbcTemplate = getJdbcTemplate();
        createOrOpenDatabase(jdbcTemplate, inputDataTD);
        localdbTableDesc = createLocalDbDesc(inputDataTD);
        localDataDao = new LocalDataDao(jdbcTemplate, localdbTableDesc);/*как сделать dao для 2 бд*/
    }

    @Override
    public void addSolution(@Nonnull DtoEntityImpl dtoEntity) throws SQLException {
        localDataDao.create(dtoEntity);
    }

    @Override
    public void updateSolution(@Nonnull DtoEntityImpl dtoEntity) throws SQLException {
        localDataDao.update(dtoEntity);
    }

    @Override
    public void clearDatabase() throws SQLException {
        Statement statmt = jdbcTemplate.getDataSource().getConnection().createStatement();
        statmt.execute("drop table " + TABLE_NAME + ";");
    }

    @Override
    public @Nonnull LocalDataTableDesc getLocalDbTableDesc() {
        return localdbTableDesc;
    }

    /**
     * Создаём таблицы в локальной бд, если их ещё нет
     * @param jdbcTemplate работа с бд
     * @param inputDataTD структура таблицы входных данных
     */
    private void createOrOpenDatabase(JdbcTemplate jdbcTemplate, ATableDesc inputDataTD) {
        try {
            Statement statement = jdbcTemplate.getDataSource().getConnection().createStatement();

            // создание таблицы в локальной бд
            statement.execute(createSqlQuery(inputDataTD));
        } catch (SQLException e) {
            System.out.println(e.toString());
        }

    }

    private String createSqlQuery(ATableDesc inputDataTD) {
        StringBuilder sql = new StringBuilder();
        sql.append("CREATE TABLE if not exists " + TABLE_NAME);
        sql.append("    (id INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL,");

        for (TableColumn tableColumn : inputDataTD.getColumns()) {
            if (tableColumn.getColumnName().equals(InputDataTableDesc.ID_COLUMN_NAME))
                continue;

            String columnName = tableColumn.getColumnName();
            String columnType = ATableDesc.translateToSqlType(tableColumn.getColumnType());
            sql.append(columnName + ' ' + columnType + ',');
        }

        sql.append(ANSWER_COLUMN_NAME + " TEXT,");
        sql.append(COLLECTIVEANSWER_COLUMN_NAME + " TEXT);");

        return sql.toString();
    }

    private LocalDataTableDesc createLocalDbDesc(ATableDesc inputDataTD) {
        List<TableColumn> columns = new ArrayList<>();

        for (TableColumn tableColumn : inputDataTD.getColumns())
            columns.add(new TableColumn(tableColumn.getColumnName(), tableColumn.getColumnType()));

        columns.add(new TableColumn(ANSWER_COLUMN_NAME, "String"));
        columns.add(new TableColumn(COLLECTIVEANSWER_COLUMN_NAME, "String"));

        return new LocalDataTableDesc(TABLE_NAME, ImmutableList.copyOf(columns));
    }

    private JdbcTemplate getJdbcTemplate() {
        DriverManagerDataSource ds = new DriverManagerDataSource();

        Properties dbprop = new Properties();
        try {
            dbprop.load(new FileInputStream(DB_PROPERTIES_PATH));

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

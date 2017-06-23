package agentcore.agentfoundation;

import com.google.common.collect.ImmutableList;
import agentcore.database.dao.LocalDataDao;
import agentcore.database.dto.LocalDataDto;
import agentcore.inputdata.InputDataTableDesc;
import agentcore.inputdata.LocalDataTableDesc;
import agentcore.inputdata.ATableDesc;
import agentcore.inputdata.TableColumn;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.datasource.DriverManagerDataSource;

import javax.annotation.Nonnull;
import java.net.URL;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;
import java.util.Observable;
import java.util.Properties;

import static com.google.common.base.Preconditions.checkNotNull;

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

    public AgentDatabaseImpl(@Nonnull InputDataTableDesc inputDataTD, @Nonnull String localdbResPath) {
        DB_PROPERTIES_PATH = localdbResPath;
        jdbcTemplate = getJdbcTemplate();
        try {
            clearDatabase();
        } catch (SQLException e) {
            throw new RuntimeException("Невозможно очистить локальную базу данных");
        }
        createOrOpenDatabase(jdbcTemplate, inputDataTD);
        localdbTableDesc = createLocalDbDesc(inputDataTD);
        localDataDao = new LocalDataDao(jdbcTemplate, localdbTableDesc);/*как сделать dao для 2 бд*/
    }

    @Override
    public void addSolution(@Nonnull LocalDataDto dtoEntity) throws SQLException {
        localDataDao.create(dtoEntity);
    }

    @Override
    public void updateSolution(@Nonnull LocalDataDto dtoEntity) throws SQLException {
        localDataDao.update(dtoEntity);
    }

    @Override
    public void clearDatabase() throws SQLException {
        Statement statmt = jdbcTemplate.getDataSource().getConnection().createStatement();
        statmt.execute("drop table if exists " + TABLE_NAME + ";");
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
            System.exit(2);
        }

    }

    private String createSqlQuery(ATableDesc inputDataTD) {
        StringBuilder sql = new StringBuilder();
        sql.append("CREATE TABLE if not exists " + TABLE_NAME);
        sql.append("    (id INTEGER PRIMARY KEY NOT NULL,");

        for (TableColumn tableColumn : inputDataTD.getColumns()) {
            if (tableColumn.getColumnName().equals(InputDataTableDesc.ID_COLUMN_NAME))
                continue;

            String columnName = tableColumn.getColumnName();
            String columnType = ATableDesc.translateToSqlType(tableColumn.getColumnType());
            checkNotNull(columnName, "columnName is not null");
            checkNotNull(columnType, "columnType is not null");

            String temp = columnName + ' ' + columnType + ',';
            sql.append(temp);
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
            URL resourceUrl = getClass().getClassLoader().getResource(DB_PROPERTIES_PATH);
            checkNotNull(resourceUrl);

            dbprop.load(resourceUrl.openStream());

            String driverClassName = dbprop.getProperty("driverClassName");
            String url = dbprop.getProperty("url");

            ds.setDriverClassName(driverClassName);
            ds.setUrl(url);
        } catch (Exception e) {
            System.out.println(e.toString());
            System.exit(1);
        }

        return new JdbcTemplate(ds);
    }

}

package agentcore.database.dao;

import agentcore.database.dto.ConfigureEntityImpl;
import agentcore.database.dto.InputDataType;
import agentcore.database.dto.LocalDataDto;
import agentcore.database.dto.LocalRowMapper;
import agentcore.inputdata.LocalDataTableDesc;
import agentcore.inputdata.ATableDesc;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.transaction.annotation.Transactional;

import java.sql.SQLException;

/**
 * Created by user on 21.02.2017.
 */
public class LocalDataDao extends ABaseDao<LocalDataDto> implements ILocalDataDao<LocalDataDto> {

    private static String SELECT_BYID_SQL;

    private LocalDataTableDesc tableDesc;
    private JdbcTemplate jdbcTemplate;

    /**
     * Осущ. чтение базы данных
     * @param jdbcTemplate чтение бд
     * @param localdbTableDesc данные о таблице бд
     */
    public LocalDataDao(JdbcTemplate jdbcTemplate, LocalDataTableDesc localdbTableDesc) {
        SELECT_BYID_SQL = "select * from " + localdbTableDesc.getTableName() + " where " +
                ATableDesc.ID_COLUMN_NAME + " = ?";
        this.tableDesc = localdbTableDesc;
        this.jdbcTemplate = jdbcTemplate;
    }

    @Override
    @Transactional(readOnly = true)
    public LocalDataDto get(int id) throws SQLException {
        return jdbcTemplate.queryForObject(
                SELECT_BYID_SQL, new Object[] { id },
                new LocalRowMapper(tableDesc));
    }

    @Override
    @Transactional
    public void create(LocalDataDto entity) throws SQLException {
        String INSERT_SQL = configureInsertSql(entity, tableDesc);
        jdbcTemplate.update(INSERT_SQL);
    }

    @Override
    @Transactional
    public void update(LocalDataDto entity) throws SQLException {
        String UPDATE_SQL = configureUpdateSql(entity, tableDesc);
        jdbcTemplate.update(UPDATE_SQL);
    }

    private String configureUpdateSql(ConfigureEntityImpl entity, LocalDataTableDesc tableDesc) {
        StringBuilder updateSql = new StringBuilder();

        updateSql.append("update ").append(tableDesc.getTableName()).append(" set ");
        for (String columnName : entity.getColumnNames())
            updateSql.append(columnName).append(" = ").append(entity.getValueByColumnName(columnName)).append(",");
        // замена последней запятой
        updateSql.replace(updateSql.length() - 1, updateSql.length(), " where " +
                ATableDesc.ID_COLUMN_NAME + " = " + entity.getValueByColumnName(ATableDesc.ID_COLUMN_NAME));

        return updateSql.toString();
    }

    /**
     * запись создаётся с тем id, который будет указан в entity
     */
    private String configureInsertSql(ConfigureEntityImpl entity, LocalDataTableDesc tableDesc) {
        StringBuilder updateSql = new StringBuilder();

        updateSql.append("insert into ").append(tableDesc.getTableName()).append(" (");
        for (String columnName : entity.getColumnNames())
            //if (!columnName.equals(ATableDesc.ID_COLUMN_NAME))
            updateSql.append(columnName).append(",");
        // замена последней запятой
        updateSql.replace(updateSql.length() - 1, updateSql.length(), ") values (");

        for (String columnName : entity.getColumnNames()) {
            String typeName = entity.getTypeByColumnName(columnName);
            switch (InputDataType.getByName(typeName)) {
                case STRING: {
                    updateSql.append('\'');
                    updateSql.append(entity.getValueByColumnName(columnName));
                    updateSql.append("',");
                    break;
                }
                case INT: {
                    updateSql.append(entity.getValueByColumnName(columnName));
                    updateSql.append(',');
                    break;
                }
                case DOUBLE: {
                    updateSql.append(entity.getValueByColumnName(columnName));
                    updateSql.append(',');
                    break;
                }
                default: {
                    throw new UnsupportedOperationException("Не известный тип данных");
                }
            }
        }

        // замена последней запятой
        updateSql.replace(updateSql.length() - 1, updateSql.length(), ")");

        return updateSql.toString();
    }

}

package database.dao;

import database.dto.DtoEntityImpl;
import database.dto.DtoEntityImplRowMapper;
import inputdata.inputdataverification.inputdata.LocalDataTableDesc;
import inputdata.inputdataverification.inputdata.ATableDesc;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.transaction.annotation.Transactional;

import java.sql.SQLException;

/**
 * Created by user on 21.02.2017.
 */
public class LocalDataDao extends ABaseDao<DtoEntityImpl> implements ILocalDataDao<DtoEntityImpl> {

    private static String INSERT_SQL;
    private static String SELECT_BYID_SQL;
    private static String UPDATE_SQL;

    private LocalDataTableDesc tableDesc;

    /**
     * Осущ. чтение базы данных
     * @param jdbcTemplate чтение бд
     * @param localdbTableDesc данные о таблице бд
     */
    public LocalDataDao(JdbcTemplate jdbcTemplate, LocalDataTableDesc localdbTableDesc) {
        super(jdbcTemplate);

        SELECT_BYID_SQL = "select * from " + localdbTableDesc.getTableName() + " where " +
                ATableDesc.ID_COLUMN_NAME + " = ?";
        this.tableDesc = localdbTableDesc;
    }

    @Override
    @Transactional(readOnly = true)
    public DtoEntityImpl get(int id) throws SQLException {
        return jdbcTemplate.queryForObject(
                SELECT_BYID_SQL, new Object[] { id },
                new DtoEntityImplRowMapper(tableDesc));
    }

    @Override
    @Transactional
    public void create(DtoEntityImpl entity) throws SQLException {
        INSERT_SQL = configureInsertSql(entity, tableDesc);
        jdbcTemplate.update(INSERT_SQL);
    }

    @Override
    @Transactional
    public void update(DtoEntityImpl entity) throws SQLException {
        UPDATE_SQL = configureUpdateSql(entity, tableDesc);
        jdbcTemplate.update(UPDATE_SQL);
    }

    private String configureUpdateSql(DtoEntityImpl entity, LocalDataTableDesc tableDesc) {
        StringBuilder updateSql = new StringBuilder();

        updateSql.append("update " + tableDesc.getTableName() + " set ");
        for (String columnName : entity.getColumnNames())
            if (!columnName.equals(ATableDesc.ID_COLUMN_NAME))
                updateSql.append(columnName + " = " + entity.getValueByColumnName(columnName) + ",");
        // замена последней запятой
        updateSql.replace(updateSql.length() - 1, updateSql.length(), " where " +
                ATableDesc.ID_COLUMN_NAME + " = " + entity.getValueByColumnName(ATableDesc.ID_COLUMN_NAME));

        return updateSql.toString();
    }

    private String configureInsertSql(DtoEntityImpl entity, LocalDataTableDesc tableDesc) {
        StringBuilder updateSql = new StringBuilder();

        updateSql.append("insert into " + tableDesc.getTableName() + " (");
        for (String columnName : entity.getColumnNames())
            if (!columnName.equals(ATableDesc.ID_COLUMN_NAME))
                updateSql.append(columnName + ",");
        // замена последней запятой
        updateSql.replace(updateSql.length() - 1, updateSql.length(), ") values (");

        for (String columnName : entity.getColumnNames())
            if (!columnName.equals(ATableDesc.ID_COLUMN_NAME))
                updateSql.append(entity.getValueByColumnName(columnName) + ",");
        // замена последней запятой
        updateSql.replace(updateSql.length() - 1, updateSql.length(), ")");

        return updateSql.toString();
    }

}

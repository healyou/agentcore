package agentcore.database.dao;

import agentcore.database.dto.InputDataDto;
import agentcore.database.dto.InputRowMapper;
import agentcore.inputdata.ATableDesc;
import agentcore.inputdata.InputDataTableDesc;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.sql.SQLException;

/**
 * @author Nikita Gorodilov
 */
public class InputDataDao extends ABaseDao<InputDataDto> implements IInputDataDao<InputDataDto> {

    private static String SELECT_FIRST_SQL;
    private static String DELETE_SQL;

    private InputDataTableDesc tableDesc;
    private JdbcTemplate jdbcTemplate;

    /**
     * Осущ. чтение базы данных
     * @param jdbcTemplate чтение бд
     * @param tableDesc данные о таблице бд
     */
    public InputDataDao(@NotNull JdbcTemplate jdbcTemplate, @NotNull InputDataTableDesc tableDesc) {
        SELECT_FIRST_SQL = "select * from " + tableDesc.getTableName() + " order by ? limit 1";
        DELETE_SQL = "delete from " + tableDesc.getTableName() + " where id=?";
        this.tableDesc = tableDesc;
        this.jdbcTemplate = jdbcTemplate;
    }

    @Override
    @Transactional(readOnly = true)
    @Nullable
    public InputDataDto getFirst() throws SQLException {
        return jdbcTemplate.queryForObject(
                SELECT_FIRST_SQL, new Object[] { ATableDesc.ID_COLUMN_NAME },
                new InputRowMapper(tableDesc));
    }

    @Override
    @Transactional
    public void delete(@NotNull InputDataDto entity) throws SQLException {
        jdbcTemplate.update(DELETE_SQL, entity.getValueByColumnName(ATableDesc.ID_COLUMN_NAME));
    }

}

package database.dao;

import database.dao.base.ABaseDao;
import database.dao.base.IInputDataDao;
import database.dto.DtoEntityImpl;
import database.dto.DtoEntityImplRowMapper;
import inputdata.inputdataverification.inputdata.InputDataTableDesc;
import inputdata.inputdataverification.inputdata.base.ATableDesc;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.transaction.annotation.Transactional;

import java.sql.SQLException;

/**
 * Created on 17.02.2017 20:50
 *
 * @autor Nikita Gorodilov
 */
public class InputDataDao extends ABaseDao<DtoEntityImpl> implements IInputDataDao<DtoEntityImpl> {

    private static String SELECT_FIRST_SQL;
    private static String DELETE_SQL;

    private InputDataTableDesc tableDesc;

    /**
     * Осущ. чтение базы данных
     * @param jdbcTemplate чтение бд
     * @param tableDesc данные о таблице бд
     */
    public InputDataDao(JdbcTemplate jdbcTemplate, InputDataTableDesc tableDesc) {
        super(jdbcTemplate);

        SELECT_FIRST_SQL = "select * from " + tableDesc.getTableName() + " order by ? limit 1";
        DELETE_SQL = "delete from " + tableDesc.getTableName() + " where id = ?";
        this.tableDesc = tableDesc;
    }

    @Override
    @Transactional(readOnly = true)
    public DtoEntityImpl getFirst(String columnIdName) throws SQLException {
        return jdbcTemplate.queryForObject(
                SELECT_FIRST_SQL, new Object[] { columnIdName },
                new DtoEntityImplRowMapper(tableDesc));
    }

    @Override
    @Transactional
    public void delete(DtoEntityImpl entity, String columnIdName) throws SQLException {
        jdbcTemplate.query(DELETE_SQL, new Object[] {
                entity.getValueByColumnName(columnIdName) },
                new DtoEntityImplRowMapper(tableDesc));
    }

}

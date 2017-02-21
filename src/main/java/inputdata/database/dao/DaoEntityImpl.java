package inputdata.database.dao;

import inputdata.database.dao.base.ABaseDao;
import inputdata.database.dto.DtoEntityImpl;
import inputdata.database.dto.DtoEntityImplRowMapper;
import inputdata.inputdataverification.inputdata.TableDesc;
import org.springframework.jdbc.core.JdbcTemplate;

import java.sql.SQLException;

/**
 * Created on 17.02.2017 20:50
 *
 * @autor Nikita Gorodilov
 */
public class DaoEntityImpl extends ABaseDao<DtoEntityImpl> {

    private static String SELECT_FIRST_SQL;
    private static String DELETE_SQL;

    private TableDesc tableDesc;

    /**
     * Осущ. чтение базы данных
     * @param jdbcTemplate чтение бд
     * @param tableDesc данные о таблице бд
     */
    public DaoEntityImpl(JdbcTemplate jdbcTemplate, TableDesc tableDesc) {
        super(jdbcTemplate);

        SELECT_FIRST_SQL = "select * from " + tableDesc.getTableName() + " order by ? limit 1";
        DELETE_SQL = "delete from " + tableDesc.getTableName() + " where id = ?";
        this.tableDesc = tableDesc;
    }

    @Override
    public DtoEntityImpl getFirst(String columnIdName) throws SQLException {
        return jdbcTemplate.queryForObject(
                SELECT_FIRST_SQL, new Object[] { columnIdName },
                new DtoEntityImplRowMapper(tableDesc));
    }

    @Override
    public void delete(DtoEntityImpl entity, String columnIdName) throws SQLException {
        jdbcTemplate.query(DELETE_SQL, new Object[] {
                entity.getValueByColumnName(columnIdName) },
                new DtoEntityImplRowMapper(tableDesc));
    }

}

package inputdata.database.dao;

import inputdata.database.dao.base.ABaseDao;
import inputdata.database.dto.DtoEntityImpl;
import inputdata.database.dto.DtoEntityImplRowMapper;
import inputdata.inputdataverification.inputdata.InputTableDesc;
import org.springframework.jdbc.core.JdbcTemplate;

import java.sql.SQLException;

/**
 * Created on 17.02.2017 20:50
 *
 * @autor Nikita Gorodilov
 */
public class DaoEntityImpl extends ABaseDao<DtoEntityImpl> {

    //"select * from intsedent ORDER BY id limit 1"
    private static final String SELECT_FIRST_SQL = "select * from ? order by ? limit 1";
    private static final String DELETE_SQL = "delete from ? where id = ?";

    private InputTableDesc inputTableDesc;

    public DaoEntityImpl(JdbcTemplate jdbcTemplate, InputTableDesc inputTableDesc) {
        super(jdbcTemplate);

        this.inputTableDesc = inputTableDesc;
    }

    @Override
    public DtoEntityImpl getFirst(String columnIdName, String tableName) throws SQLException {
        return jdbcTemplate.queryForObject(
                SELECT_FIRST_SQL, new Object[] { tableName, columnIdName },
                new DtoEntityImplRowMapper(inputTableDesc));
    }

    @Override
    public void delete(DtoEntityImpl entity, String columnIdName, String tableName) throws SQLException {
        jdbcTemplate.query(DELETE_SQL, new Object[] {
                tableName, entity.getValueByColumnName(columnIdName) },
                new DtoEntityImplRowMapper(inputTableDesc));
    }

}

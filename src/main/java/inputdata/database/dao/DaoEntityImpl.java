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
    private static String SELECT_FIRST_SQL;
    private static  String DELETE_SQL;

    private InputTableDesc inputTableDesc;

    public DaoEntityImpl(JdbcTemplate jdbcTemplate, InputTableDesc inputTableDesc) {
        super(jdbcTemplate);

        SELECT_FIRST_SQL = "select * from " + inputTableDesc.getTableName() + " order by ? limit 1";
        DELETE_SQL = "delete from " + inputTableDesc.getTableName() + " where id = ?";
        this.inputTableDesc = inputTableDesc;
    }

    @Override
    public DtoEntityImpl getFirst(String columnIdName) throws SQLException {
        return jdbcTemplate.queryForObject(
                SELECT_FIRST_SQL, new Object[] { columnIdName },
                new DtoEntityImplRowMapper(inputTableDesc));
    }

    @Override
    public void delete(DtoEntityImpl entity, String columnIdName) throws SQLException {
        jdbcTemplate.query(DELETE_SQL, new Object[] {
                entity.getValueByColumnName(columnIdName) },
                new DtoEntityImplRowMapper(inputTableDesc));
    }

}

package inputdata.database.dao.base;

import inputdata.database.dto.base.ABaseDtoEntity;
import org.springframework.jdbc.core.JdbcTemplate;

import java.sql.SQLException;

/**
 * Created on 17.02.2017 20:46
 *
 * @autor Nikita Gorodilov
 */
public abstract class ABaseDao<T extends ABaseDtoEntity> {

    protected JdbcTemplate jdbcTemplate;

    public ABaseDao(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    public abstract T getFirst(String columnIdName) throws SQLException;
    public abstract void delete(T entity, String columnIdName) throws SQLException;

}

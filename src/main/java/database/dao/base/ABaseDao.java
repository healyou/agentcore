package database.dao.base;

import database.dto.base.ABaseDtoEntity;
import org.springframework.jdbc.core.JdbcTemplate;

import java.sql.SQLException;

/**
 * Created on 17.02.2017 20:46
 * Нужен для чтение объектов из базы данных,
 * таблица которой будет известна во время исполнения
 *
 * @autor Nikita Gorodilov
 */
public abstract class ABaseDao<T extends ABaseDtoEntity> {

    protected JdbcTemplate jdbcTemplate;

    public ABaseDao(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

}

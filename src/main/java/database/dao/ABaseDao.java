package database.dao;

import database.dto.ABaseDtoEntity;
import org.springframework.jdbc.core.JdbcTemplate;

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

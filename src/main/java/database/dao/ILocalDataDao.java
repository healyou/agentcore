package database.dao;

import database.dto.ABaseDtoEntity;

import java.sql.SQLException;

/**
 * Created by user on 21.02.2017.
 */
public interface ILocalDataDao<T extends ABaseDtoEntity> {

    T get(int id) throws SQLException;
    void create(T entity) throws SQLException;
    void update(T entity) throws SQLException;

}

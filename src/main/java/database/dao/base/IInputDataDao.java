package database.dao.base;

import database.dto.base.ABaseDtoEntity;

import java.sql.SQLException;

/**
 * Created by user on 21.02.2017.
 */
public interface IInputDataDao<T extends ABaseDtoEntity> {

    public abstract T getFirst(String columnIdName) throws SQLException;
    public abstract void delete(T entity, String columnIdName) throws SQLException;

}

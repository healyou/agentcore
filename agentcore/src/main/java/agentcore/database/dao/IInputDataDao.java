package agentcore.database.dao;

import agentcore.database.dto.ABaseDtoEntity;

import java.sql.SQLException;

/**
 * Created by user on 21.02.2017.
 */
public interface IInputDataDao<T extends ABaseDtoEntity> {

    public abstract T getFirst() throws SQLException;
    public abstract void delete(T entity) throws SQLException;

}

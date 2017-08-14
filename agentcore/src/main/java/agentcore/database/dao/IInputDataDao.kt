package agentcore.database.dao

import agentcore.database.dto.ABaseDtoEntity

import java.sql.SQLException

/**
 * Created by user on 21.02.2017.
 */
interface IInputDataDao<T : ABaseDtoEntity> {

    @Throws(SQLException::class)
    fun getFirst(): T?

    @Throws(SQLException::class)
    fun delete(entity: T)
}

package agentcore.database.dao

import agentcore.database.dto.ABaseDtoEntity

import java.sql.SQLException

/**
 * Created by user on 21.02.2017.
 */
interface ILocalDataDao<T : ABaseDtoEntity> {

    @Throws(SQLException::class)
    operator fun get(id: Int): T

    @Throws(SQLException::class)
    fun create(entity: T)

    @Throws(SQLException::class)
    fun update(entity: T)
}

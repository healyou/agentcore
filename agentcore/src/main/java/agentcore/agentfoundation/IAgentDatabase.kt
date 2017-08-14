package agentcore.agentfoundation

import agentcore.database.dto.LocalDataDto
import agentcore.inputdata.ATableDesc
import java.sql.SQLException

/**
 * Created by user on 21.02.2017.
 */
interface IAgentDatabase {

    @Throws(SQLException::class)
    fun addSolution(dtoEntity: LocalDataDto)

    @Throws(SQLException::class)
    fun updateSolution(dtoEntity: LocalDataDto)

    @Throws(SQLException::class)
    fun clearDatabase()

    val localDbTableDesc: ATableDesc
}

package agenttask.agentbrain

import agentcore.agentfoundation.AgentDatabaseImpl
import agentcore.agentfoundation.AgentObserverArg
import agentcore.database.dao.InputDataDao
import agentcore.inputdata.InputDataVerificationImpl
import org.junit.After
import org.junit.Assert
import org.junit.Before
import org.junit.Test
import java.sql.SQLException
import java.util.*

/**
 * @author Nikita Gorodilov
 */
class TestGeneticsAgentBrain: Assert() {

    lateinit private var agentDb: AgentDatabaseImpl
    lateinit private var brain: GeneticsAgentBrain

    var agentObserverArg: Any? = null

    @Before
    fun setUpData() {
        val dataVerification = InputDataVerificationImpl()
        try {
            val tableDesc = dataVerification.getDatabaseTables(
                    InputDataVerificationImpl::class.java.getResource("tableDescription.xml").toURI().path)

            agentDb = AgentDatabaseImpl(tableDesc, "agentcore/inputdata/testdb.properties")

            val localdb = AgentDatabaseImpl(tableDesc,
                    "agentcore/agentfoundation/localsqlitedb.properties")
            val jdbcTemplate = dataVerification.getJdbcTemplate(InputDataVerificationImpl::class.java.getResource("testdb.properties").toURI().path)
            val inputDataDao = InputDataDao(jdbcTemplate, tableDesc)
            brain = GeneticsAgentBrain(
                    inputDataDao,
                    localdb,
                    GeneticsAgentBrain::class.java.getResource("testclipsfit.CLP").toURI().path
            )
            brain.addObserver { o, arg ->
                agentObserverArg = arg
            }
        } catch (e: Exception) {
            Assert.fail(e.toString())
        }
    }

    @After
    fun removeData() {
        try {
            agentDb.clearDatabase()
        } catch (e: SQLException) {
            Assert.fail(e.toString())
        }
    }

    @Test
    fun testCalculateOutputData() {
        brain.takeInputData()
        brain.calculateOutput()

        agentObserverArg ?: Assert.fail("agentObserverArg is null")

        val arg = agentObserverArg as AgentObserverArg
        // пока приходит сообщение - ошибка чтения данных, а надо 25 чтобы было в ответе
    }
}
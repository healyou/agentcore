package agenttask.agentbrain

import agentcore.agentfoundation.AgentDatabaseImpl
import agentcore.agentfoundation.AgentObserverArg
import agentcore.database.dao.InputDataDao
import agentcore.database.dto.LocalDataDto
import agentcore.inputdata.InputDataVerificationImpl
import agentcore.inputdata.InputVerificationImplTest
import org.junit.After
import org.junit.Assert
import org.junit.Before
import org.junit.Test
import java.io.BufferedReader
import java.io.FileReader
import java.sql.DriverManager
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

        createDatabase()
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

            // writedb data
//            val localDataTD = agentDb.localDbTableDesc
//
//            val paramType = HashMap<String, String>()
//            for (column in localDataTD.columns) {
//                paramType.put(column.columnName, column.columnType)
//            }
//            val paramValue = HashMap<String, Any>()
//            for (column in localDataTD.columns) {
//                if (column.columnName != "id")
//                    paramValue.put(column.columnName, "1")
//                else
//                    paramValue.put(column.columnName, "1")
//            }
//
//            val entity = LocalDataDto(paramType, paramValue)
//            agentDb.addSolution(entity)

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

    /**
     * Создаём бд и заполняем её тестовыми данными
     */
    private fun createDatabase() {
        try {
            val dbPath = GeneticsAgentBrain::class.java.getResource("testDatabase.s3db").toURI().path
            val dbConnection = DriverManager.getConnection("jdbc:sqlite:" + dbPath)
            val statmt = dbConnection.createStatement()

            // create tables
            var filePath = GeneticsAgentBrain::class.java.getResource("createBrainTables.sql").toURI().path
            var br = BufferedReader(FileReader(filePath))
            val sql = StringBuilder()
            while (br.ready())
                sql.append(br.readLine())
            statmt.execute(sql.toString())

            // setupdata
            sql.setLength(0)
            filePath = GeneticsAgentBrain::class.java.getResource("testBrainDbData.sql").toURI().path
            br = BufferedReader(FileReader(filePath))
            while (br.ready())
                sql.append(br.readLine())
            statmt.execute(sql.toString())
        } catch (e: Exception) {
            Assert.fail(e.toString())
        }

    }
}
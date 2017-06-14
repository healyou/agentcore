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

/**
 * @author Nikita Gorodilov
 */
class TestNeuralAgentBrain: Assert() {

    lateinit private var agentDb: AgentDatabaseImpl
    lateinit private var brain: NeuralAgentBrain

    var agentObserverArg: Any? = null

    @Before
    fun setUpData() {
        val dataVerification = InputDataVerificationImpl()

        createDatabase()
        try {
            val tableDesc = dataVerification.getDatabaseTables(
                    javaClass.getResource("brainTableDescription.xml").toURI().path)

            agentDb = AgentDatabaseImpl(tableDesc, "agentcore/inputdata/testdb.properties")

            val jdbcTemplate = dataVerification.getJdbcTemplate(InputDataVerificationImpl::class.java.getResource("testdb.properties").toURI().path)
            val inputDataDao = InputDataDao(jdbcTemplate, tableDesc)
            brain = NeuralAgentBrain(
                    inputDataDao,
                    agentDb,
                    javaClass.getResource("testneuraltraining.csv").toURI().path.substring(1)
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
        val localData = arg.arg as LocalDataDto
        val retValue = localData.getValueByColumnName(LocalDataDto.ANSWER_COLUMN_NAME)

        // todo неправильно работает нейронная сеть - почему то неверно обучает - исправить тест
        if (retValue.toString().toDouble() / 0.99 < 1)
            Assert.fail("Неверное значение")
    }

    /**
     * Создаём бд и заполняем её тестовыми данными
     */
    private fun createDatabase() {
        try {
            val dbPath = InputVerificationImplTest::class.java.getResource("testDatabase.s3db").toURI().path
            val dbConnection = DriverManager.getConnection("jdbc:sqlite:" + dbPath)
            val statmt = dbConnection.createStatement()

            // create tables
            var filePath = javaClass.getResource("createBrainTables.sql").toURI().path
            var br = BufferedReader(FileReader(filePath))
            val sql = StringBuilder()
            while (br.ready())
                sql.append(br.readLine())
            statmt.execute(sql.toString())

            // setupdata
            sql.setLength(0)
            filePath = javaClass.getResource("createBrainDbData.sql").toURI().path
            br = BufferedReader(FileReader(filePath))
            while (br.ready())
                sql.append(br.readLine())
            statmt.execute(sql.toString())
        } catch (e: Exception) {
            Assert.fail(e.toString())
        }
    }
}
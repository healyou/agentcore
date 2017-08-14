package agentcore.agentfoundation

import agentcore.agentcommunication.AgentCommunicationImpl
import agentcore.database.dao.InputDataDao
import agentcore.gui.GuiController
import agentcore.inputdata.InputDataVerificationImpl
import agentcore.inputdata.InputDataTableDesc
import agenttask.agentbrain.ExpertAgentBrain
import agenttask.agentbrain.GeneticsAgentBrain
import agenttask.agentbrain.NeuralAgentBrain
import org.springframework.jdbc.core.JdbcTemplate

import java.io.FileInputStream
import java.util.Properties

import java.lang.Thread.interrupted
import java.lang.Thread.sleep

/**
 * @author Nikita Gorodilov
 */
class Agent @Throws(Exception::class)
constructor(gui: GuiController) : AAgentCommand(), Runnable {

    private var brain: IAgentBrain? = null
    private var updateMs: Int = 0

    private var thread = Thread(this)

    init {
        onInit(gui)
    }

    override fun run() {
        try {
            while (!interrupted()) {
                brain!!.takeInputData()
                brain!!.calculateOutput()
                sleep(updateMs.toLong())
            }
        } catch (e: Exception) {
            println(e.toString() + " прекращение выполнения потока агента")
        }
    }

    override fun start() {
        onStart()
    }

    override fun stop() {
        onStop()
    }

    @Throws(Exception::class)
    private fun onInit(gui: GuiController) {
        // проверка корректности описания входных данных
        val dataVerification = InputDataVerificationImpl()
        val jdbcTemplate = dataVerification.getJdbcTemplate(USER_DB_PROP_PATH)
        val tableDesc = dataVerification.getDatabaseTables(TABLE_DESC_PATH)
        dataVerification.testReadDbData(jdbcTemplate, tableDesc)
        updateMs = tableDesc.periodicityMS

        // инициализация основных переменных
        initCoreData(jdbcTemplate, tableDesc, gui)
    }

    override fun onStart() {
        thread = Thread(this)
        thread.isDaemon = true
        thread.start()
    }

    override fun onStop() {
        thread.interrupt()
    }

    /**
     * инициализация основных переменных
     * @param jdbcTemplate вз-ие с бд
     * *
     * @param tableDesc описание входной таблицы
     * *
     * @throws Exception если была ошибка при инициализации
     */
    @Throws(Exception::class)
    private fun initCoreData(jdbcTemplate: JdbcTemplate,
                             tableDesc: InputDataTableDesc, gui: GuiController) {
        val ac = AgentCommunicationImpl()
        val localdb = AgentDatabaseImpl(tableDesc,
                "agentcore/agentfoundation/localsqlitedb.properties")
        val inputDataDao = InputDataDao(jdbcTemplate, tableDesc)
        brain = TestAgentBrain(inputDataDao, localdb)
        //brain = new GeneticsAgentBrain(inputDataDao, localdb);
        //brain = new NeuralAgentBrain(inputDataDao, localdb);
        //brain = new ExpertAgentBrain(inputDataDao, localdb);
        val comAnalizer = ComAnalizerImpl(tableDesc, ac, localdb)
        // слушает выходные сигналы с мозга агента и от мод. вз-ия с серваком
        brain!!.addObserver(gui)
        brain!!.addObserver(comAnalizer)
        ac.addObserver(comAnalizer)

        // подключаемся к серваку
        connectToServer(ac)
    }

    /**
     * подключение к серваку для агентного взаимодействия
     * @param ac модуль общения с сервером
     * *
     * @throws Exception если не удалось подключиться
     */
    @Throws(Exception::class)
    private fun connectToServer(ac: AgentCommunicationImpl) {
        // подключение к серваку идёт сразу
        val prop = Properties()
        prop.load(FileInputStream(CONNECT_PROP_PATH))
        val hostAdress = prop.getProperty("host_adress")
        val port = Integer.valueOf(prop.getProperty("port"))!!

        ac.connect(hostAdress, port)
    }

    companion object {

        private val USER_DB_PROP_PATH = "data/input/db.properties"
        private val TABLE_DESC_PATH = "data/input/td.xml"
        private val CONNECT_PROP_PATH = "data/input/connect.properties"
    }
}

package agentfoundation;

import agentcommunication.AgentCommunicationImpl;
import agentfoundation.agentbrain.AgentBrainImpl;
import agentfoundation.agentcomandanalizer.ComAnalizerImpl;
import agentfoundation.agentcommand.base.IAgentCommand;
import agentfoundation.agentlifecicle.base.AAgentLifecicle;
import agentfoundation.localdatabase.AgentDatabaseImpl;
import database.dao.LocalDataDao;
import inputdata.inputdataverification.InputDataVerificationImpl;
import inputdata.inputdataverification.inputdata.InputDataTableDesc;
import org.springframework.jdbc.core.JdbcTemplate;

import java.io.FileInputStream;
import java.util.Properties;

/**
 * Created by user on 21.02.2017.
 */
public class Agent extends AAgentLifecicle implements IAgentCommand {

    private final static String USER_DB_PROP_PATH = "data/input/db.properties";
    private final static String TABLE_DESC_PATH = "data/input/td.xml";
    private final static String CONNECT_PROP_PATH = "data/input/connect.properties";

    private AgentBrainImpl brain;
    private ComAnalizerImpl comAnalizer;
    private int updateMs;

    public Agent() throws Exception {
        onInit();
    }

    @Override
    public void start() {

    }

    @Override
    public void stop() {

    }

    @Override
    public void pause() {

    }

    @Override
    public void resume() {

    }

    @Override
    protected void onInit() throws Exception {
        // проверка корректности описания входных данных
        InputDataVerificationImpl dataVerification = new InputDataVerificationImpl();
        JdbcTemplate jdbcTemplate = dataVerification.getJdbcTemplate(USER_DB_PROP_PATH);
        InputDataTableDesc tableDesc = dataVerification.getDatabaseTables(TABLE_DESC_PATH);
        dataVerification.testReadDbData(jdbcTemplate, tableDesc);
        updateMs = tableDesc.getPeriodicityMS();

        String localDbPropPath = AgentDatabaseImpl.class.
                getResource("localsqlitedb.properties").toURI().getPath();
        // инициализация основных переменных
        initCoreData(jdbcTemplate, tableDesc, localDbPropPath);
    }

    @Override
    protected void onStart() {

    }

    @Override
    protected void onPause() {

    }

    @Override
    protected void onResume() {

    }

    @Override
    protected void onStop() {

    }

    /**
     * инициализация основных переменных
     * @param jdbcTemplate вз-ие с бд
     * @param tableDesc описание входной таблицы
     * @param localDbPropPath файл описания jdbc для лок бд
     * @throws Exception если была ошибка при инициализации
     */
    private void initCoreData(JdbcTemplate jdbcTemplate, InputDataTableDesc tableDesc,
                              String localDbPropPath) throws Exception {
        brain = new AgentBrainImpl();
        AgentCommunicationImpl ac = new AgentCommunicationImpl();
        AgentDatabaseImpl localdb = new AgentDatabaseImpl(tableDesc, localDbPropPath);
        LocalDataDao dataDao = new LocalDataDao(jdbcTemplate, localdb.getLocalDbTableDesc());
        comAnalizer = new ComAnalizerImpl(tableDesc, ac, dataDao);
        // слушает выходные сигналы с мозга агента и от мод. вз-ия с серваком
        brain.addObserver(comAnalizer);
        ac.addObserver(comAnalizer);

        // подключаемся к серваку
        connectToServer(ac);
    }

    /**
     * подключение к серваку для агентного взаимодействия
     * @param ac модуль общения с сервером
     * @throws Exception если не удалось подключиться
     */
    private void connectToServer(AgentCommunicationImpl ac) throws Exception {
        // подключение к серваку идёт сразу
        Properties prop = new Properties();
        prop.load(new FileInputStream(CONNECT_PROP_PATH));
        String hostAdress = prop.getProperty("host_adress");
        int port = Integer.valueOf(prop.getProperty("port"));

        ac.connect(hostAdress, port);
    }

}

package agentfoundation;

import agentcommunication.AgentCommunicationImpl;
import agentfoundation.agentbrain.AgentBrainImpl;
import agentfoundation.agentcomandanalizer.ComAnalizerImpl;
import agentfoundation.localdatabase.AgentDatabaseImpl;
import database.dao.LocalDataDao;
import inputdata.inputdataverification.InputDataVerificationImpl;
import inputdata.inputdataverification.inputdata.InputDataTableDesc;
import org.springframework.jdbc.core.JdbcTemplate;

import java.io.FileInputStream;
import java.util.Properties;

import static java.lang.Thread.interrupted;
import static java.lang.Thread.sleep;

/**
 * Created by user on 21.02.2017.
 */
public class Agent extends AAgentCommand implements Runnable {

    private final static String USER_DB_PROP_PATH = "data/input/db.properties";
    private final static String TABLE_DESC_PATH = "data/input/td.xml";
    private final static String CONNECT_PROP_PATH = "data/input/connect.properties";

    private AgentBrainImpl brain;
    private ComAnalizerImpl comAnalizer;
    private AgentCommunicationImpl ac;
    private int updateMs;

    private Thread thread = new Thread(this);

    public Agent() throws Exception {
        onInit();
    }

    @Override
    public void run() {
        try {
            while (!interrupted()) {
                brain.takeInputData();
                brain.calculateOutput();
                sleep(updateMs);
            }
        } catch (Exception e) {
            System.out.println(e.toString() + " прекращение выполнения потока агента");
        }
    }

    @Override
    public void start() {
        onStart();
    }

    @Override
    public void stop() {
        onStop();
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
        thread.setDaemon(true);
        thread.start();
    }

    @Override
    protected void onStop() {
        thread.interrupt();
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
        ac = new AgentCommunicationImpl();
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

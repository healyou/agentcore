package inputdata;

import agentcommunication.AgentCommunicationImpl;
import agentcommunication.AMessage;
import agentcommunication.MCollectiveSolution;
import agentcommunication.MSearchSolution;
import agentfoundation.IAgentBrain;
import agentfoundation.ComAnalizerImpl;
import agentfoundation.AgentDatabaseImpl;
import database.dao.LocalDataDao;
import database.dto.DtoEntityImpl;
import inputdata.InputDataTableDesc;
import inputdata.InputDataVerificationImpl;
import inputdata.LocalDataTableDesc;
import inputdata.TableColumn;
import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.springframework.jdbc.core.JdbcTemplate;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.sql.SQLException;
import java.util.HashMap;

import static java.lang.Thread.interrupted;
import static java.lang.Thread.sleep;

/**
 * Created by lappi on 24.02.2017.
 */
public class ComAnalizerImplTest  extends Assert {

    private final static int PORT = 5678;
    private final static String HOST = "localhost";
    private final static String HOST_ADDRESS = "127.0.0.1";

    private AgentDatabaseImpl agentDb;
    private ComAnalizerImpl comAnalizer;
    private LocalDataDao dataDao;
    private AgentCommunicationImpl agentCom;
    private TestServer server;
    private Thread serverThread;
    private TestAgentBrain agentBrain;

    @Before
    public void setUpData() {
        try {
            InputDataVerificationImpl dataVerification = new InputDataVerificationImpl();
            String propFilePath = InputDataVerificationImpl.class.
                    getResource("testdb.properties").toURI().getPath();
            String tableDescFileName = InputDataVerificationImpl.class.
                    getResource("tableDescription.xml").toURI().getPath();

            JdbcTemplate jdbcTemplate = dataVerification.getJdbcTemplate(propFilePath);
            InputDataTableDesc tableDesc = dataVerification.getDatabaseTables(tableDescFileName);
            agentDb = new AgentDatabaseImpl(tableDesc, propFilePath);

            server = new TestServer(PORT, 0, InetAddress.getByName(HOST), agentDb);
            serverThread = new Thread(server);
            serverThread.setDaemon(true);
            serverThread.start();

            agentCom = new AgentCommunicationImpl();
            agentCom.connect(HOST_ADDRESS, PORT);

            LocalDataTableDesc localTableDesc = agentDb.getLocalDbTableDesc();
            dataDao = new LocalDataDao(jdbcTemplate, localTableDesc);

            comAnalizer = new ComAnalizerImpl(tableDesc, agentCom, agentDb);
            agentBrain = new TestAgentBrain();

            agentCom.addObserver(comAnalizer);
            agentBrain.addObserver(comAnalizer);
        } catch (Exception e) {
            fail(e.toString());
        }
    }

    @After
    public void removeData() {
        try {
            agentDb.clearDatabase();
            agentCom.disconnect();
            serverThread.interrupt();
            server.close();
        } catch (Exception e) {
            fail(e.toString());
        }
    }

    /**
     * 1- добавляем запись в бд с AgentDatabaseImpl.ANSWER_COLUMN_NAME = "1234" всё ост = "1"
     * 2- тк 1234(4 символа) - отправка на сервак, где создаётся и отправляется новый объект
     *      с AgentDatabaseImpl.COLLECTIVEANSWER_COLUMN_NAME = "123" всё ост = "1"
     * 3- запись в бд изменяется на пришедшую с сервака(запись в бд одна с id = 1)
     */
    @Test
    public void testComAnalizerUpdateData() {
        // создаём первую запись в бд - запись будет отправлена на сервак
        addDtoEntityToLocalDb();
        testAddRecordOnLocalDb();

        // отправкауведомления к comAnalizer - он на сервак - сервак изменит в лок бд
        agentBrain.takeInputData();
        agentBrain.calculateOutput();

        // проверяем изменение в локальной базе данных
        testUpdateRecordFromServer();
    }

    /**
     * проверяет новую запись в бд - изменённая от сервака
     */
    private void testUpdateRecordFromServer() {
        try {
            sleep(150);

            DtoEntityImpl dtoEntity = dataDao.get(1);
            Object collectiveValue = dtoEntity.getValueByColumnName(AgentDatabaseImpl.COLLECTIVEANSWER_COLUMN_NAME);
            Object answerValue = dtoEntity.getValueByColumnName(AgentDatabaseImpl.ANSWER_COLUMN_NAME);

            assertTrue(server.isGetClientMessage());
            assertEquals("1", answerValue);
            assertEquals("123", collectiveValue);
        } catch (Exception e) {
            System.out.println(e.toString());
            fail(e.toString());
        }
    }

    /**
     * проверяем коректность добавление записи в бд
     */
    private void testAddRecordOnLocalDb() {
        try {
            DtoEntityImpl dtoEntity = dataDao.get(1);
            Object value = dtoEntity.getValueByColumnName(AgentDatabaseImpl.ANSWER_COLUMN_NAME);
            assertEquals("1234", value);

            value = dtoEntity.getValueByColumnName(AgentDatabaseImpl.COLLECTIVEANSWER_COLUMN_NAME);
            assertEquals("1", value);
        } catch (Exception e) {
            System.out.println(e.toString());
            fail(e.toString());
        }
    }

    /**
     * добавляет одну запись в базу данных
     */
    private void addDtoEntityToLocalDb() {
        LocalDataTableDesc localDataTD = agentDb.getLocalDbTableDesc();

        HashMap<String, String> paramType = new HashMap<>();
        for (TableColumn column : localDataTD.getColumns()) {
            paramType.put(column.getColumnName(), column.getColumnType());
        }
        HashMap<String, Object> paramValue = new HashMap<>();
        for (TableColumn column : localDataTD.getColumns()) {
            if (!column.getColumnName().equals("id")) {
                if (column.getColumnName().equals(AgentDatabaseImpl.ANSWER_COLUMN_NAME))
                    // 4 значение - общее решение будет
                    paramValue.put(column.getColumnName(), "1234");
                else
                    paramValue.put(column.getColumnName(), "1");
            }
            else
                paramValue.put(column.getColumnName(), "");
        }

        DtoEntityImpl entity = new DtoEntityImpl(paramType, paramValue);
        try {
            agentDb.addSolution(entity);
        } catch (SQLException e) {
            System.out.println(e.toString());
        }
    }

    /**
     * Класс для тестирования выдачи выходного сигнала
     */
    private class TestAgentBrain extends IAgentBrain {
        private DtoEntityImpl dtoEntity;
        @Override
        public void takeInputData() {
            try {
                dtoEntity = dataDao.get(1);
            } catch (SQLException e) {
                System.out.println(e.toString());
            }
        }
        @Override
        public void calculateOutput() {
            setChanged();
            notifyObservers(dtoEntity);
        }
    }

    /**
     * Тестовый сервак для проверки 1го пользователя
     * Принимает ответ с текстом 1234 а отправляет 123
     */
    private class TestServer extends ServerSocket implements Runnable {

        private boolean isGetMessage = false;
        private AgentDatabaseImpl agentDatabase;

        public TestServer(int port, int backlog, InetAddress bindAddr, AgentDatabaseImpl agentDb) throws IOException {
            super(port, backlog, bindAddr);
            agentDatabase = agentDb;
        }

        @Override
        public void run() {
            try {
                Socket socket = accept();

                ObjectOutputStream outputStream = new ObjectOutputStream(socket.getOutputStream());
                ObjectInputStream inputStream = new ObjectInputStream(socket.getInputStream());

                while (!interrupted()) {
                    Object object = inputStream.readObject();
                    if (object instanceof AMessage) {
                        isGetMessage = true;
                        if (object instanceof MSearchSolution) {
                            DtoEntityImpl dtoEntity = ((MSearchSolution) object).getDtoEntity();
                            outputStream.writeObject(new MCollectiveSolution(dtoEntity, 1));
                        }
                        if (object instanceof MCollectiveSolution) {
                            // создаём с изменёнными параметрами
                            DtoEntityImpl updateDto = createDtoEntity();
                            if (((MCollectiveSolution) object).getSolutionId().equals(1)) {
                                outputStream.writeObject(
                                        new MSearchSolution(updateDto));
                            }
                        }
                    }
                }

                close();
            } catch (Exception e) { }
        }

        public boolean isGetClientMessage() {
            return isGetMessage;
        }

        private DtoEntityImpl createDtoEntity() {
            LocalDataTableDesc localDataTD = agentDatabase.getLocalDbTableDesc();
            HashMap<String, String> paramType = new HashMap<>();
            for (TableColumn column : localDataTD.getColumns()) {
                paramType.put(column.getColumnName(), column.getColumnType());
            }
            HashMap<String, Object> paramValue = new HashMap<>();
            for (TableColumn column : localDataTD.getColumns()) {
                if (!column.getColumnName().equals("id")) {
                    if (column.getColumnName().equals(AgentDatabaseImpl.COLLECTIVEANSWER_COLUMN_NAME))
                        // 4 значение - общее решение будет
                        paramValue.put(column.getColumnName(), "123");
                    else
                        paramValue.put(column.getColumnName(), "1");
                }
                else
                    paramValue.put(column.getColumnName(), "1");
            }

            return new DtoEntityImpl(paramType, paramValue);
        }
    }

}

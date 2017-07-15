package agentcore.agentcommunication;

import agentcore.database.dto.ALocalDataDto;
import agentcore.database.dto.LocalDataDto;
import agentcore.database.dto.MessageLocalDataDto;
import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.HashMap;
import java.util.Observable;
import java.util.Observer;

import static java.lang.Thread.interrupted;
import static java.lang.Thread.sleep;

/**
 * Created by lappi on 22.02.2017.
 */
public class AgentCommunicationImplTest extends Assert {

    private final static int PORT = 5678;
    private final static String HOST = "localhost";
    private final static String HOST_ADDRESS = "127.0.0.1";

    private TestServer server;
    private Thread serverThread;
    private AgentCommunicationImpl agentCom;
    private TestClientObserver clientObserver;

    @Before
    public void setUpData() {
        try {
            server = new TestServer(PORT, 0, InetAddress.getByName(HOST));

            serverThread = new Thread(server);
            serverThread.setDaemon(true);
            serverThread.start();

            agentCom = new AgentCommunicationImpl();
            agentCom.connect(HOST_ADDRESS, PORT);

            clientObserver = new TestClientObserver();
            agentCom.addObserver(clientObserver);
        } catch (Exception e) {
            fail(e.toString());
        }
    }

    @After
    public void closeData() {
        try {
            agentCom.disconnect();
            serverThread.interrupt();
            server.close();
        } catch (IOException e) {
            fail(e.toString());
        }
    }

    @Test
    public void testSendMessageToServer() {
        try {
            agentCom.sendMassege(new MSearchSolution(new MessageLocalDataDto(getParamType(), getParamValue())));
            sleep(100);

            assertTrue(server.isGetClientMessage());
        } catch (Exception e) {
            fail(e.toString());
        }
    }

    @Test
    public void testGetMessageFromServer() {
        try {
            agentCom.sendMassege(new MSearchSolution(new MessageLocalDataDto(getParamType(), getParamValue())));
            sleep(100);

            assertTrue(clientObserver.isGetMessage());
        } catch (Exception e) {
            fail(e.toString());
        }
    }

    @Test
    public void testGetSearchCollectiveSolution() {
        try {
            agentCom.sendMassege(new MSearchSolution(new MessageLocalDataDto(getParamType(), getParamValue())));
            sleep(100);

            assertTrue(clientObserver.isSearchCollectiveSolution());
        } catch (Exception e) {
            fail(e.toString());
        }
    }

    @Test
    public void testGetCollectiveSolution() {
        try {
            agentCom.sendMassege(new MSearchSolution(new MessageLocalDataDto(getParamType(), getParamValue())));
            sleep(100);

            assertTrue(server.isGetClientMessage());
            assertTrue(clientObserver.isGetMessage());
            assertTrue(clientObserver.isSearchCollectiveSolution());
            assertTrue(clientObserver.isGetSolution());
        } catch (Exception e) {
            fail(e.toString());
        }
    }

    /**
     * Класс для теста - определяет получение смс от тестового сервера
     */
    private class TestClientObserver implements Observer {
        private boolean isGetSolution = false;
        private boolean isSearchCollectiveSolution = false;
        private boolean isGetMessage = false;
        @Override
        public void update(Observable o, Object arg) {
            if (!(arg instanceof Message))
                return;

            if (arg instanceof MSearchSolution) {
                isGetSolution = true;
            }
            if (arg instanceof MCollectiveSolution) {
                isSearchCollectiveSolution = true;
            }
            isGetMessage = true;
        }
        public boolean isGetSolution() {
            if (isGetSolution) {
                isGetSolution = false;
                return true;
            } return false;
        }
        public boolean isSearchCollectiveSolution() {
            if (isSearchCollectiveSolution) {
                isSearchCollectiveSolution = false;
                return true;
            } return false;
        }
        public boolean isGetMessage() {
            if (isGetMessage) {
                isGetMessage = false;
                return true;
            } return false;
        }
    }

    /**
     * Тестовый сервак для проверки 1го пользователя
     */
    private class TestServer extends ServerSocket implements Runnable {
        private boolean isGetMessage = false;
        public TestServer(int port, int backlog, InetAddress bindAddr) throws IOException {
            super(port, backlog, bindAddr);
        }
        @Override
        public void run() {
            try {
                Socket socket = accept();

                ObjectOutputStream outputStream = new ObjectOutputStream(socket.getOutputStream());
                ObjectInputStream inputStream = new ObjectInputStream(socket.getInputStream());

                while (!interrupted()) {
                    Object object = inputStream.readObject();
                    if (object instanceof Message) {
                        isGetMessage = true;

                        // отправим сразу оба для теста
                        if (object instanceof MSearchSolution) {
                            MessageLocalDataDto dtoEntity = ((MSearchSolution) object).getDtoEntity();
                            outputStream.writeObject(new MCollectiveSolution(dtoEntity, 1));
                            outputStream.writeObject(new MSearchSolution(dtoEntity));
                        }
                    }
                }

                close();
            } catch (Exception e) {
                System.out.println("Ошибка тестового сервака");
            }
        }
        public boolean isGetClientMessage() {
            if (isGetMessage) {
                isGetMessage = false;
                return true;
            } return false;
        }
    }

    private HashMap<String, Object> getParamValue() {
        HashMap<String, Object> ret = new HashMap<>();
        ret.put(ALocalDataDto.Companion.getID_COLUMN_NAME(), 0);
        ret.put(ALocalDataDto.Companion.getANSWER_COLUMN_NAME(), 0);
        ret.put(ALocalDataDto.Companion.getCOLLECTIVEANSWER_COLUMN_NAME(), "");
        return ret;
    }

    private HashMap<String, String> getParamType() {
        HashMap<String, String> ret = new HashMap<>();
        ret.put(ALocalDataDto.Companion.getID_COLUMN_NAME(), ALocalDataDto.Companion.getID_COLUMN_TYPE());
        ret.put(ALocalDataDto.Companion.getANSWER_COLUMN_NAME(), ALocalDataDto.Companion.getANSWER_COLUMN_TYPE());
        ret.put(ALocalDataDto.Companion.getCOLLECTIVEANSWER_COLUMN_NAME(), ALocalDataDto.Companion.getCOLLECTIVEANSWER_COLUMN_TYPE());
        return ret;
    }
}

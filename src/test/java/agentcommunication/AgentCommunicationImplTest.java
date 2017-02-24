package agentcommunication;

import agentcommunication.message.ClientMessage;
import agentcommunication.message.ServerMessage;
import database.dto.DtoEntityImpl;
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

            agentCom = AgentCommunicationImpl.getInstance();
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
            agentCom.sendMassege(new ClientMessage(new DtoEntityImpl(null, null)));
            sleep(100);

            assertTrue(server.isGetClientMessage());
        } catch (Exception e) {
            fail(e.toString());
        }
    }

    @Test
    public void testGetMessageFromServer() {
        try {
            agentCom.sendMassege(new ClientMessage(new DtoEntityImpl(null, null)));
            sleep(100);

            assertTrue(clientObserver.isGetServerMessage());
        } catch (Exception e) {
            fail(e.toString());
        }
    }

    /**
     * Класс для теста - определяет получение смс от тестового сервера
     */
    private class TestClientObserver implements Observer {
        private boolean isGetMessage = false;
        @Override
        public void update(Observable o, Object arg) {
            isGetMessage = true;
        }
        public boolean isGetServerMessage() {
            return isGetMessage;
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
                    if (object instanceof ClientMessage) {
                        isGetMessage = true;
                        outputStream.writeObject(
                                new ServerMessage(((ClientMessage) object).getDtoEntity()));
                    }
                }

                close();
            } catch (Exception e) { }
        }
        public boolean isGetClientMessage() {
            return isGetMessage;
        }
    }

}

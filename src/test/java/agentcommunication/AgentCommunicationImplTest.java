package agentcommunication;

import agentcommunication.message.ClientMessage;
import agentcommunication.message.ServerMessage;
import agentcommunication.message.ClientMessage.ClientMessageType;
import agentcommunication.message.ServerMessage.ServerMessageType;
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
            agentCom.sendMassege(new ClientMessage(new DtoEntityImpl(null, null),
                    ClientMessageType.SEARCH_SOLUTION));
            sleep(100);

            assertTrue(server.isGetClientMessage());
        } catch (Exception e) {
            fail(e.toString());
        }
    }

    @Test
    public void testGetMessageFromServer() {
        try {
            agentCom.sendMassege(new ClientMessage(new DtoEntityImpl(null, null),
                    ClientMessageType.SEARCH_SOLUTION));
            sleep(100);

            assertTrue(clientObserver.isGetMessage);
        } catch (Exception e) {
            fail(e.toString());
        }
    }

    @Test
    public void testGetSearchCollectiveSolution() {
        try {
            agentCom.sendMassege(new ClientMessage(new DtoEntityImpl(null, null),
                    ClientMessageType.SEARCH_SOLUTION));
            sleep(100);

            assertTrue(clientObserver.isGetSearchCollectiveSolution);
        } catch (Exception e) {
            fail(e.toString());
        }
    }

    @Test
    public void testGetCollectiveSolution() {
        try {
            agentCom.sendMassege(new ClientMessage(new DtoEntityImpl(null, null),
                    ClientMessageType.GET_SOLUTION));
            sleep(100);

            assertTrue(clientObserver.isGetCollectiveSolution);
        } catch (Exception e) {
            fail(e.toString());
        }
    }

    /**
     * Класс для теста - определяет получение смс от тестового сервера
     */
    private class TestClientObserver implements Observer {
        public boolean isGetSearchCollectiveSolution = false;
        public boolean isGetCollectiveSolution = false;
        public boolean isGetMessage = false;
        @Override
        public void update(Observable o, Object arg) {
            if (arg instanceof ServerMessage) {
                ServerMessage message = (ServerMessage) arg;
                ServerMessageType type = message.getMessageType();

                switch (type) {
                    case SEARCH_COLLECTIVE_SOLUTION:
                        isGetSearchCollectiveSolution = true;
                        break;
                    case GET_COLLECTIVE_SOLUTION:
                        isGetCollectiveSolution = true;
                        break;
                    default:
                        System.out.println("неизвестное сообщение от сервера");
                        break;
                }
                isGetMessage = true;
            }
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
                        ClientMessage message = (ClientMessage) object;
                        DtoEntityImpl dtoEntity = message.getDtoEntity();
                        DtoEntityImpl updateDto = dtoEntity;

                        switch (message.getMessageType()) {
                            case SEARCH_SOLUTION:
                                outputStream.writeObject(
                                        new ServerMessage(dtoEntity, ServerMessageType.SEARCH_COLLECTIVE_SOLUTION));
                                break;
                            case GET_SOLUTION:
                                outputStream.writeObject(
                                        new ServerMessage(updateDto, ServerMessageType.GET_COLLECTIVE_SOLUTION));
                                break;
                            default:
                                System.out.println("неизвестное сообщение от клиента");
                                break;
                        }
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

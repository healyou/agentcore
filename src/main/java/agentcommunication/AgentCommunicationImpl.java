package agentcommunication;

import agentcommunication.base.IAgentCommunication;
import agentcommunication.message.ClientMessage;
import agentcommunication.message.ServerMessage;

import java.io.*;
import java.net.Socket;
import java.util.Observable;

import static java.lang.Thread.interrupted;

/**
 * Created by user on 21.02.2017.
 */
public class AgentCommunicationImpl extends Observable implements IAgentCommunication, Runnable {

    private final static AgentCommunicationImpl instance = new AgentCommunicationImpl();

    private ObjectOutputStream outputStream;
    private ObjectInputStream inputStream;
    private Socket socket;
    private boolean isConnect = false;
    private Thread thread;

    private AgentCommunicationImpl() { }

    public static AgentCommunicationImpl getInstance() {
        return instance;
    }

    @Override
    public void sendMassege(ClientMessage message) throws IOException {
        if (!isConnect)
            return;

        outputStream.writeObject(message);
        outputStream.flush();
    }

    @Override
    public void connect(String host, int port) throws IOException {
        if (isConnect)
            return;

        // создаём подключение
        try {
            socket = new Socket(host, port);
        } catch (IOException e) {
            throw new IOException(e.toString());
        }

        // получаем потоки ввода вывода
        try {
            InputStream in = socket.getInputStream();
            OutputStream out = socket.getOutputStream();
            outputStream = new ObjectOutputStream(out);
            inputStream = new ObjectInputStream(in);
        } catch (Exception e) {
            throw new IOException(e.toString() + "Не удалось получить поток вывода.");
        }

        isConnect = true;
        startThread();
    }

    @Override
    public void disconnect() throws IOException {
        if (!isConnect)
            return;

        try {
            isConnect = false;
            thread.interrupt();
            outputStream.close();
            inputStream.close();
            socket.close();
        } catch (IOException e) {
            throw new IOException(e.toString());
        }
    }

    @Override
    public boolean isConnect() {
        return isConnect;
    }

    /**
     * Получение сообщений от сервера
     */
    @Override
    public void run() {
        // наследоваться от потока + сделать чтение нормальным(чтобы читать данные, когда они есть)
        try {
            while (!interrupted()) {
                Object object = inputStream.readObject();
                checkServerObject(object);
            }
        } catch (Exception e) {
            System.out.println(e.toString() + " ошибка чтения данных с сервера");
        }
    }

    /**
     * Проверяет и записывает данные о пришедшем сообщении с сервера
     * @param object присланный объект
     */
    private void checkServerObject(Object object) {
        if (object instanceof ServerMessage) {
            // уведомить о новом сообщении
        }

        setChanged();
        notifyObservers(object);
    }

    /**
     * запускает выполнение в новом потоке
     */
    private void startThread() {
        thread = new Thread(this);
        thread.setDaemon(true);
        thread.start();
    }

}

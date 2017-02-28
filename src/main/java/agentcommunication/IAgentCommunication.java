package agentcommunication;

import agentcommunication.message.AMessage;

import java.io.IOException;

/**
 * Created by user on 21.02.2017.
 */
public interface IAgentCommunication {

    public void sendMassege(AMessage message)  throws IOException;
    public void connect(String host, int port) throws IOException;
    public void disconnect() throws IOException;
    public boolean isConnect();

}

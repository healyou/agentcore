package agentcore.agentcommunication;

import java.io.IOException;

/**
 * Created by user on 21.02.2017.
 */
public interface IAgentCommunication {

    void sendMassege(Message message)  throws IOException;
    void connect(String host, int port) throws IOException;
    void disconnect() throws IOException;
    boolean isConnect();

}

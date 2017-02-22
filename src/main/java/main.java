import agentcommunication.AgentCommunicationImpl;
import agentcommunication.base.IAgentCommunication;
import agentcommunication.message.ClientMessage;
import agentcommunication.message.ServerMessage;
import agentfoundation.localdatabase.AgentDatabaseImpl;
import database.dto.DtoEntityImpl;
import inputdata.inputdataverification.InputDataVerificationImpl;
import inputdata.inputdataverification.inputdata.InputDataTableDesc;
import inputdata.inputdataverification.inputdata.LocalDataTableDesc;
import inputdata.inputdataverification.inputdata.TableColumn;
import org.springframework.jdbc.core.JdbcTemplate;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.HashMap;

/**
 * Created on 17.02.2017 12:03
 *
 * @autor Nikita Gorodilov
 */
public class main {

    public static void main(String[] args) {
        agentCommunication();
        //agentlocaldatabase();
        //inputdataverif();
    }

    private static void agentCommunication() {
        int port = 5678;
        String host = "localhost";
        ServerSocket serv = null;
        try {
            serv = new ServerSocket(port, 0, InetAddress.getByName(host));
        } catch (IOException e) {
            System.out.println("Порт занят: " + port);
            System.exit(-1);
        }

        try {
            IAgentCommunication agentCom = AgentCommunicationImpl.getInstance();
            agentCom.connect("127.0.0.1", port);

            Socket socket = serv.accept();
            agentCom.sendMassege(new ClientMessage(new DtoEntityImpl(null, null)));

            ObjectInputStream inputStream = new ObjectInputStream(socket.getInputStream());

            //while (true) {
            Object object = inputStream.readObject();
            if (object instanceof ClientMessage) {
                System.out.println("good message from client to server");
                new ObjectOutputStream(socket.getOutputStream()).writeObject(new ServerMessage());
            }

            serv.close();
        } catch (Exception e) {
            System.out.println(e.toString());
        }
    }

    private static void agentlocaldatabase() {
        InputDataVerificationImpl dataVerification = new InputDataVerificationImpl();
        try {
            InputDataTableDesc tableDesc = dataVerification.
                    getDatabaseTables("C:\\Users\\lappi\\IdeaProjects\\agentcore\\src\\test\\resources\\inputdata\\inputdataverification\\tableDescription.xml");

            AgentDatabaseImpl agentDb = new AgentDatabaseImpl(tableDesc,
                    AgentDatabaseImpl.class.getResource("localsqlitedb.properties").toURI().getPath());
            LocalDataTableDesc localDataTD = agentDb.getLocalDbTableDesc();

            HashMap<String, String> paramType = new HashMap<>();
            for (TableColumn column : localDataTD.getColumns()) {
                paramType.put(column.getColumnName(), column.getColumnType());
            }
            HashMap<String, Object> paramValue = new HashMap<>();
            for (TableColumn column : localDataTD.getColumns()) {
                if (!column.getColumnName().equals("id"))
                    paramValue.put(column.getColumnName(), "1");
                else
                    paramValue.put(column.getColumnName(), "");
            }

            DtoEntityImpl entity = new DtoEntityImpl(paramType, paramValue);
            agentDb.addSolution(entity);

            paramValue.put("id", 2);
            paramValue.put("info", 2);
            DtoEntityImpl updateEntity = new DtoEntityImpl(paramType, paramValue);
            agentDb.updateSolution(updateEntity);
        } catch (Exception e) {
            System.out.println(e.toString());
        }
    }

    private static void inputdataverif() {
        InputDataVerificationImpl dataVerification = new InputDataVerificationImpl();
        try {
            InputDataTableDesc tableDesc = dataVerification.
                    getDatabaseTables("C:\\Users\\lappi\\Intellij IDEA\\Projects\\agentcore\\src\\test\\resources\\inputdata\\inputdataverification\\tableDescription.xml");
            JdbcTemplate jdbcTemplate = dataVerification.getJdbcTemplate("src\\test\\resources\\inputdata\\inputdataverification\\testdb.properties");
            dataVerification.testReadDbData(jdbcTemplate, tableDesc);
        } catch (Exception e) {
            System.out.println(e.toString());
        }
    }

}

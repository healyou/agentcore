import agentcommunication.AgentCommunicationImpl;
import agentcommunication.IAgentCommunication;
import agentcommunication.MCollectiveSolution;
import agentcommunication.MSearchSolution;
import agentfoundation.AgentDatabaseImpl;
import database.dto.DtoEntityImpl;
import database.dto.LocalDataDto;
import inputdata.InputDataVerificationImpl;
import inputdata.InputDataTableDesc;
import inputdata.LocalDataTableDesc;
import inputdata.TableColumn;
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
        //agentCommunication();
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
            IAgentCommunication agentCom = new AgentCommunicationImpl();
            agentCom.connect("127.0.0.1", port);

            Socket socket = serv.accept();
            LocalDataDto dtoEntity = new LocalDataDto(null, null);
            agentCom.sendMassege(new MSearchSolution(dtoEntity));

            ObjectInputStream inputStream = new ObjectInputStream(socket.getInputStream());

            //while (true) {
            Object object = inputStream.readObject();
            if (object instanceof MSearchSolution) {
                System.out.println("good message from client to server");
                new ObjectOutputStream(socket.getOutputStream()).writeObject(new MCollectiveSolution(dtoEntity, 1));
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

            LocalDataDto entity = new LocalDataDto(paramType, paramValue);
            agentDb.addSolution(entity);

            paramValue.put("id", 2);
            paramValue.put("info", 2);
            LocalDataDto updateEntity = new LocalDataDto(paramType, paramValue);
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

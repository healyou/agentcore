import agentcommunication.AgentCommunicationImpl;
import agentcommunication.IAgentCommunication;
import agentcommunication.message.MCollectiveSolution;
import agentcommunication.message.MSearchSolution;
import agentfoundation.localdatabase.AgentDatabaseImpl;
import com.mongodb.*;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import database.dto.DtoEntityImpl;
import inputdata.inputdataverification.InputDataVerificationImpl;
import inputdata.inputdataverification.inputdata.InputDataTableDesc;
import inputdata.inputdataverification.inputdata.LocalDataTableDesc;
import inputdata.inputdataverification.inputdata.TableColumn;
import org.bson.*;
import org.springframework.jdbc.core.JdbcTemplate;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;

/**
 * Created on 17.02.2017 12:03
 *
 * @autor Nikita Gorodilov
 */
public class main {

    public static void main(String[] args) {
        mongodb();
        //agentCommunication();
        //agentlocaldatabase();
        //inputdataverif();
    }

    private static void mongodb() {
        // Since 2.10.0, uses MongoClient
        try {
            MongoCredential mongoCredential = MongoCredential.createScramSha1Credential("admin", "inputdata",
                    "admin".toCharArray());

            MongoClient mongoClient = new MongoClient(new ServerAddress("localhost", 27017), Arrays.asList(mongoCredential));
            MongoDatabase db = mongoClient.getDatabase("inputdata");

            MongoCollection<Document> table = db.getCollection("datacollection");

            Document document = new Document();
            document.append("name", new BsonString("mkyong"));
            document.append("age", new BsonInt32(30));
            document.append("createdDate", new BsonDateTime(new Date().getTime()));
            table.insertOne(document);

            System.out.println();

            Object object = table.find().first().get("_id");
            System.out.println(object);
            table.deleteOne(table.find().first());
        } catch (Exception e) {
            System.out.println(e.toString());
        }
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
            DtoEntityImpl dtoEntity = new DtoEntityImpl(null, null);
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

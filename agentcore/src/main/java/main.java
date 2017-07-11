import agentcore.agentcommunication.AgentCommunicationImpl;
import agentcore.agentcommunication.IAgentCommunication;
import agentcore.agentcommunication.MCollectiveSolution;
import agentcore.agentcommunication.MSearchSolution;
import agentcore.agentfoundation.AgentDatabaseImpl;
import agentcore.database.dto.LocalDataDto;
import agentcore.inputdata.InputDataVerificationImpl;
import agentcore.inputdata.InputDataTableDesc;
import agentcore.inputdata.LocalDataTableDesc;
import agentcore.inputdata.TableColumn;
import org.springframework.jdbc.core.JdbcTemplate;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.OutputKeys;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import java.io.*;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Arrays;
import java.util.HashMap;

/**
 * Created on 17.02.2017 12:03
 *
 * @autor Nikita Gorodilov
 */
public class main {

    public static void main(String[] args) {
//        File xml = new File("data/input/td.xml");
        try {
//            Path path = Paths.get("data/input/td.xml");
//            byte[] data = Files.readAllBytes(path);
//            System.out.println(Arrays.toString(data));



            // content - то, что мы видим в файле при его просмотре
            FileOutputStream fos = new FileOutputStream("data/input/gg.xml");
            //fos.write(data);
            //fos.write(configurePackageDescriptionFileContent());
            fos.write(configureReportInfoFileContent());
            fos.close();
        } catch (IOException e) {
        }
    }

    private static byte[] configureReportInfoFileContent() {
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();

        try {
            DocumentBuilderFactory docFactory = DocumentBuilderFactory.newInstance();
            DocumentBuilder docBuilder = docFactory.newDocumentBuilder();

            // root element
            Document doc = docBuilder.newDocument();
            Element rootElement = doc.createElement("описаниеСведений");
            doc.appendChild(rootElement);

            // датаВремяОтправки element
            Element element = doc.createElement("датаВремяОтправки");
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
            LocalDateTime dateTime = LocalDateTime.now();
            dateTime = LocalDateTime.parse(dateTime.format(formatter), formatter);
            element.setTextContent(dateTime.toString());
            rootElement.appendChild(element);

            // регистрационныйНомерОрганизации element
            element = doc.createElement("регистрационныйНомерОрганизации");
            element.setTextContent("testрегистрационныйНомерОрганизации");
            rootElement.appendChild(element);

            // описаниеПачек elements
            Element infoElement = doc.createElement("описаниеПачек");
            rootElement.appendChild(infoElement);

            // пачка element
            Element pack = doc.createElement("пачка");
            infoElement.appendChild(pack);

            // идентификаторДокумента element
            element = doc.createElement("идентификаторДокумента");
            element.setTextContent("testидентификаторДокумента");
            pack.appendChild(element);

            // имяФайла element
            element = doc.createElement("имяФайла");
            element.setTextContent("testимяФайла");
            pack.appendChild(element);

            // описаниеПриложений element
            element = doc.createElement("описаниеПриложений");
            rootElement.appendChild(element);

            // write the content into xml file
            TransformerFactory transformerFactory = TransformerFactory.newInstance();
            Transformer transformer = transformerFactory.newTransformer();

            DOMSource source = new DOMSource(doc);
            StreamResult result = new StreamResult(outputStream);
            transformer.transform(source, result);

        } catch (ParserConfigurationException | TransformerException pce) {
        }

        return outputStream.toByteArray();
    }

    private static byte[] configurePackageDescriptionFileContent() {
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();

        try {
            DocumentBuilderFactory docFactory = DocumentBuilderFactory.newInstance();
            DocumentBuilder docBuilder = docFactory.newDocumentBuilder();

            // root element
            Document doc = docBuilder.newDocument();
            Element rootElement = doc.createElement("пакет");
            rootElement.setAttribute("версияФормата", "1.2");
            rootElement.setAttribute("идентификаторДокументооборота", "testидентификаторДокументооборота");
            rootElement.setAttribute("типДокументооборота", "СведенияПФР");
            rootElement.setAttribute("типДокументооборота", "сведения");
            doc.appendChild(rootElement);

            // СКЗИ element
            Element element = doc.createElement("СКЗИ");
            element.setAttribute("типСКЗИ", "Крипто-Про");
            rootElement.appendChild(element);

            // отправитель element
            element = doc.createElement("отправитель");
            element.setAttribute("идентификаторСубъекта", "testидентификаторСубъекта");
            element.setAttribute("типСубъекта", "АбонентСЭД");
            rootElement.appendChild(element);

            // получатель element
            element = doc.createElement("отправитель");
            element.setAttribute("идентификаторСубъекта", "testидентификаторСубъекта");
            element.setAttribute("типСубъекта", "ОрганПФР");
            rootElement.appendChild(element);

            // системаОтправителя element
            element = doc.createElement("отправитель");
            element.setAttribute("идентификаторСубъекта", "Русь Телеком");
            element.setAttribute("типСубъекта", "Провайдер");
            rootElement.appendChild(element);

			/*
			 * документ elements
			 */
            // отчёт
            Element reportDocElem = doc.createElement("документ");
            reportDocElem.setAttribute("сжат", "true");
            reportDocElem.setAttribute("идентификаторДокумента", "testидентификаторДокумента");
            reportDocElem.setAttribute("типДокумента", "пачкаИС");
            reportDocElem.setAttribute("типСодержимого", "xml");
            reportDocElem.setAttribute("зашифрован", "true"); // todo так ли это?

            // отчёт - содержимое element
            element = doc.createElement("содержимое");
            element.setAttribute("имяФайла", "testимяФайла");
            reportDocElem.appendChild(element);

            // отчёт - подпись element
            element = doc.createElement("подпись");
            element.setAttribute("имяФайла", "testимяФайла");
            element.setAttribute("роль", "руководитель");
            reportDocElem.appendChild(element);

            rootElement.appendChild(reportDocElem);


            // описание сведений
            reportDocElem = doc.createElement("документ");
            reportDocElem.setAttribute("сжат", "true");
            reportDocElem.setAttribute("идентификаторДокумента", "testидентификаторДокумента");
            reportDocElem.setAttribute("типДокумента", "описаниеСведений");
            reportDocElem.setAttribute("типСодержимого", "xml");
            reportDocElem.setAttribute("зашифрован", "true"); // todo так ли это?

            // отчёт - содержимое element
            element = doc.createElement("содержимое");
            element.setAttribute("имяФайла", "testимяФайла");
            reportDocElem.appendChild(element);

            // отчёт - подпись element
            element = doc.createElement("подпись");
            element.setAttribute("имяФайла", "testимяФайла");
            element.setAttribute("роль", "руководитель");
            reportDocElem.appendChild(element);

            rootElement.appendChild(reportDocElem);
			/*
			 * end документ elements
			 */

            // write the content into xml file
            TransformerFactory transformerFactory = TransformerFactory.newInstance();
            Transformer transformer = transformerFactory.newTransformer();
            transformer.setOutputProperty(OutputKeys.ENCODING, "windows-1251");

            DOMSource source = new DOMSource(doc);
            StreamResult result = new StreamResult(outputStream);
            transformer.transform(source, result);

        } catch (ParserConfigurationException | TransformerException pce) {
        }

        return outputStream.toByteArray();
    }
}

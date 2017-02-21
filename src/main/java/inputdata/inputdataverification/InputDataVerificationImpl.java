package inputdata.inputdataverification;

import com.google.common.collect.ImmutableList;
import database.dao.InputDataDao;
import database.dto.DtoEntityImpl;
import inputdata.inputdataverification.inputdata.InputDataTableDesc;
import inputdata.inputdataverification.inputdata.base.ATableDesc;
import inputdata.inputdataverification.inputdata.TableColumn;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.datasource.DriverManagerDataSource;
import inputdata.inputdataverification.base.InputDataVerification;
import org.w3c.dom.*;

import java.io.*;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Properties;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import org.w3c.dom.Document;

/**
 * Created by user on 16.02.2017.
 */
public class InputDataVerificationImpl implements InputDataVerification {

    @Override
    public JdbcTemplate getJdbcTemplate(String propPath) throws Exception {
        DriverManagerDataSource ds = new DriverManagerDataSource();

        Properties dbprop = new Properties();
        try {
            dbprop.load(new FileInputStream(propPath));

            String driverClassName = dbprop.getProperty("driverClassName");
            String url = dbprop.getProperty("url");
            String username = dbprop.getProperty("username");
            String password = dbprop.getProperty("password");

            ds.setDriverClassName(driverClassName);
            ds.setUrl(url);
            ds.setUsername(username);
            ds.setPassword(password);
        } catch (FileNotFoundException e) {
            throw new FileNotFoundException(e.toString());
        } catch (IOException e) {
            throw new IOException(e.toString());
        }

        return new JdbcTemplate(ds);
    }

    @Override
    public InputDataTableDesc getDatabaseTables(String descFilePath) throws Exception {
        InputDataTableDesc tableDesc = null;

        try {
            final File xmlFile = new File(descFilePath);
            DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
            DocumentBuilder db = dbf.newDocumentBuilder();
            Document doc = db.parse(xmlFile);

            doc.getDocumentElement().normalize();

            if (doc.hasChildNodes()) {
                // пока реализована лишь 1 таблица - для входных данных
                NodeList tablesNL = doc.getElementsByTagName("Table");
                // читаем первую таблицу в файле
                Node node = tablesNL.item(0);
                tableDesc = parseTableNode(node);
            }

        } catch (ParserConfigurationException | org.xml.sax.SAXException
                | IOException e) {
            throw new Exception(e.toString());
        }

        return tableDesc;
    }

    @Override
    public void testReadDbData(JdbcTemplate jdbcTemplate, ATableDesc tableDesc) throws Exception {
        if (tableDesc == null)
            throw new IOException();

        try {
            InputDataDao daoEntity = new InputDataDao(jdbcTemplate, tableDesc);
            DtoEntityImpl dtoEntity = daoEntity.getFirst(ATableDesc.ID_COLUMN_NAME);
            if (dtoEntity == null)
                throw new SQLException();
        } catch (SQLException e) {
            throw new Exception(e.toString());
        }
    }


    /**
     * Парсим таблицу из xml
     * <Table>
     *     ...
     * </Table>
     */
    private InputDataTableDesc parseTableNode(Node tableNode) {
        String tableName = "";
        int periodicityMs = -1;
        ImmutableList<TableColumn> columns = null;

        NodeList children = tableNode.getChildNodes();
        for (int i = 0; i < children.getLength(); i++) {
            Node node = children.item(i);
            if(node.getNodeType() == Node.ELEMENT_NODE) {
                if (node.getNodeName().equals("TableName")) {
                    tableName = node.getTextContent();
                }
                if (node.getNodeName().equals("PeriodicityMS")) {
                    periodicityMs = Integer.valueOf(node.getTextContent());
                }
                if (node.getNodeName().equals("ColumnDescription")) {
                    columns = parseColumns(node);
                }
            }
        }

        return new InputDataTableDesc(tableName, periodicityMs, columns);
    }

    /**
     * Парсим столбцы из таблицы в xml
     * <ColumnDescription>
     *     ...
     * </ColumnDescription>
     */
    private ImmutableList<TableColumn> parseColumns(Node columnsNode) {
        ArrayList<TableColumn> columns = new ArrayList<>();

        NodeList children = columnsNode.getChildNodes();
        for (int i = 0; i < children.getLength(); i++) {
            Node node = children.item(i);
            if(node.getNodeType() == Node.ELEMENT_NODE) {
                columns.add(parseColumnNode(node));
            }
        }

        return ImmutableList.copyOf(columns);
    }

    /**
     * Парсим столбец из столбцов в xml
     * <Column>
     *     ...
     * </Column>
     */
    private TableColumn parseColumnNode(Node columnNode) {
        String columnName = "";
        String columnType = "";

        NodeList children = columnNode.getChildNodes();
        for (int i = 0; i < children.getLength(); i++) {
            Node node = children.item(i);
            if (node.getNodeType() == Node.ELEMENT_NODE) {
                if (node.getNodeName().equals("ColumnName")) {
                    columnName = node.getTextContent();
                }
                if (node.getNodeName().equals("ColumnType")) {
                    columnType = node.getTextContent();
                }
            }
        }

        return new TableColumn(columnName, columnType);
    }

}

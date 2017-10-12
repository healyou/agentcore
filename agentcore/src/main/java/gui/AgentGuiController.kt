package gui

import db.core.sc.ServiceMessageSC
import db.core.sc.SystemAgentSC
import db.core.servicemessage.ServiceMessage
import db.core.servicemessage.ServiceMessageService
import db.core.systemagent.SystemAgent
import db.core.systemagent.SystemAgentService
import gui.table.AgentComboBoxRenderer
import javafx.beans.property.SimpleStringProperty
import javafx.beans.value.ObservableValue
import javafx.collections.FXCollections
import javafx.fxml.FXML
import javafx.scene.control.*
import javafx.scene.control.cell.PropertyValueFactory
import javafx.util.Callback
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Component
import java.text.SimpleDateFormat


/**
 * @author Nikita Gorodilov
 */
@Component
class AgentGuiController {

    @Autowired
    private lateinit var systemAgentService: SystemAgentService
    @Autowired
    private lateinit var serviceMessageService: ServiceMessageService

    private val messagesData = FXCollections.observableArrayList<ServiceMessage>()
    private val dateFormat = SimpleDateFormat("dd.MM.yyyy HH:mm:ss")

    @FXML
    lateinit var agentComboBox: ComboBox<SystemAgent>
    @FXML
    lateinit var messageTable: TableView<ServiceMessage>

    fun initialize() {
        configureMessageTable()
        configureAgentChoiceBox()
    }

    private fun configureMessageTable() {
        val idColumn = TableColumn<ServiceMessage, Long>("Идентификатор")
        idColumn.cellValueFactory = PropertyValueFactory("id")
        val senderColumn = TableColumn<ServiceMessage, Long>("Собственник")
        senderColumn.cellValueFactory = PropertyValueFactory("systemAgentId")
        val createDateColumn = TableColumn<ServiceMessage, String>("Дата создания")
        createDateColumn.cellValueFactory = Callback<TableColumn.CellDataFeatures<ServiceMessage, String>, ObservableValue<String>> { param ->
            if (param.value != null && param.value.createDate != null) {
                SimpleStringProperty(dateFormat.format(param.value.createDate))
            } else {
                SimpleStringProperty("");
            }
        }
        val useDateColumn = TableColumn<ServiceMessage, String>("Дата использования")
        useDateColumn.cellValueFactory = Callback<TableColumn.CellDataFeatures<ServiceMessage, String>, ObservableValue<String>> { param ->
            if (param.value != null && param.value.useDate != null) {
                SimpleStringProperty(dateFormat.format(param.value.useDate))
            } else {
                SimpleStringProperty("");
            }
        }
        val typeColumn = TableColumn<ServiceMessage, String>("Тип сообщения")
        typeColumn.cellValueFactory = Callback<TableColumn.CellDataFeatures<ServiceMessage, String>, ObservableValue<String>> { param ->
            if (param.value != null) {
                return@Callback SimpleStringProperty(param.value.messageType.name);
            } else {
                SimpleStringProperty("");
            }
        }
        val jsonColumn = TableColumn<ServiceMessage, String>("Тело сообщения")
        jsonColumn.cellValueFactory = PropertyValueFactory("jsonObject")

        messageTable.columns.addAll(idColumn, senderColumn, createDateColumn, useDateColumn, typeColumn, jsonColumn)
        messageTable.items = messagesData
    }

    private fun configureAgentChoiceBox() {
        agentComboBox.items = FXCollections.observableArrayList(getSystemAgents())
        agentComboBox.tooltip = Tooltip("Выберите агента")
        agentComboBox.buttonCell = AgentComboBoxRenderer()
        agentComboBox.cellFactory = Callback<ListView<SystemAgent>, ListCell<SystemAgent>> {
            AgentComboBoxRenderer()
        }
        agentComboBox.selectionModel.selectedIndexProperty().addListener { observable, oldValue, newValue ->
            messagesData.setAll(loadServiceMessages(agentComboBox.items[newValue.toInt()]))
        }
    }

    private fun getSystemAgents(): List<SystemAgent> {
        val sc = SystemAgentSC()
        sc.isSendAndGetMessages = true
        sc.isDeleted = false

        return systemAgentService.get(sc)
    }

    private fun loadServiceMessages(systemAgent: SystemAgent): List<ServiceMessage> {
        val sc = ServiceMessageSC()
        sc.systemAgentId = systemAgent.id

        return serviceMessageService.get(sc)
    }
}
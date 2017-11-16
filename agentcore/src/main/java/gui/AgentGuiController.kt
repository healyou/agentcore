package gui

import db.core.sc.ServiceMessageSC
import db.core.sc.SystemAgentSC
import db.core.servicemessage.ServiceMessage
import db.core.servicemessage.ServiceMessageService
import db.core.servicemessage.ServiceMessageType
import db.core.systemagent.SystemAgent
import db.core.systemagent.SystemAgentService
import dsl.RuntimeAgent
import gui.table.AgentComboBoxRenderer
import gui.table.CustomTableBuilder
import gui.table.columns.DateTimeTableColumn
import gui.table.columns.DictionaryTableColumn
import gui.table.columns.PropertyTableColumn
import javafx.collections.FXCollections
import javafx.fxml.FXML
import javafx.scene.control.*
import javafx.util.Callback
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Component
import java.awt.Image
import java.awt.image.BufferedImage
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
    @FXML
    lateinit var loadAgentsButton: Button

    fun initialize() {
        configureLoadAgentsButton()
        configureMessageTable()
        configureAgentChoiceBox()
    }

    private fun configureLoadAgentsButton() {
        loadAgentsButton.setOnAction {
            /* Для теста чисто 1 файл загрузим - а так надо сканировать все файлы в папке*/
            val runtimeAgent = object : RuntimeAgent("data/dsl/testagent.groovy") {

                override fun getSystemAgentService(): SystemAgentService = systemAgentService
                override fun getServiceMessageService(): ServiceMessageService = serviceMessageService
            }
        }
    }

    private fun configureMessageTable() {
        messageTable = CustomTableBuilder<ServiceMessage>()
                .addColumn(PropertyTableColumn("Идентификатор", "id"))
                .addColumn(PropertyTableColumn("Собственник", "systemAgentId"))
                .addColumn(DateTimeTableColumn("Дата создания", "createDate"))
                .addColumn(DateTimeTableColumn("Дата использования", "useDate"))
                .addColumn(DictionaryTableColumn<ServiceMessage, ServiceMessageType>("Тип сообщения", "messageType"))
                .addColumn(PropertyTableColumn("Тело сообщения", "jsonObject"))
                .withTable(messageTable)
                .withItems(messagesData)
                .build()
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
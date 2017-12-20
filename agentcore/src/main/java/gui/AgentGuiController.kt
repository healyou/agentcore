package gui

import db.base.Environment
import db.core.sc.ServiceMessageSC
import db.core.sc.SystemAgentSC
import db.core.servicemessage.ServiceMessage
import db.core.servicemessage.ServiceMessageService
import db.core.servicemessage.ServiceMessageType
import db.core.servicemessage.ServiceMessageTypeService
import db.core.systemagent.SystemAgent
import db.core.systemagent.SystemAgentService
import dsl.ThreadPoolRuntimeAgent
import dsl.loader.RuntimeAgentLoader
import dsl.objects.DslImage
import gui.table.AgentComboBoxRenderer
import gui.table.CustomTableBuilder
import gui.table.columns.DateTimeTableColumn
import gui.table.columns.DictionaryTableColumn
import gui.table.columns.PropertyTableColumn
import javafx.collections.FXCollections
import javafx.event.ActionEvent
import javafx.fxml.FXML
import javafx.scene.Node
import javafx.scene.control.*
import javafx.scene.layout.AnchorPane
import javafx.stage.FileChooser
import javafx.stage.Window
import javafx.util.Callback
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Component
import service.LoginService
import service.ServerTypeService
import java.awt.image.DataBufferByte
import java.io.File
import javax.imageio.ImageIO
import java.io.ByteArrayOutputStream
import java.awt.image.BufferedImage




/**
 * @author Nikita Gorodilov
 */
@Component
class AgentGuiController {

    companion object {
        val IMAGE_REGEX = "^(.*jpg$)|(.*jpeg$)|(.*png$)$"
    }

    @Autowired
    private lateinit var systemAgentService: SystemAgentService
    @Autowired
    private lateinit var serviceMessageService: ServiceMessageService
    @Autowired
    private lateinit var messageTypeService: ServiceMessageTypeService
    @Autowired
    private lateinit var serverTypeService: ServerTypeService
    @Autowired
    private lateinit var environment: Environment
    @Autowired
    private lateinit var loginService: LoginService

    private val agentLoader = RuntimeAgentLoader()
    private val messagesData = FXCollections.observableArrayList<ServiceMessage>()
    private val agentsData = FXCollections.observableArrayList<SystemAgent>()

    @FXML
    lateinit var agentComboBox: ComboBox<SystemAgent>
    @FXML
    lateinit var messageTable: TableView<ServiceMessage>
    @FXML
    lateinit var loadAgentsButton: Button
    @FXML
    lateinit var loadImageButton: Button

    fun initialize() {
        configureLoadImageButton()
        configureLoadAgentsButton()
        configureMessageTable()
        configureAgentChoiceBox()
    }

    private fun configureLoadImageButton() {
        loadImageButton.setOnAction { event ->
            val extFilter = FileChooser.ExtensionFilter("Image file(*.jpg, *.jpeg, *.png)", "*.jpg", "*.jpeg", "*.png")
            val fileChooser = FileChooser()
            fileChooser.title = "Загрузка изображения"
            fileChooser.initialDirectory = File(System.getProperty("user.home"));
            fileChooser.extensionFilters.add(extFilter)

            val imageFile = fileChooser.showOpenDialog(getWindow(event))
            if (IMAGE_REGEX.toRegex().matches(imageFile.name)) {
                val dslImage = configureDslImage(imageFile)
                val selectedAgent = getSelectedAgent()
                agentLoader.onLoadImage(selectedAgent, dslImage)
            }
        }
    }

    private fun configureDslImage(imageFile: File): DslImage {
        val bufferedImage = ImageIO.read(imageFile)
        val baos = ByteArrayOutputStream()
        ImageIO.write(bufferedImage, "jpg", baos)
        return DslImage(
                imageFile.name,
                baos.toByteArray()
        )
    }

    private fun getSelectedAgent(): SystemAgent {
        return agentComboBox.selectionModel.selectedItem
    }

    private fun configureLoadAgentsButton() {
        loadAgentsButton.setOnAction {
            loadAgents()
        }
    }

    private fun loadAgents() {
        agentLoader.stop()
        agentLoader.load { path ->
            return@load object : ThreadPoolRuntimeAgent(path) {

                override fun getSystemAgentService(): SystemAgentService = this@AgentGuiController.systemAgentService
                override fun getServiceMessageService(): ServiceMessageService = this@AgentGuiController.serviceMessageService
                override fun getServerTypeService(): ServerTypeService = this@AgentGuiController.serverTypeService
                override fun getLoginService(): LoginService = this@AgentGuiController.loginService
                override fun getEnvironment(): Environment = this@AgentGuiController.environment
                override fun getMessageTypeService(): ServiceMessageTypeService = this@AgentGuiController.messageTypeService
            }
        }
        agentLoader.start()
        updateUiData()
    }

    private fun configureMessageTable() {
        messageTable = CustomTableBuilder<ServiceMessage>()
                .addColumn(PropertyTableColumn("Идентификатор", "id"))
                .addColumn(PropertyTableColumn("Собственник", "systemAgentId"))
                .addColumn(DateTimeTableColumn("Дата создания", "createDate"))
                .addColumn(DateTimeTableColumn("Дата использования", "useDate"))
                .addColumn(DictionaryTableColumn<ServiceMessage, ServiceMessageType>("Локальный тип сообщения", "serviceMessageType"))
                .addColumn(PropertyTableColumn("Тип сообщения", "sendMessageType"))
                .withTable(messageTable)
                .withItems(messagesData)
                .build()
    }

    private fun configureAgentChoiceBox() {
        agentComboBox.items = agentsData
        agentComboBox.tooltip = Tooltip("Выберите агента")
        agentComboBox.buttonCell = AgentComboBoxRenderer()
        agentComboBox.cellFactory = Callback<ListView<SystemAgent>, ListCell<SystemAgent>> {
            AgentComboBoxRenderer()
        }
        agentComboBox.selectionModel.selectedIndexProperty().addListener { observable, oldValue, newValue ->
            if (newValue.toInt() >= 0) {
                loadImageButton.isDisable = false
                messagesData.setAll(loadServiceMessages(agentComboBox.items[newValue.toInt()]))
            }
        }
    }

    /**
     * Обновление данных интерфейса
     */
    private fun updateUiData() {
        agentComboBox.selectionModel.clearSelection()
        loadImageButton.isDisable = true
        messagesData.clear()
        agentsData.setAll(getSystemAgents())
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

    private fun getWindow(event: ActionEvent): Window {
        return (event.target as Node).scene.window
    }
}
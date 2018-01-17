package gui

import com.mycompany.db.base.Environment
import com.mycompany.db.core.file.FileContentLocator
import com.mycompany.db.core.sc.ServiceMessageSC
import com.mycompany.db.core.sc.SystemAgentSC
import com.mycompany.db.core.servicemessage.ServiceMessage
import com.mycompany.db.core.servicemessage.ServiceMessageService
import com.mycompany.db.core.servicemessage.ServiceMessageType
import com.mycompany.db.core.servicemessage.ServiceMessageTypeService
import com.mycompany.db.core.systemagent.SystemAgent
import com.mycompany.db.core.systemagent.SystemAgentEventHistoryService
import com.mycompany.db.core.systemagent.SystemAgentService
import com.mycompany.dsl.ThreadPoolRuntimeAgent
import com.mycompany.dsl.base.behavior.RuntimeAgentHistoryEventBehavior
import com.mycompany.dsl.base.behavior.RuntimeAgentUpdateUiEventHistoryBehavior
import com.mycompany.dsl.loader.RuntimeAgentLoader
import com.mycompany.dsl.objects.DslImage
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
import javafx.stage.FileChooser
import javafx.stage.Window
import javafx.util.Callback
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Component
import com.mycompany.service.LoginService
import com.mycompany.service.ServerTypeService
import com.mycompany.user.User
import java.io.ByteArrayOutputStream
import java.io.File
import javax.imageio.ImageIO


/**
 * @author Nikita Gorodilov
 */
@Component
class AgentGuiController {

    companion object {
        val IMAGE_REGEX = "^(.*jpg$)|(.*jpeg$)|(.*png$)$"
        /**
         * Количество записей истории поведения агента выбираемых из бд
         */
        val HISTORY_RECORDS_SIZE = 15
        /**
         * Количество загружаемых агентов
         */
        val AGENT_RECORDS_SIZE = 10
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
    @Autowired
    private lateinit var historyService: SystemAgentEventHistoryService
    @Autowired
    private lateinit var fileContentLocator: FileContentLocator
    @Autowired
    private lateinit var javaFxSession: AuthenticatedJavaFxSession

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
    @FXML
    lateinit var eventHistoryUpdateButton: Button
    @FXML
    lateinit var eventHistoryTextArea: TextArea

    fun initialize() {
        configureLoadImageButton()
        configureLoadAgentsButton()
        configureEventHistoryUpdateButton()
        configureMessageTable()
        configureAgentChoiceBox()
    }

    private fun configureEventHistoryUpdateButton() {
        eventHistoryUpdateButton.setOnAction {
            updateEventHistoryText()
        }
    }

    private fun configureLoadImageButton() {
        loadImageButton.setOnAction { event ->
            val extFilter = FileChooser.ExtensionFilter("Image file(*.jpg, *.jpeg, *.png)", "*.jpg", "*.jpeg", "*.png")
            val fileChooser = FileChooser()
            fileChooser.title = "Загрузка изображения"
            fileChooser.initialDirectory = File(System.getProperty("user.home"));
            fileChooser.extensionFilters.add(extFilter)

            val imageFile: File? = fileChooser.showOpenDialog(getWindow(event))
            if (imageFile != null && IMAGE_REGEX.toRegex().matches(imageFile.name)) {
                val dslImage = configureDslImage(imageFile)
                val selectedAgent = getSelectedAgent()
                agentLoader.onLoadImage(selectedAgent!!, dslImage)
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

    private fun getSelectedAgent(): SystemAgent? {
        return agentComboBox.selectionModel.selectedItem
    }

    private fun configureLoadAgentsButton() {
        loadAgentsButton.setOnAction {
            loadAgents()
        }
    }

    private fun loadAgents() {
        agentLoader.stop()
        agentLoader.load { dslFile ->
            val runtimeAgent = object : ThreadPoolRuntimeAgent(dslFile) {

                override fun getSystemAgentService(): SystemAgentService = this@AgentGuiController.systemAgentService
                override fun getServiceMessageService(): ServiceMessageService = this@AgentGuiController.serviceMessageService
                override fun getServerTypeService(): ServerTypeService = this@AgentGuiController.serverTypeService
                override fun getLoginService(): LoginService = this@AgentGuiController.loginService
                override fun getEnvironment(): Environment = this@AgentGuiController.environment
                override fun getMessageTypeService(): ServiceMessageTypeService = this@AgentGuiController.messageTypeService
                override fun getFileContentLocator(): FileContentLocator = this@AgentGuiController.fileContentLocator
                override fun getOwner(): User = this@AgentGuiController.javaFxSession.principal.user
                override fun getCreateUser(): User = this@AgentGuiController.javaFxSession.principal.user
            }
            runtimeAgent.add(RuntimeAgentHistoryEventBehavior(historyService))
            runtimeAgent.add(RuntimeAgentUpdateUiEventHistoryBehavior(historyService, { systemAgent, message ->
                val selectedAgent = getSelectedAgent()
                if (selectedAgent != null) {
                    if (selectedAgent.id == systemAgent.id) {
                        eventHistoryTextArea.text += message
                    }
                }
            }))
            return@load runtimeAgent
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
        agentComboBox.selectionModel.selectedIndexProperty().addListener { _, _, newValue ->
            if (newValue.toInt() >= 0) {
                val systemAgent = agentComboBox.items[newValue.toInt()]
                eventHistoryTextArea.text = getHistoryAsTextAreaString(systemAgent, HISTORY_RECORDS_SIZE)
                loadImageButton.isDisable = false
                messagesData.setAll(loadServiceMessages(systemAgent))
            }
        }
    }

    /**
     * Обновление истории поведения агента
     */
    private fun updateEventHistoryText() {
        val selectedAgent = getSelectedAgent()
        if (selectedAgent != null) {
            eventHistoryTextArea.text = getHistoryAsTextAreaString(selectedAgent, HISTORY_RECORDS_SIZE)
        }
    }

    private fun getHistoryAsTextAreaString(systemAgent: SystemAgent, size: Int): String {
        val historyText = StringBuilder()
        historyService.getLastNumberItems(systemAgent.id!!, size.toLong()).asReversed().forEach {
            historyText.append(it.createDate).append(' ').append(it.message).append("\n")
        }
        return historyText.toString()
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
        return systemAgentService.get(AGENT_RECORDS_SIZE.toLong(), javaFxSession.principal.user.id!!)
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
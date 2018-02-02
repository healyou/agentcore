package com.mycompany.desktopapp.page

import com.mycompany.db.base.Environment
import com.mycompany.db.core.file.ByteArrayFileContent
import com.mycompany.db.core.file.FileContent
import com.mycompany.db.core.file.FileContentLocator
import com.mycompany.db.core.file.FileContentRef
import com.mycompany.db.core.file.dslfile.DslFileAttachment
import com.mycompany.db.core.sc.ServiceMessageSC
import com.mycompany.db.core.servicemessage.ServiceMessage
import com.mycompany.db.core.servicemessage.ServiceMessageService
import com.mycompany.db.core.servicemessage.ServiceMessageType
import com.mycompany.db.core.servicemessage.ServiceMessageTypeService
import com.mycompany.db.core.systemagent.SystemAgent
import com.mycompany.db.core.systemagent.SystemAgentEventHistoryService
import com.mycompany.db.core.systemagent.SystemAgentService
import com.mycompany.desktopapp.security.AuthenticatedJavaFxSession
import com.mycompany.dsl.ThreadPoolRuntimeAgent
import com.mycompany.dsl.base.behavior.RuntimeAgentHistoryEventBehavior
import com.mycompany.dsl.base.behavior.RuntimeAgentUpdateUiEventHistoryBehavior
import com.mycompany.dsl.loader.IRuntimeAgentWorkControl
import com.mycompany.service.LoginService
import com.mycompany.service.ServerTypeService
import com.mycompany.user.User
import com.mycompany.desktopapp.table.AgentComboBoxRenderer
import com.mycompany.desktopapp.table.CustomTableBuilder
import com.mycompany.desktopapp.table.columns.DateTimeTableColumn
import com.mycompany.desktopapp.table.columns.DictionaryTableColumn
import com.mycompany.desktopapp.table.columns.PropertyTableColumn
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
import java.io.File
import java.nio.file.Files


/**
 * @author Nikita Gorodilov
 */
@Component
class AgentGuiController {

    companion object {
        private val IMAGE_REGEX = "^(.*jpg$)|(.*jpeg$)|(.*png$)$"
        /**
         * Количество записей истории поведения агента выбираемых из бд
         */
        private val HISTORY_RECORDS_SIZE = 15
        /**
         * Количество загружаемых агентов
         */
        private val AGENT_RECORDS_SIZE = 10
        /**
         * Путь до папки с конфигурациями агентов
         */
        private val AGENT_DSL_PATH = "data/dsl/"
        /**
         * Грузим только groovy файлы
         */
        private val GROOVY_FILE_REGEX = "^.*.groovy$"
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
    @Autowired
    private lateinit var agentLoader: IRuntimeAgentWorkControl

    private val messagesData = FXCollections.observableArrayList<ServiceMessage>()
    private val agentsData = FXCollections.observableArrayList<SystemAgent>()

    @FXML
    lateinit var agentComboBox: ComboBox<SystemAgent>
    @FXML
    lateinit var messageTable: TableView<ServiceMessage>
    @FXML
    lateinit var loadAgentsButton: Button
    @FXML
    lateinit var loadAgentButton: Button
    @FXML
    lateinit var eventHistoryUpdateButton: Button
    @FXML
    lateinit var eventHistoryTextArea: TextArea

    fun initialize() {
        configureLoadAgentButton()
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

    private fun getSelectedAgent(): SystemAgent? {
        return agentComboBox.selectionModel.selectedItem
    }

    private fun configureLoadAgentButton() {
        loadAgentButton.setOnAction { event ->
            val extFilter = FileChooser.ExtensionFilter("Конфигурационный файл агента (*.groovy)", "*.groovy")
            val fileChooser = FileChooser()
            fileChooser.title = "Загрузка конфигурации агента"
            fileChooser.initialDirectory = File(System.getProperty("user.home"));
            fileChooser.extensionFilters.add(extFilter)

            val loadFile: File? = fileChooser.showOpenDialog(getWindow(event))
            if (loadFile != null && GROOVY_FILE_REGEX.toRegex().matches(loadFile.name)) {
                try {
                    val agent = createAgent(createDslFile(loadFile.path, loadFile.name)).getSystemAgent()
                    agentsData.add(agent)
                    agentLoader.start(agent)
                } catch (e: Exception) {
                    throw RuntimeException("Невозможно создать агента по конфигурационному файлу - ${loadFile.path}")
                }
            }
        }
    }

    private fun configureLoadAgentsButton() {
        loadAgentsButton.setOnAction {
            loadAgents()
        }
    }

    private fun loadAgents() {
        agentLoader.stop()
        getUserAgents().forEach {
            agentLoader.start(it)
        }
        updateUiData()
    }

    /**
     * @return агенты текущего пользователя
     */
    private fun getUserAgents(): List<SystemAgent> {
        return configureAgents().map { it.getSystemAgent() }.toList()
    }

    private fun createAgent(dslFile: DslFileAttachment): ThreadPoolRuntimeAgent {
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
        return runtimeAgent
    }

    private fun configureAgents(): List<ThreadPoolRuntimeAgent> {
        val agents = arrayListOf<ThreadPoolRuntimeAgent>()
        File(AGENT_DSL_PATH).walk().forEach {
            if (GROOVY_FILE_REGEX.toRegex().matches(it.name)) {
                agents.add(createAgent(createDslFile(it.path, it.name)))
            }
        }
        return agents
    }

    private fun createDslFile(path: String, filename: String): DslFileAttachment {
        val file = File(path)
        val content = Files.readAllBytes(file.toPath())
        return DslFileAttachment(
                filename,
                object : FileContentRef {
                    @Override
                    override fun getContent(visitor: FileContentLocator): FileContent {
                        return ByteArrayFileContent(content)
                    }
                    @Override
                    override fun getName(): String {
                        return filename
                    }
                },
                content.size.toLong()
        )
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
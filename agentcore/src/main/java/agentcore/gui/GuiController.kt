package agentcore.gui

import agentcore.agentfoundation.*
import agentcore.database.dao.InputDataDao
import agentcore.inputdata.InputDataVerificationImpl
import javafx.application.Platform
import javafx.fxml.FXML
import javafx.scene.control.Button
import javafx.scene.control.Label
import javafx.scene.control.TextArea
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.beans.factory.annotation.Value
import org.springframework.context.annotation.Scope
import org.springframework.stereotype.Component
import java.util.*

/**
 * @author Nikita Gorodilov
 */
@Component
@Scope("prototype")
open class GuiController: Observer {

    @FXML
    lateinit var startButton: Button
    @FXML
    lateinit var stopButton: Button
    @FXML
    lateinit var statusLabel: Label
    @FXML
    lateinit var logTextArea: TextArea

    private var agent: Agent? = null

    @Value("#{properties['testKey2']}")
    lateinit var aValueForKey2: String
    @Autowired
    lateinit var testKey: String

    // todo не видит clips dll в папке libs

    fun initialize() {
        startButton.setOnAction {
            agent?.start()

            startButton.isDisable = true
            stopButton.isDisable = false
            statusLabel.text = "Агент работает"
            logTextArea.text += "\nНачало работы агента"
        }
        stopButton.setOnAction {
            agent?.stop()

            startButton.isDisable = false
            stopButton.isDisable = true
            statusLabel.text = "Агент не работает"
            logTextArea.text += "\nКонец работы агента"
        }

        agent = Agent(this)

        println(aValueForKey2)
        println(testKey)
    }

    override fun update(o: Observable?, arg: Any?) {
        if (o is IAgentBrain)
            onAgentUpdate(arg)
    }

    private fun onAgentUpdate(arg: Any?) {
        Platform.runLater {
            if (arg !is AgentObserverArg)
                return@runLater

            when (arg.type) {
                ObserverArgType.OUTPUT_DATA -> {
                    logTextArea.appendText("\nвыходные данные - " + arg.arg)
                }
                ObserverArgType.MESSAGE -> {
                    logTextArea.appendText("\n" + arg.arg)
                }
                ObserverArgType.DEFAUL_VALUE -> {
                    logTextArea.appendText("\nDEFAUL message")
                }
            }
        }
    }
}
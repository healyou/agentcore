package agentcore.gui

import agentcore.agentfoundation.*
import javafx.application.Platform
import javafx.fxml.FXML
import javafx.scene.control.Button
import javafx.scene.control.Label
import javafx.scene.control.TextArea
import org.springframework.jdbc.datasource.DriverManagerDataSource
import java.io.BufferedReader
import java.io.FileReader
import java.sql.SQLException
import java.util.*

/**
 * Created on 27.03.2017 18:58
 * @autor Nikita Gorodilov
 */
class GuiController: Observer {

    @FXML
    lateinit var startButton: Button
    @FXML
    lateinit var stopButton: Button
    @FXML
    lateinit var statusLabel: Label
    @FXML
    lateinit var logTextArea: TextArea

    private var agent: Agent? = null

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
                else -> {
                    logTextArea.appendText("\nnot message")
                }
            }
        }
    }
}
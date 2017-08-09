package agentcore.gui

import agentcore.agentfoundation.*
import javafx.fxml.FXML
import javafx.scene.control.Button
import javafx.scene.control.Label
import javafx.scene.control.TextArea
import kotlinx.coroutines.experimental.CommonPool
import kotlinx.coroutines.experimental.run
import kotlinx.coroutines.experimental.launch
import org.springframework.stereotype.Component
import java.util.*
import kotlin.concurrent.thread
import kotlinx.coroutines.experimental.javafx.JavaFx as UI

/**
 * @author Nikita Gorodilov
 */
@Component
open class GuiController: Observer {

    @FXML
    lateinit var startButton: Button
    @FXML
    lateinit var stopButton: Button
    @FXML
    lateinit var statusLabel: Label
    @FXML
    lateinit var logTextArea: TextArea

    lateinit var agent: Agent

    // todo не видит clips dll в папке libs

    fun initialize() {
        /* onClick - максимум одно действие одновременно. выполняется в ui потоке */
        startButton.onClick {

            /* run - выполнение без блокировки ui потока - тоесть новый поток */
            run(CommonPool) {
                agent.start()

                /* run - выполнение без блокировки ui потока -> выполняется в ui потоке */
                launch(UI) {
                    startButton.isDisable = true
                    stopButton.isDisable = false
                    statusLabel.text = "Агент работает"
                    logTextArea.text += "\nНачало работы агента"
                }
            }
        }
        stopButton.onClick {
            run(CommonPool) {
                agent.stop()

                launch(UI) {
                    startButton.isDisable = false
                    stopButton.isDisable = true
                    statusLabel.text = "Агент не работает"
                    logTextArea.text += "\nКонец работы агента"
                }
            }
        }

        agent = Agent(this)
    }

    override fun update(o: Observable?, arg: Any?) {
        if (o is IAgentBrain)
            onAgentUpdate(arg)
    }

    private fun onAgentUpdate(arg: Any?) {
        if (arg !is AgentObserverArg)
            return

        launch(UI) {
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
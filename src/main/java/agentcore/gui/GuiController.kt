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

    // todo maven после install не работает с кастомными либами(надо их как то вручную туда добавлять)

    // todo не видит clips dll в папке libs

    // todo переделать входные данные с double на int

    fun initialize() {
        // создание и инициализация inputdb
        updateInputDbData()
        updateLocalDbData()

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

    private fun updateInputDbData() {
        val ds = DriverManagerDataSource()

        val dbprop = Properties()
        dbprop.load(FileReader("data/input/db.properties"))

        val driverClassName = dbprop.getProperty("driverClassName")
        val url = dbprop.getProperty("url")

        ds.setDriverClassName(driverClassName)
        ds.url = url

        val st = ds.connection.createStatement()

        // create table
        var filePath = "data/input/initdb/CreateInputDb.sql"
        var br = BufferedReader(FileReader(filePath))
        val sql = StringBuilder()
        while (br.ready())
            sql.append(br.readLine())
        st.execute(sql.toString())

        // clear table
        sql.setLength(0)
        filePath = "data/input/initdb/ClearTableData.sql"
        br = BufferedReader(FileReader(filePath))
        while (br.ready())
            sql.append(br.readLine())
        st.execute(sql.toString())

        // setup data
        sql.setLength(0)
        filePath = "data/input/initdb/InitInputDbData.sql"
        br = BufferedReader(FileReader(filePath))
        while (br.ready())
            sql.append(br.readLine())
        st.execute(sql.toString())

        st.close()
    }

    private fun updateLocalDbData() {
        val ds = DriverManagerDataSource()
        ds.setDriverClassName("org.sqlite.JDBC")
        ds.url = "jdbc:sqlite:data/localdb/localdb.s3db"

        val st = ds.connection.createStatement()
        try {
            st.execute("DELETE FROM " + AgentDatabaseImpl.TABLE_NAME)
        } catch (e: SQLException) {
        } finally {
            st.close()
        }
    }
}
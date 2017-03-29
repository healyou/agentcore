package gui

import agentfoundation.*
import inputdata.InputDataVerificationImpl
import javafx.fxml.FXML
import javafx.scene.control.Button
import javafx.scene.control.Label
import javafx.scene.control.TextArea
import org.springframework.jdbc.datasource.DriverManagerDataSource
import java.io.FileReader
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

    fun initialize() {
        // при первом запуске без бд, надо удалить
        // st.execute("drop table intsedent") and st.execute("DELETE FROM " + AgentDatabaseImpl.TABLE_NAME)
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
        if (arg !is AgentObserverArg)
            return

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

    private fun updateInputDbData() {
        val ds = DriverManagerDataSource()

        val dbprop = Properties()
        dbprop.load(FileReader("data/input/db.properties"))

        val driverClassName = dbprop.getProperty("driverClassName")
        val url = dbprop.getProperty("url")

        ds.setDriverClassName(driverClassName)
        ds.url = url

        val st = ds.connection.createStatement()
        st.execute("drop table intsedent")
        st.execute("CREATE TABLE if not exists intsedent (id INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL,shortinfo TEXT,info TEXT);")
        for (i in 1..100)
            st.execute("insert into intsedent (shortinfo, info) values ('$i', '$i')")
        st.close()
    }

    private fun updateLocalDbData() {
        val ds = DriverManagerDataSource()
        ds.setDriverClassName("org.sqlite.JDBC")
        ds.url = "jdbc:sqlite:data/localdb/localdb.s3db"

        val st = ds.connection.createStatement()
        st.execute("DELETE FROM " + AgentDatabaseImpl.TABLE_NAME)
        st.close()
    }
}
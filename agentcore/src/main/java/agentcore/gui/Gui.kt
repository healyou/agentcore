package agentcore.gui

import javafx.application.Application
import javafx.scene.Scene
import javafx.stage.Stage
import org.springframework.context.support.ClassPathXmlApplicationContext

/**
 * @author Nikita Gorodilov
 */
class Gui : Application() {

    override fun start(primaryStage: Stage?) {
        val applicationContext = ClassPathXmlApplicationContext("context.xml")
        val loader = applicationContext.getBean(SpringFxmlLoader::class.java)
        val root = loader.load(javaClass.getResourceAsStream("gui.fxml"))
        primaryStage?.title = "Agent core"
        primaryStage?.scene = Scene(root, 800.0, 600.0)
        primaryStage?.show()
    }

    companion object {
        @JvmStatic
        fun main(args: Array<String>) {
            launch(Gui::class.java)
        }
    }
}
package agentcore.gui

import javafx.application.Application
import javafx.scene.Scene
import javafx.fxml.FXMLLoader.load
import javafx.scene.Parent
import javafx.stage.Stage

/**
 * Created on 27.03.2017 18:58
 * @autor Nikita Gorodilov
 */
class Gui : Application() {

    override fun start(primaryStage: Stage?) {
        val root = load<Parent?>(Gui::class.java.getResource("gui.fxml"))
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
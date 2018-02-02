package com.mycompany.desktopapp

import com.mycompany.desktopapp.page.LoginGuiController
import javafx.application.Application
import javafx.scene.Scene
import javafx.stage.Stage
import org.springframework.context.annotation.AnnotationConfigApplicationContext
import java.util.concurrent.ScheduledExecutorService

/**
 * Отображения списка сообщений для выбраноого агента
 *
 * @author Nikita Gorodilov
 */
class AgentGui : Application() {

    companion object {
        val applicationContext = AnnotationConfigApplicationContext(
                ApplicationConfig::class.java, JdbcConfig::class.java)

        @JvmStatic
        fun main(args: Array<String>) {
            launch(AgentGui::class.java)
        }
    }

    override fun start(primaryStage: Stage?) {
        val loader = applicationContext.getBean(AgentSpringFxmlLoader::class.java)
        val root = loader.load(LoginGuiController::class.java.getResourceAsStream("login.fxml"))
        primaryStage?.title = "Авторизация"
        primaryStage?.scene = Scene(root, 800.0, 600.0)
        primaryStage?.show()
    }

    override fun stop() {
        super.stop()
        stopExecutorService()
    }

    private fun stopExecutorService() {
        val executor = applicationContext.getBean(ScheduledExecutorService::class.java)
        executor.shutdown()
    }
}
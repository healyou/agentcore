package gui

import dsl.ImagesFunctions
import javafx.application.Application
import javafx.scene.Scene
import javafx.stage.Stage
import org.springframework.context.support.ClassPathXmlApplicationContext
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor
import org.springframework.scheduling.concurrent.ThreadPoolTaskScheduler

/**
 * Отображения списка сообщений для выбраноого агента
 *
 * @author Nikita Gorodilov
 */
class AgentGui : Application() {

    private lateinit var applicationContext: ClassPathXmlApplicationContext;

    override fun start(primaryStage: Stage?) {
        applicationContext = ClassPathXmlApplicationContext("context.xml")
        val loader = applicationContext.getBean(AgentSpringFxmlLoader::class.java)
        val root = loader.load(javaClass.getResourceAsStream("gui.fxml"))
        primaryStage?.title = "Сообщения агента"
        primaryStage?.scene = Scene(root, 800.0, 600.0)
        primaryStage?.show()
    }

    override fun stop() {
        super.stop()
        val executor = applicationContext.getBean(ThreadPoolTaskExecutor::class.java)
        val scheduler = applicationContext.getBean(ThreadPoolTaskScheduler::class.java)
        scheduler.shutdown()
        executor.shutdown()
    }

    companion object {
        @JvmStatic
        fun main(args: Array<String>) {
            ImagesFunctions.testImageFun1()
            ImagesFunctions.testImageFun2()
            ImagesFunctions.testImageFun3()
            launch(AgentGui::class.java)
        }
    }
}
package com.mycompany.desktopapp

import javafx.fxml.FXMLLoader
import javafx.scene.Parent
import org.springframework.beans.BeansException
import org.springframework.context.ApplicationContext
import org.springframework.context.ApplicationContextAware
import org.springframework.stereotype.Component
import java.io.IOException
import java.io.InputStream
import javafx.util.Callback

/**
 * @author Nikita Gorodilov
 */
@Component
open class AgentSpringFxmlLoader : ApplicationContextAware {

    private var applicationContext: ApplicationContext? = null

    fun load(inputStream: InputStream): Parent {
        val loader = FXMLLoader()
        loader.controllerFactory = Callback<Class<*>, Any> { applicationContext!!.getBean(it) }

        try {
            return loader.load<Parent>(inputStream)
        } catch (e: IOException) {
            throw RuntimeException(e)
        }
    }

    @Throws(BeansException::class)
    override fun setApplicationContext(applicationContext: ApplicationContext) {
        this.applicationContext = applicationContext
    }
}

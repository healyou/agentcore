package dsl

import db.core.servicemessage.ServiceMessage
import db.core.servicemessage.ServiceMessageService
import db.core.systemagent.SystemAgentService
import org.easymock.EasyMock
import org.easymock.EasyMock.mock
import org.junit.Assert
import org.junit.Before
import org.junit.Test
import java.awt.Image
import java.awt.image.BufferedImage

/**
 * @author Nikita Gorodilov
 */
class RuntimeAgentTest : Assert() {

    lateinit var runtimeAgentService: RuntimeAgentService

    @Before
    fun setup() {
//        runtimeAgentService = object : RuntimeAgentService() {
//        }
//        runtimeAgentService.loadExecuteRules(javaClass.getResource("testagentdsl.groovy").toURI().path)
    }

    /* Надо тестить именно работу dsl как таковой тут */

    /* Проходит вызов всех функций из dsl */
    @Test
    fun test() {
//        runtimeAgentService.applyOnLoadImage(mock(BufferedImage::class.java))
//        runtimeAgentService.applyOnEndImageTask(mock(BufferedImage::class.java))
//        runtimeAgentService.applyOnGetMessage(mock(ServiceMessage::class.java))
    }

    // TODO тестовый класс, наследованный от RuntimeAgentService для тестирования dsl
}
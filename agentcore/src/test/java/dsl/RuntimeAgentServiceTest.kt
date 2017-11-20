package dsl

import db.core.servicemessage.ServiceMessage
import org.easymock.EasyMock.mock
import org.junit.Assert
import org.junit.Before
import org.junit.Test
import java.awt.image.BufferedImage

/**
 * @author Nikita Gorodilov
 */
class RuntimeAgentServiceTest : Assert() {

    lateinit private var runtimeAgentService: RuntimeAgentService

    @Before
    fun setup() {
        runtimeAgentService = RuntimeAgentService()
    }

    /* Без загрузки функций выходит ошибка при работе */
    @Test
    fun testErrorApplyNotLoadFunctions() {
        assertTrue(runExpectedFunctionError { runtimeAgentService.applyInit() })
        assertTrue(runExpectedFunctionError { runtimeAgentService.applyOnLoadImage(mock(BufferedImage::class.java)) })
        assertTrue(runExpectedFunctionError { runtimeAgentService.applyOnEndImageTask(mock(BufferedImage::class.java)) })
        assertTrue(runExpectedFunctionError { runtimeAgentService.applyOnGetMessage(mock(ServiceMessage::class.java)) })
    }

    /* Если в dsl не предоставлены все функции - выходит ошибка */
    @Test
    fun testLoadErrorsDsl() {
        assertTrue(runExpectedFunctionError { runtimeAgentService.loadExecuteRules(javaClass.getResource("noinitagentdsl.groovy").toURI().path) })
        assertFalse(runExpectedFunctionError { runtimeAgentService.loadExecuteRules(javaClass.getResource("testagentdsl.groovy").toURI().path) })
    }

    /* Тест загрузки данных из dsl */
    @Test
    fun testApplyInit() {
        runtimeAgentService.loadExecuteRules(javaClass.getResource("testagentdsl.groovy").toURI().path)
        runtimeAgentService.applyInit()

        /* Данные из файла testagentdsl.groovy */
        assertEquals(runtimeAgentService.agentName, "name")
        assertEquals(runtimeAgentService.agentType, "worker")
        assertEquals(runtimeAgentService.masId, "masId")
    }

    private fun runExpectedFunctionError(func: () -> Unit): Boolean {
        return try {
            func.invoke()
            false
        } catch (ignored: Exception) {
            true
        }
    }
}
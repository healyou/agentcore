package dsl

import db.core.servicemessage.ServiceMessage
import groovy.lang.MetaClass
import org.easymock.EasyMock.mock
import org.junit.Assert
import org.junit.Before
import org.junit.Test
import java.awt.image.BufferedImage
import kotlin.test.assertTrue

/**
 * @author Nikita Gorodilov
 */
class RuntimeAgentServiceTest : Assert() {

    /* Без загрузки функций выходит ошибка при работе */
    @Test
    fun testErrorApplyNotLoadFunctions() {
        val runtimeAgentService = RuntimeAgentService()

        assertTrue(runExpectedFunctionError { runtimeAgentService.applyInit() })
        assertTrue(runExpectedFunctionError { runtimeAgentService.applyOnLoadImage(mock(BufferedImage::class.java)) })
        assertTrue(runExpectedFunctionError { runtimeAgentService.applyOnEndImageTask(mock(BufferedImage::class.java)) })
        assertTrue(runExpectedFunctionError { runtimeAgentService.applyOnGetMessage(mock(ServiceMessage::class.java)) })
    }

    /* Если в dsl не предоставлены все функции - выходит ошибка */
    @Test
    fun testLoadErrorsDsl() {
        val runtimeAgentService = RuntimeAgentService()

        assertTrue(runExpectedFunctionError { runtimeAgentService.loadExecuteRules(javaClass.getResource("noinitagentdsl.groovy").toURI().path) })
        assertFalse(runExpectedFunctionError { runtimeAgentService.loadExecuteRules(javaClass.getResource("testagentdsl.groovy").toURI().path) })
    }

    /* Тест загрузки данных из dsl */
    @Test
    fun testApplyInit() {
        val runtimeAgentService = RuntimeAgentService()

        runtimeAgentService.loadExecuteRules(javaClass.getResource("testagentdsl.groovy").toURI().path)
        runtimeAgentService.applyInit()

        /* Данные из файла testagentdsl.groovy */
        assertEquals(runtimeAgentService.agentName, "name")
        assertEquals(runtimeAgentService.agentType, "worker")
        assertEquals(runtimeAgentService.masId, "masId")
    }

    /* Проходят все вызовы функций из dsl */
    @Test
    fun testExecuteDslFunction() {
        val runtimeAgentService = TestRuntimeAgentServiceClass()

        runtimeAgentService.loadExecuteRules(javaClass.getResource("testagentdsl.groovy").toURI().path)
        runtimeAgentService.applyInit()
        runtimeAgentService.applyOnLoadImage(mock(BufferedImage::class.java))
        runtimeAgentService.applyOnEndImageTask(mock(BufferedImage::class.java))
        runtimeAgentService.applyOnGetMessage(mock(ServiceMessage::class.java))

        assertTrue(runtimeAgentService.isExecuteInit as Boolean)
        assertTrue(runtimeAgentService.isExecuteOnGetMessages as Boolean)
        assertTrue(runtimeAgentService.isExecuteOnEndImageTask as Boolean)
        assertTrue(runtimeAgentService.isExecuteOnLoadImage as Boolean)
    }

    /**
     * Тестирование выполнения блоков dsl
     */

    /* Вызов функций в anyOf, allOf, condition блоках */
    @Test
    fun testExecuteConditionBlock() {
        /* Проверить работу dsl блоков */
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
package dsl

import org.junit.Assert
import org.junit.Before
import org.junit.Test

/**
 * @author Nikita Gorodilov
 */
class RuntimeAgentServiceTest : Assert() {

    lateinit private var runtimeAgentService: RuntimeAgentService

    @Before
    fun setup() {
        runtimeAgentService = RuntimeAgentService()
    }

    /* Без загрузки функций ничего вызвать будет нельзя */
    @Test
    fun testErrorApplyNotLoadFunctions() {
        assertTrue(runExpectedFunctionError { runtimeAgentService.applyInit() })
        //assertTrue(runExpectedFunctionError { runtimeAgentService.applyOnLoadImage(null) })
        //assertTrue(runExpectedFunctionError { runtimeAgentService.applyOnEndImageTask(null) })
        //assertTrue(runExpectedFunctionError { runtimeAgentService.applyOnGetMessage(null) })
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
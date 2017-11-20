package dsl

import db.core.servicemessage.ServiceMessage
import org.easymock.EasyMock.mock
import org.junit.Assert
import org.junit.Test
import java.awt.image.BufferedImage

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
        assertTrue(runtimeAgentService.isExecuteTestOnGetMessages as Boolean)
        assertTrue(runtimeAgentService.isExecuteTestOnEndImageTask as Boolean)
        assertTrue(runtimeAgentService.isExecuteTestOnLoadImage as Boolean)
    }

    /**
     * Тестирование выполнения блоков dsl
     */

    /* Тестирование работы init блока */
    @Test
    fun testDslInitBlock() {
        val runtimeAgentService = TestRuntimeAgentServiceClass()
        val type = "worker"
        val name = "name"
        val masId = "masId"

        runtimeAgentService.testLoadExecuteRules(
                """
                    init = {
                        type = "$type"
                        name = "$name"
                        masId = "$masId"
                    }
                    onGetMessage = {}
                    onLoadImage = {}
                    onEndImageTask = {}
                """
        )
        runtimeAgentService.applyInit()

        assertTrue(runtimeAgentService.isExecuteInit as Boolean)
        assertEquals(type, runtimeAgentService.agentType)
        assertEquals(name, runtimeAgentService.agentName)
        assertEquals(masId, runtimeAgentService.masId)
    }

    // TODO - junit5

    /* Проверка выполнения функции в блоках allOf, anyOf, condition and other */
    @Test
    fun testDslExecuteConditionBlock() {
        testDslConditionBlocksArray.forEach {
            val runtimeAgentService = TestRuntimeAgentServiceClass()

            runtimeAgentService.testLoadExecuteRules(it.rules)
            runtimeAgentService.applyOnGetMessage(mock(ServiceMessage::class.java))

            assertEquals(it.expectedExecute, runtimeAgentService.isExecuteTestOnGetMessages as Boolean)
        }
    }

    private fun runExpectedFunctionError(func: () -> Unit): Boolean {
        return try {
            func.invoke()
            false
        } catch (ignored: Exception) {
            true
        }
    }

    private val testDslConditionBlocksArray = arrayListOf<TestDslConditionBlocks>(
            TestDslConditionBlocks( // все блоки вернут да
                    """
                        init = {
                            type = "worker"
                            name = "name"
                            masId = "masId"
                        }
                        onGetMessage = { message ->
                            executeCondition ("Успешное выполнение функции") {
                                anyOf {
                                    allOf {
                                        condition {
                                            true
                                        }
                                        condition {
                                            true
                                        }
                                    }
                                    condition {
                                        false
                                    }
                                }
                                execute {
                                    testOnGetMessageFun()
                                }
                            }
                        }
                        onLoadImage = {}
                        onEndImageTask = {}
                    """,
                    true
            ),
            TestDslConditionBlocks( // блоки allOf вернёт нет
                    """
                        init = {
                            type = "worker"
                            name = "name"
                            masId = "masId"
                        }
                        onGetMessage = { message ->
                            executeCondition ("Нет выполнение функции") {
                                anyOf {
                                    allOf {
                                        condition {
                                            true
                                        }
                                        condition {
                                            false
                                        }
                                    }
                                    condition {
                                        false
                                    }
                                }
                                execute {
                                    testOnGetMessageFun()
                                }
                            }
                        }
                        onLoadImage = {}
                        onEndImageTask = {}
                    """,
                    false
            ),
            TestDslConditionBlocks( // блок вернёт нет
                    """
                        init = {
                            type = "worker"
                            name = "name"
                            masId = "masId"
                        }
                        onGetMessage = { message ->
                            executeCondition ("Успешное выполнение функции") {
                                anyOf {
                                    allOf {
                                        condition {
                                            true
                                        }
                                        condition {
                                            true
                                        }
                                    }
                                    condition {
                                        false
                                    }
                                }
                                // по and объединяются
                                condition {
                                    false
                                }
                                execute {
                                    testOnGetMessageFun()
                                }
                            }
                        }
                        onLoadImage = {}
                        onEndImageTask = {}
                    """,
                    false
            ),
            TestDslConditionBlocks(
                    """
                        init = {
                            type = "worker"
                            name = "name"
                            masId = "masId"
                        }
                        onGetMessage = { message ->
                            executeCondition ("Успешное выполнение функции") {
                                execute {
                                    testOnGetMessageFun()
                                }
                            }
                        }
                        onLoadImage = {}
                        onEndImageTask = {}
                    """,
                    true
            ),
            TestDslConditionBlocks(
                    """
                        init = {
                            type = "worker"
                            name = "name"
                            masId = "masId"
                        }
                        onGetMessage = { message ->
                            executeCondition ("Успешное выполнение функции") {
                                condition {
                                    true
                                }
                                execute {
                                    testOnGetMessageFun()
                                }
                            }
                        }
                        onLoadImage = {}
                        onEndImageTask = {}
                    """,
                    true
            )
    )
    private class TestDslConditionBlocks(val rules: String, val expectedExecute: Boolean)
}
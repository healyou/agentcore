package dsl

import db.core.servicemessage.ServiceMessage
import org.junit.Assert
import org.junit.Test

import java.awt.image.BufferedImage

import static org.easymock.EasyMock.mock

/**
 * @author Nikita Gorodilov
 */
class RuntimeAgentServiceTest extends Assert {

    /* Без загрузки функций выходит ошибка при работе */
    @Test
    void testErrorApplyNotLoadFunctions() {
        def runtimeAgentService = new RuntimeAgentService()

        assertTrue(runExpectedFunctionError { runtimeAgentService.applyInit() })
        assertTrue(runExpectedFunctionError { runtimeAgentService.applyOnLoadImage(mock(BufferedImage.class)) })
        assertTrue(runExpectedFunctionError { runtimeAgentService.applyOnEndImageTask(mock(BufferedImage.class)) })
        assertTrue(runExpectedFunctionError { runtimeAgentService.applyOnGetMessage(mock(ServiceMessage.class)) })
    }

    /* Если в dsl не предоставлены все функции - выходит ошибка */
    @Test
    void testLoadErrorsDsl() {
        def runtimeAgentService = new RuntimeAgentService()

        assertTrue(runExpectedFunctionError { runtimeAgentService.loadExecuteRules(getClass().getResource("noinitagentdsl.groovy").toURI().path) })
        assertFalse(runExpectedFunctionError { runtimeAgentService.loadExecuteRules(getClass().getResource("testagentdsl.groovy").toURI().path) })
    }

    /* Тест загрузки данных из dsl */
    @Test
    void testApplyInit() {
        def runtimeAgentService = new RuntimeAgentService()

        runtimeAgentService.loadExecuteRules(getClass().getResource("testagentdsl.groovy").toURI().path)
        runtimeAgentService.applyInit()

        /* Данные из файла testagentdsl.groovy */
        assertEquals(runtimeAgentService.agentName, "name")
        assertEquals(runtimeAgentService.agentType, "worker")
        assertEquals(runtimeAgentService.masId, "masId")
    }

    /* Проходят все вызовы функций из dsl */
    @Test
    void testExecuteDslFunction() {
        def runtimeAgentService = new TestRuntimeAgentServiceClass()

        runtimeAgentService.loadExecuteRules(getClass().getResource("testagentdsl.groovy").toURI().path)
        runtimeAgentService.applyInit()
        runtimeAgentService.applyOnLoadImage(mock(BufferedImage.class))
        runtimeAgentService.applyOnEndImageTask(mock(BufferedImage.class))
        runtimeAgentService.applyOnGetMessage(mock(ServiceMessage.class))

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
    void testDslInitBlock() {
        def runtimeAgentService = new TestRuntimeAgentServiceClass()
        def type = "worker"
        def name = "name"
        def masId = "masId"

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

    /* Проверка выполнения функции в блоках allOf, anyOf, condition and other */
    @Test
    void testDslExecuteConditionBlock() {
        testDslConditionBlocksArray.forEach {
            def runtimeAgentService = new TestRuntimeAgentServiceClass()

            runtimeAgentService.testLoadExecuteRules(it.rules)
            runtimeAgentService.applyOnGetMessage(mock(ServiceMessage.class))

            assertEquals(it.expectedExecute, runtimeAgentService.isExecuteTestOnGetMessages as Boolean)
        }
    }

    private runExpectedFunctionError(Closure c) {
        try {
            c()
            false
        } catch (ignored) {
            true
        }
    }

    def testDslConditionBlocksArray = [
            new TestDslConditionBlocks( // все блоки вернут да
                    rules: """
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
                    expectedExecute: true
            ),
            new TestDslConditionBlocks( // блоки allOf вернёт нет
                    rules: """
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
                    expectedExecute: false
            ),
            new TestDslConditionBlocks( // блок вернёт нет
                    rules: """
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
                    expectedExecute: false
            ),
            new TestDslConditionBlocks(
                    rules: """
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
                    expectedExecute: true
            ),
            new TestDslConditionBlocks(
                    rules: """
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
                    expectedExecute: true
            )
    ]
    private class TestDslConditionBlocks {
        def rules
        def expectedExecute
    }
}

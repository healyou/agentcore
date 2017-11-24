package dsl

import db.core.servicemessage.ServiceMessage
import org.junit.Assert
import org.junit.Test
import service.objects.AgentType
import service.objects.MessageBodyType
import service.objects.MessageGoalType
import service.objects.MessageType

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
        assertEquals(runtimeAgentService.agentMasId, "masId")
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
        assertEquals(masId, runtimeAgentService.agentMasId)
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

    /* Проверка создания всех переменный из типов данных словарей(с сервиса) */
    @Test
    void testCreateBindingTypeVariables() {
        /* создание данных */
        def rule = testTypeRule
        def ras = new TestRuntimeAgentServiceClass()
        // TODO - вынести в модуль Objects
        ras.agentTypes = Arrays.asList(
                new AgentType(1L, AgentType.Code.WORKER, "name", false),
                new AgentType(1L, AgentType.Code.SERVER, "name", false)
        )
        ras.messageBodyTypes = Arrays.asList(
                new MessageBodyType(1L, MessageBodyType.Code.JSON, "name", false)
        )
        ras.messageGoalTypes = Arrays.asList(
                new MessageGoalType(1L, MessageGoalType.Code.TASK_DECISION, "name", false)
        )
        ras.messageTypes = Arrays.asList(
                new MessageType(1L, MessageType.Code.SEARCH_SOLUTION, "name", 1, ras.messageGoalTypes[0], false),
                new MessageType(1L, MessageType.Code.SEARCH_TASK_SOLUTION, "name", 2, ras.messageGoalTypes[0], false)
        )

        /* Проверка выполнения условий с созданными типами */
        def testClosure = {
            ras.applyOnGetMessage(mock(ServiceMessage.class))
            assertEquals(true, ras.isExecuteTestOnGetMessages as Boolean)
            ras.isExecuteTestOnGetMessages = false
        }

        ras.agentTypes.each {
            ras.testLoadExecuteRules(String.format(rule, "${ras.getAgentTypeVariableByCode(it.code.code)} == \"${it.code.code}\""))
            testClosure()
        }
        ras.messageBodyTypes.each {
            ras.testLoadExecuteRules(String.format(rule, "${ras.getMessageBodyTypeVariableByCode(it.code.code)} == \"${it.code.code}\""))
            testClosure()
        }
        ras.messageGoalTypes.each {
            ras.testLoadExecuteRules(String.format(rule, "${ras.getMessageGoalTypeVariableByCode(it.code.code)} == \"${it.code.code}\""))
            testClosure()
        }
        ras.messageTypes.each {
            ras.testLoadExecuteRules(String.format(rule, "${ras.getMessaTypeVariableByCode(it.code.code)} == \"${it.code.code}\""))
            testClosure()
        }
    }

    private boolean runExpectedFunctionError(Closure c) {
        try {
            c()
            false
        } catch (ignored) {
            true
        }
    }

    def testTypeRule =
            """
                init = {
                    type = "worker"
                    name = "name"
                    masId = "masId"
                }
                onGetMessage = {
                    executeCondition ("Успешное выполнение функции") {
                        condition() {
                            %s
                        }
                        execute() {
                            testOnGetMessageFun()
                        }
                    }
                }
                onLoadImage = {}
                onEndImageTask = {}
            """

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

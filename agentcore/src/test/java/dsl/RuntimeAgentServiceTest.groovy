package dsl

import db.core.servicemessage.ServiceMessage
import org.junit.Assert
import org.junit.Test
import service.objects.AgentType
import service.objects.MessageBodyType
import service.objects.MessageGoalType
import service.objects.MessageType
import objects.TypesObjects

import java.awt.image.BufferedImage

import static org.easymock.EasyMock.mock

/**
 * @author Nikita Gorodilov
 */
class RuntimeAgentServiceTest extends Assert {

    /**
     * Начало - Тестирование вызова метода sendMessage
     */

    /* Отправка сообщения с обязательными полями */
    @Test
    void testSendMessageWithRequiredParams() {
        def runtimeAgentService = createTestRuntimeAgentServiceClass()

        def isExecuteSendMessage = false
        runtimeAgentService.setAgentSendMessageClosure({ Map map ->
            isExecuteSendMessage = true
        })
        runtimeAgentService.testLoadExecuteRules(
                createDslWithOnGetLoadImageBlock(
                        """
                            execute {
                                sendMessage ${SendMessageParameters.MESSAGE_TYPE.paramName}: "${MessageType.Code.values()[0].code}",
                                        ${SendMessageParameters.IMAGE.paramName}: image,
                                        ${SendMessageParameters.AGENT_TYPES.paramName}: ["${AgentType.Code.values()[0].code}"]
                            }
                        """
                )
        )
        runtimeAgentService.applyOnLoadImage(mock(BufferedImage.class))

        assertTrue(isExecuteSendMessage)
    }

    /* Без обязательного поля(Image) будет ошибка */
    @Test
    void testSendMessageWithoutRequiredParams() {
        def runtimeAgentService = createTestRuntimeAgentServiceClass()

        def isExecuteSendMessage = false
        runtimeAgentService.setAgentSendMessageClosure({ Map map ->
            isExecuteSendMessage = true
        })
        runtimeAgentService.testLoadExecuteRules(
                createDslWithOnGetLoadImageBlock(
                        """
                            execute {
                                sendMessage ${SendMessageParameters.MESSAGE_TYPE.paramName}: "${MessageType.Code.values()[0].code}",
                                        ${SendMessageParameters.AGENT_TYPES.paramName}: ["${AgentType.Code.values()[0].code}"]
                            }
                        """
                )
        )
        def isError = false
        try {
            runtimeAgentService.applyOnLoadImage(mock(BufferedImage.class))
        } catch (ignored) {
            isError = true
        }
        assertTrue(isError)
        assertFalse(isExecuteSendMessage)
    }

    /**
     * Конец - Тестирование вызова метода sendMessage
     */

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
        def runtimeAgentService = new TestRuntimeAgentServiceClass()

        assertTrue(runExpectedFunctionError { runtimeAgentService.testLoadExecuteRules(createNotInitBlockDsl())})
        assertFalse(runExpectedFunctionError { runtimeAgentService.testLoadExecuteRules(createAllBlocksDsl())})
    }

    /* Тест загрузки данных из dsl */
    @Test
    void testApplyInit() {
        def runtimeAgentService = new TestRuntimeAgentServiceClass()

        runtimeAgentService.testLoadExecuteRules(createAllBlocksDsl())
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

        runtimeAgentService.testLoadExecuteRules(
                createDslWithExecuteConditionBlocks(
                        """
                            execute {
                                testOnGetMessageFun()
                            }
                        """,
                        """
                            execute {
                                testOnLoadImage()
                            }
                        """,
                        """
                            execute {
                                testOnEndImageTask()
                            }
                        """
                )
        )
        runtimeAgentService.applyInit()
        runtimeAgentService.applyOnLoadImage(mock(BufferedImage.class))
        runtimeAgentService.applyOnEndImageTask(mock(BufferedImage.class))
        runtimeAgentService.applyOnGetMessage(mock(ServiceMessage.class))

        assertTrue(runtimeAgentService.isExecuteInit as Boolean)
        assertTrue(runtimeAgentService.isExecuteTestOnGetMessages as Boolean)
        assertTrue(runtimeAgentService.isExecuteTestOnEndImageTask as Boolean)
        assertTrue(runtimeAgentService.isExecuteTestOnLoadImage as Boolean)
    }

    /* Можно вызвать функцию execute в executeCondition без condition блока */
    @Test
    void testExecuteFunctionWithoutCondition() {
        def runtimeAgentService = new TestRuntimeAgentServiceClass()
        runtimeAgentService.testLoadExecuteRules(
                createDslWithOnGetMessageExecuteConditionBlock(
                        """
                            execute {
                                testOnGetMessageFun()
                            }
                        """
                )
        )
        runtimeAgentService.applyInit()
        runtimeAgentService.applyOnGetMessage(mock(ServiceMessage.class))

        assertTrue(runtimeAgentService.isExecuteTestOnGetMessages as Boolean)
    }

    /* Нельзя вызвать функцию execute вне executeCondition блока */
    @Test(expected = Exception.class)
    void testExecuteFunctionWithoutExecuteConditionBlock() {
        def runtimeAgentService = new TestRuntimeAgentServiceClass()
        runtimeAgentService.testLoadExecuteRules(
                createDslWithOnGetMessageBlock(
                        """
                            execute {
                                testOnGetMessageFun()
                            }
                        """
                )
        )
        runtimeAgentService.applyInit()
        runtimeAgentService.applyOnGetMessage(mock(ServiceMessage.class))
    }

    /* Нельзя вызвать функции библиотеки вне execute блока */
    @Test(expected = Exception.class)
    void testExecuteLibraryFunctionWithoutExecuteBlock() {
        def runtimeAgentService = new TestRuntimeAgentServiceClass()
        runtimeAgentService.testLoadExecuteRules(
                createDslWithOnGetMessageBlock(
                        """
                            testOnGetMessageFun()
                        """
                )
        )
        runtimeAgentService.applyInit()
        runtimeAgentService.applyOnGetMessage(mock(ServiceMessage.class))
    }

    /* Выполнение двух и более функций в одном блоке dsl */
    @Test
    void testExecuteMoreOneConditionInOneBlock() {
        def runtimeAgentService = new TestRuntimeAgentServiceClass()
        runtimeAgentService.testLoadExecuteRules(
                createDslWithOnGetMessageBlock(
                        """
                            executeCondition ("Выполняется всегда") {
                                condition {
                                    true
                                }
                                execute {
                                    testOnGetMessageFun()
                                }
                            }
                            executeCondition ("Выполняется всегда") {
                                condition {
                                    true
                                }
                                execute {
                                    testOnLoadImage()
                                }
                            }
                            executeCondition ("Выполняется всегда") {
                                condition {
                                    true
                                }
                                execute {
                                    testOnEndImageTask()
                                }
                            }
                        """
                )
        )
        runtimeAgentService.applyInit()
        runtimeAgentService.applyOnGetMessage(mock(ServiceMessage.class))

        assertTrue(runtimeAgentService.isExecuteTestOnGetMessages as Boolean)
        assertTrue(runtimeAgentService.isExecuteTestOnEndImageTask as Boolean)
        assertTrue(runtimeAgentService.isExecuteTestOnLoadImage as Boolean)
    }

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

        /* Выполняется функция в dsl, которая проверяет условие СОЗДАННЫЙ_ТИП == "значение типа" */
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

    TestRuntimeAgentServiceClass createTestRuntimeAgentServiceClass() {
        def runtimeAgentService = new TestRuntimeAgentServiceClass()
        runtimeAgentService.setAgentTypes(TypesObjects.agentTypes)
        runtimeAgentService.setMessageBodyTypes(TypesObjects.messageBodyTypes)
        runtimeAgentService.setMessageGoalTypes(TypesObjects.messageGoalTypes)
        runtimeAgentService.setMessageTypes(TypesObjects.messageTypes)
        runtimeAgentService
    }

    boolean runExpectedFunctionError(Closure c) {
        try {
            c()
            false
        } catch (ignored) {
            true
        }
    }

    // TODO - вынести в objects все создания dsl
    String createNotInitBlockDsl() {
        """
            onGetMessage = { message -> }
            onLoadImage = { image -> }
            onEndImageTask = { updateImage -> }
        """
    }

    String createAllBlocksDsl() {
        """
            init = {
                type = "worker"
                name = "name"
                masId = "masId"
            }
            onGetMessage = { message ->
            }
            onLoadImage = { image ->
            }
            onEndImageTask = { updateImage ->
            }
        """
    }

    String createDslWithOnGetMessageExecuteConditionBlock(executeConditionBlockBody) {
        """
            init = {
                type = "worker"
                name = "name"
                masId = "masId"
            }
            onGetMessage = {
                executeCondition ("BlockBody") {
        """ +
                    executeConditionBlockBody +
        """
                }
            }
            onLoadImage = {}
            onEndImageTask = {}
        """
    }

    String createDslWithOnGetMessageBlock(executeConditionBlockBody) {
        """
            init = {
                type = "worker"
                name = "name"
                masId = "masId"
            }
            onGetMessage = {
                """ +
                executeConditionBlockBody +
                """
            }
            onLoadImage = {}
            onEndImageTask = {}
        """
    }

    String createDslWithExecuteConditionBlocks(onGetMessageBlock, onLoadImageBlock, onEndImageBlock) {
        """
            init = {
                type = "worker"
                name = "name"
                masId = "masId"
            }
            onGetMessage = {
                executeCondition ("BlockBody") {
                    """ +
                    onGetMessageBlock +
                    """
                }
            }
            onLoadImage = {
                executeCondition ("BlockBody") {
                    """ +
                    onLoadImageBlock +
                    """
                }
            }
            onEndImageTask = {
                executeCondition ("BlockBody") {
                    """ +
                    onEndImageBlock +
                    """
                }
            }
        """
    }

    String createDslWithOnGetLoadImageBlock(executeConditionBlockBody) {
        """
            init = {
                type = "worker"
                name = "name"
                masId = "masId"
            }
            onGetMessage = {}
            onLoadImage = { image ->
                executeCondition ("BlockBody") {
        """ +
                    executeConditionBlockBody +
        """
                }
            }
            onEndImageTask = {}
        """
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

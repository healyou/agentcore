package dsl

import dsl.base.SendMessageParameters
import dsl.objects.DslImage
import dsl.objects.DslMessage
import objects.DslObjects
import objects.StringObjects
import objects.TypesObjects
import org.junit.Assert
import org.junit.Test
import service.objects.AgentType
import service.objects.MessageType

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
                DslObjects.createDslWithOnGetLoadImageBlock(
                        """
                            execute {
                                sendMessage ${SendMessageParameters.MESSAGE_TYPE.paramName}: "${MessageType.Code.values()[0].code}",
                                        ${SendMessageParameters.IMAGE.paramName}: image,
                                        ${SendMessageParameters.AGENT_TYPES.paramName}: ["${AgentType.Code.values()[0].code}"]
                            }
                        """
                )
        )
        runtimeAgentService.applyOnLoadImage(mock(DslImage.class))

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
                DslObjects.createDslWithOnGetLoadImageBlock(
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
            runtimeAgentService.applyOnLoadImage(mock(DslImage.class))
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
        assertTrue(runExpectedFunctionError { runtimeAgentService.applyOnLoadImage(mock(DslImage.class)) })
        assertTrue(runExpectedFunctionError { runtimeAgentService.applyOnEndImageTask(mock(DslImage.class)) })
        assertTrue(runExpectedFunctionError { runtimeAgentService.applyOnGetMessage(mock(DslMessage.class)) })
    }

    /* Если в dsl не предоставлены все функции - выходит ошибка */
    @Test
    void testLoadErrorsDsl() {
        def runtimeAgentService = new TestRuntimeAgentServiceClass()

        assertTrue(runExpectedFunctionError { runtimeAgentService.testLoadExecuteRules(DslObjects.notInitBlockDsl)})
        assertFalse(runExpectedFunctionError { runtimeAgentService.testLoadExecuteRules(DslObjects.allBlocksDsl)})
    }

    /* Тест загрузки данных из dsl */
    @Test
    void testApplyInit() {
        def runtimeAgentService = new TestRuntimeAgentServiceClass()
        def type = TypesObjects.firstAgentTypeCodeStr()
        def name = StringObjects.randomString()
        def masId = StringObjects.randomString()

        runtimeAgentService.testLoadExecuteRules(DslObjects.allBlocksDslWithInitParams(type, name, masId))
        runtimeAgentService.applyInit()

        assertEquals(runtimeAgentService.agentName, name)
        assertEquals(runtimeAgentService.agentType, type)
        assertEquals(runtimeAgentService.agentMasId, masId)
    }

    /* Проходят все вызовы функций из dsl */
    @Test
    void testExecuteDslFunction() {
        def runtimeAgentService = new TestRuntimeAgentServiceClass()

        runtimeAgentService.testLoadExecuteRules(
                DslObjects.createDslWithExecuteConditionBlocks(
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
        runtimeAgentService.applyOnLoadImage(mock(DslImage.class))
        runtimeAgentService.applyOnEndImageTask(mock(DslImage.class))
        runtimeAgentService.applyOnGetMessage(mock(DslMessage.class))

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
                DslObjects.createDslWithOnGetMessageExecuteConditionBlock(
                        """
                            execute {
                                testOnGetMessageFun()
                            }
                        """
                )
        )
        runtimeAgentService.applyInit()
        runtimeAgentService.applyOnGetMessage(mock(DslMessage.class))

        assertTrue(runtimeAgentService.isExecuteTestOnGetMessages as Boolean)
    }

    /* Нельзя вызвать функцию execute вне executeCondition блока */
    @Test(expected = MissingPropertyException)
    void testExecuteFunctionWithoutExecuteConditionBlock() {
        def runtimeAgentService = new TestRuntimeAgentServiceClass()
        runtimeAgentService.testLoadExecuteRules(
                DslObjects.createDslWithOnGetMessageBlock(
                        """
                            execute {
                                testOnGetMessageFun()
                            }
                        """
                )
        )
        runtimeAgentService.applyInit()
        runtimeAgentService.applyOnGetMessage(mock(DslMessage.class))
    }

    /* Нельзя вызвать функции библиотеки вне execute блока */
    @Test(expected = MissingMethodException)
    void testExecuteLibraryFunctionWithoutExecuteBlock() {
        def runtimeAgentService = new TestRuntimeAgentServiceClass()
        runtimeAgentService.testLoadExecuteRules(DslObjects.createDslWithOnGetMessageBlock("testOnGetMessageFun()"))
        runtimeAgentService.applyInit()
        runtimeAgentService.applyOnGetMessage(mock(DslMessage.class))
    }

    /* Выполнение двух и более функций в одном блоке dsl */
    @Test
    void testExecuteMoreOneConditionInOneBlock() {
        def runtimeAgentService = new TestRuntimeAgentServiceClass()
        runtimeAgentService.testLoadExecuteRules(
                DslObjects.createDslWithOnGetMessageBlock(
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
        runtimeAgentService.applyOnGetMessage(mock(DslMessage.class))

        assertTrue(runtimeAgentService.isExecuteTestOnGetMessages as Boolean)
        assertTrue(runtimeAgentService.isExecuteTestOnEndImageTask as Boolean)
        assertTrue(runtimeAgentService.isExecuteTestOnLoadImage as Boolean)
    }

    /* Проверка выполнения функции в блоках allOf, anyOf, condition and other */
    @Test
    void testDslExecuteConditionBlock() {
        DslObjects.testDslConditionBlocksArray("testOnGetMessageFun()").forEach {
            def runtimeAgentService = new TestRuntimeAgentServiceClass()

            runtimeAgentService.testLoadExecuteRules(it.rules)
            runtimeAgentService.applyOnGetMessage(mock(DslMessage.class))

            assertEquals(it.expectedExecute, runtimeAgentService.isExecuteTestOnGetMessages as Boolean)
        }
    }

    /* Проверка создания всех переменный из типов данных словарей(с сервиса) */
    @Test
    void testCreateBindingTypeVariables() {
        def ras = new TestRuntimeAgentServiceClass()
        ras.agentTypes = TypesObjects.agentTypes
        ras.messageBodyTypes = TypesObjects.messageBodyTypes
        ras.messageGoalTypes = TypesObjects.messageGoalTypes
        ras.messageTypes = TypesObjects.messageTypes

        /* Проверка выполнения условий с созданными типами */
        def testClosure = {
            ras.applyOnGetMessage(mock(DslMessage.class))
            assertEquals(true, ras.isExecuteTestOnGetMessages as Boolean)
            ras.isExecuteTestOnGetMessages = false
        }

        /* Выполняется функция в dsl, которая проверяет условие СОЗДАННЫЙ_ТИП == "значение типа" */
        ras.agentTypes.each {
            ras.testLoadExecuteRules(DslObjects.executeConditionDsl(
                    "${ras.getAgentTypeVariableByCode(it.code.code)} == \"${it.code.code}\"",
                    "testOnGetMessageFun()"
            ))
            testClosure()
        }
        ras.messageBodyTypes.each {
            ras.testLoadExecuteRules(DslObjects.executeConditionDsl(
                    "${ras.getMessageBodyTypeVariableByCode(it.code.code)} == \"${it.code.code}\"",
                    "testOnGetMessageFun()"
            ))
            testClosure()
        }
        ras.messageGoalTypes.each {
            ras.testLoadExecuteRules(DslObjects.executeConditionDsl(
                    "${ras.getMessageGoalTypeVariableByCode(it.code.code)} == \"${it.code.code}\"",
                    "testOnGetMessageFun()"
            ))
            testClosure()
        }
        ras.messageTypes.each {
            ras.testLoadExecuteRules(DslObjects.executeConditionDsl(
                    "${ras.getMessaTypeVariableByCode(it.code.code)} == \"${it.code.code}\"",
                    "testOnGetMessageFun()"
            ))
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
}

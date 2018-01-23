package com.mycompany.dsl

import com.mycompany.dsl.base.SendMessageParameters
import com.mycompany.dsl.objects.DslImage
import com.mycompany.dsl.objects.DslLocalMessage
import com.mycompany.dsl.objects.DslServiceMessage
import objects.DslObjects
import objects.StringObjects
import objects.TypesObjects
import org.junit.Assert
import org.junit.Test

import java.util.stream.Collectors

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
                                sendMessage ${SendMessageParameters.MESSAGE_TYPE.paramName}: "${TypesObjects.messageTypes[0].code}",
                                        ${SendMessageParameters.IMAGE.paramName}: image,
                                        ${SendMessageParameters.AGENT_TYPES.paramName}: ["${TypesObjects.agentTypes[0].code}"]
                            }
                        """
                )
        )
        runtimeAgentService.applyOnLoadImage(mock(DslImage.class))

        assertTrue(isExecuteSendMessage)
    }

    /* Без обязательного поля Image будет ошибка */
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
                                sendMessage ${SendMessageParameters.MESSAGE_TYPE.paramName}: "${TypesObjects.messageTypes[0].code}",
                                        ${SendMessageParameters.AGENT_TYPES.paramName}: ["${TypesObjects.agentTypes[0].code}"]
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
        assertTrue(runExpectedFunctionError { runtimeAgentService.applyOnGetServiceMessage(mock(DslServiceMessage.class)) })
        assertTrue(runExpectedFunctionError { runtimeAgentService.applyOnGetLocalMessage(mock(DslLocalMessage.class)) })
    }

    /* Если в dsl не предоставлены все функции - выходит ошибка */
    @Test
    void testLoadErrorsDsl() {
        def runtimeAgentService = new TestRuntimeAgentServiceClass()

        /**
         * Запуск с ошибок без одного из блоков
         */
        def dslBlocks = DslObjects.allBlocksDslArray
        for (i in 0..dslBlocks.size() - 1) {
            def dslWithoutOneBlock = ""
            dslBlocks.stream().filter( {it -> return it != dslBlocks[i]} ).collect(Collectors.toList()).toArray().each {
                dslWithoutOneBlock += "$it\n "
            }
            assertTrue(runExpectedFunctionError { runtimeAgentService.testLoadExecuteRules(dslWithoutOneBlock)})
        }
        assertFalse(runExpectedFunctionError { runtimeAgentService.testLoadExecuteRules(DslObjects.allBlocksDsl)})
    }

    /* Тест загрузки данных из dsl */
    @Test
    void testApplyInit() {
        def runtimeAgentService = new TestRuntimeAgentServiceClass()
        def type = TypesObjects.testAgent1TypeCode()
        def name = StringObjects.randomString()
        def masId = StringObjects.randomString()
        def defaultBodyType = StringObjects.randomString()

        runtimeAgentService.testLoadExecuteRules(
                DslObjects.allBlocksDslWithInitParams(type, name, masId, defaultBodyType)
        )
        runtimeAgentService.applyInit()

        assertEquals(runtimeAgentService.agentName, name)
        assertEquals(runtimeAgentService.agentType, type)
        assertEquals(runtimeAgentService.agentMasId, masId)
        assertEquals(runtimeAgentService.defaultBodyType, defaultBodyType)
    }

    /* Проходят все вызовы функций из dsl */
    @Test
    void testExecuteDslFunction() {
        def runtimeAgentService = new TestRuntimeAgentServiceClass()

        runtimeAgentService.testLoadExecuteRules(
                DslObjects.createDslWithExecuteConditionBlocks(
                        """
                            execute {
                                testOnGetServiceMessageFun()
                            }
                        """,
                        """
                            execute {
                                testOnGetLocalMessageFun()
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
        runtimeAgentService.applyOnGetServiceMessage(mock(DslServiceMessage.class))
        runtimeAgentService.applyOnGetLocalMessage(mock(DslLocalMessage.class))

        assertTrue(runtimeAgentService.isExecuteInit as Boolean)
        assertTrue(runtimeAgentService.isExecuteTestOnGetServiceMessages as Boolean)
        assertTrue(runtimeAgentService.isExecuteTestOnGetLocalMessages as Boolean)
        assertTrue(runtimeAgentService.isExecuteTestOnEndImageTask as Boolean)
        assertTrue(runtimeAgentService.isExecuteTestOnLoadImage as Boolean)
    }

    /* Можно вызвать функцию execute в executeCondition без condition блока */
    @Test
    void testExecuteFunctionWithoutCondition() {
        def runtimeAgentService = new TestRuntimeAgentServiceClass()
        runtimeAgentService.testLoadExecuteRules(
                DslObjects.createDslWithOnGetServiceMessageExecuteConditionBlock(
                        """
                            execute {
                                testOnGetServiceMessageFun()
                            }
                        """
                )
        )
        runtimeAgentService.applyInit()
        runtimeAgentService.applyOnGetServiceMessage(mock(DslServiceMessage.class))

        assertTrue(runtimeAgentService.isExecuteTestOnGetServiceMessages as Boolean)
    }

    /* Нельзя вызвать функцию execute вне executeCondition блока */
    @Test(expected = MissingPropertyException)
    void testExecuteFunctionWithoutExecuteConditionBlock() {
        def runtimeAgentService = new TestRuntimeAgentServiceClass()
        runtimeAgentService.testLoadExecuteRules(
                DslObjects.createDslWithOnGetServiceMessageBlock(
                        """
                            execute {
                                testOnGetServiceMessageFun()
                            }
                        """
                )
        )
        runtimeAgentService.applyInit()
        runtimeAgentService.applyOnGetServiceMessage(mock(DslServiceMessage.class))
    }

    /* Нельзя вызвать функции библиотеки вне execute блока */
    @Test(expected = MissingMethodException)
    void testExecuteLibraryFunctionWithoutExecuteBlock() {
        def runtimeAgentService = new TestRuntimeAgentServiceClass()
        runtimeAgentService.testLoadExecuteRules(DslObjects.createDslWithOnGetServiceMessageBlock("testOnGetMessageFun()"))
        runtimeAgentService.applyInit()
        runtimeAgentService.applyOnGetServiceMessage(mock(DslServiceMessage.class))
    }

    /* Выполнение двух и более функций в одном блоке dsl */
    @Test
    void testExecuteMoreOneConditionInOneBlock() {
        def runtimeAgentService = new TestRuntimeAgentServiceClass()
        runtimeAgentService.testLoadExecuteRules(
                DslObjects.createDslWithOnGetServiceMessageBlock(
                        """
                            executeCondition ("Выполняется всегда") {
                                condition {
                                    true
                                }
                                execute {
                                    testOnGetServiceMessageFun()
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
        runtimeAgentService.applyOnGetServiceMessage(mock(DslServiceMessage.class))

        assertTrue(runtimeAgentService.isExecuteTestOnGetServiceMessages as Boolean)
        assertTrue(runtimeAgentService.isExecuteTestOnEndImageTask as Boolean)
        assertTrue(runtimeAgentService.isExecuteTestOnLoadImage as Boolean)
    }

    /* Проверка выполнения функции в блоках allOf, anyOf, condition and other */
    @Test
    void testDslExecuteConditionBlock() {
        DslObjects.testDslConditionBlocksArray("testOnGetServiceMessageFun()").forEach {
            def runtimeAgentService = new TestRuntimeAgentServiceClass()

            runtimeAgentService.testLoadExecuteRules(it.rules)
            runtimeAgentService.applyOnGetServiceMessage(mock(DslServiceMessage.class))

            assertEquals(it.expectedExecute, runtimeAgentService.isExecuteTestOnGetServiceMessages as Boolean)
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
            ras.applyOnGetServiceMessage(mock(DslServiceMessage.class))
            assertEquals(true, ras.isExecuteTestOnGetServiceMessages as Boolean)
            ras.isExecuteTestOnGetServiceMessages = false
        }

        /* Выполняется функция в dsl, которая проверяет условие СОЗДАННЫЙ_ТИП == "значение типа" */
        ras.agentTypes.each {
            ras.testLoadExecuteRules(DslObjects.executeConditionDsl(
                    "${ras.getAgentTypeVariableByCode(it.code)} == \"${it.code}\"",
                    "testOnGetServiceMessageFun()"
            ))
            testClosure()
        }
        ras.messageBodyTypes.each {
            ras.testLoadExecuteRules(DslObjects.executeConditionDsl(
                    "${ras.getMessageBodyTypeVariableByCode(it.code)} == \"${it.code}\"",
                    "testOnGetServiceMessageFun()"
            ))
            testClosure()
        }
        ras.messageGoalTypes.each {
            ras.testLoadExecuteRules(DslObjects.executeConditionDsl(
                    "${ras.getMessageGoalTypeVariableByCode(it.code)} == \"${it.code}\"",
                    "testOnGetServiceMessageFun()"
            ))
            testClosure()
        }
        ras.messageTypes.each {
            ras.testLoadExecuteRules(DslObjects.executeConditionDsl(
                    "${ras.getMessaTypeVariableByCode(it.code)} == \"${it.code}\"",
                    "testOnGetServiceMessageFun()"
            ))
            testClosure()
        }
    }

    /* Блок инициализации можно задать как строкой, так и константным параметром */
    @Test
    void testInitBlockWithTypeParameter() {
        /* Константа */
        def ras = new TestRuntimeAgentServiceClass()
        ras.agentTypes = TypesObjects.agentTypes
        def type = TypesObjects.testAgentType1()
        ras.testLoadExecuteRules(DslObjects.allBlocksDslWithTypeParameterInInitBlock(
                "${ras.getAgentTypeVariableByCode(type.code)}")
        )
        ras.applyInit()
        assertEquals(ras.agentType, type.code)

        /* Строковый параметр */
        ras = new TestRuntimeAgentServiceClass()
        type = TypesObjects.testAgent1TypeCode()
        ras.testLoadExecuteRules(DslObjects.allBlocksDslWithTypeParameterInInitBlock("\"$type\""))
        ras.applyInit()
        assertEquals(ras.agentType, type)
    }

    static TestRuntimeAgentServiceClass createTestRuntimeAgentServiceClass() {
        def runtimeAgentService = new TestRuntimeAgentServiceClass()

        runtimeAgentService.setAgentTypes(TypesObjects.agentTypes)
        runtimeAgentService.setMessageBodyTypes(TypesObjects.messageBodyTypes)
        runtimeAgentService.setMessageGoalTypes(TypesObjects.messageGoalTypes)
        runtimeAgentService.setMessageTypes(TypesObjects.messageTypes)

        runtimeAgentService
    }

    static boolean runExpectedFunctionError(Closure c) {
        try {
            c()
            false
        } catch (ignored) {
            true
        }
    }
}

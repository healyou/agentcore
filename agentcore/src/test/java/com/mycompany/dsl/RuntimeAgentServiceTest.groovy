package com.mycompany.dsl

import com.mycompany.dsl.base.SendServiceMessageParameters
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
     * Начало - Тестирование вызова метода sendServiceMessage
     */

    @Test
    void "Сообщение со всеми параметрами должно успешно отправляться"() {
        def runtimeAgentService = createTestRuntimeAgentServiceClass()

        def isExecuteSendMessage = false
        runtimeAgentService.setAgentSendMessageClosure({ Map map ->
            isExecuteSendMessage = true
        })
        runtimeAgentService.testLoadExecuteRules(
                DslObjects.createDslWithOnGetLoadImageBlock(
                        """
                            execute {
                                sendServiceMessage ${SendServiceMessageParameters.MESSAGE_TYPE.paramName}: "${TypesObjects.messageTypes[0].code}",
                                        ${SendServiceMessageParameters.IMAGE.paramName}: image,
                                        ${SendServiceMessageParameters.AGENT_TYPES.paramName}: ["${TypesObjects.agentTypes[0].code}"]
                            }
                        """
                )
        )
        runtimeAgentService.applyOnLoadImage(mock(DslImage.class))

        assertTrue(isExecuteSendMessage)
    }

    @Test
    void "Отправка сообщения без обязательного параметра выдаст ошибку"() {
        def runtimeAgentService = createTestRuntimeAgentServiceClass()

        def isExecuteSendMessage = false
        runtimeAgentService.setAgentSendMessageClosure({ Map map ->
            isExecuteSendMessage = true
        })
        runtimeAgentService.testLoadExecuteRules(
                DslObjects.createDslWithOnGetLoadImageBlock(
                        """
                            execute {
                                sendServiceMessage ${SendServiceMessageParameters.MESSAGE_TYPE.paramName}: "${TypesObjects.messageTypes[0].code}",
                                        ${SendServiceMessageParameters.AGENT_TYPES.paramName}: ["${TypesObjects.agentTypes[0].code}"]
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
     * Конец - Тестирование вызова метода sendServiceMessage
     */

    @Test
    void "Без загрузки функций выходит ошибка при работе"() {
        def runtimeAgentService = new RuntimeAgentService()

        assertTrue(runExpectedFunctionError { runtimeAgentService.applyInit() })
        assertTrue(runExpectedFunctionError { runtimeAgentService.applyOnLoadImage(mock(DslImage.class)) })
        assertTrue(runExpectedFunctionError { runtimeAgentService.applyOnEndImageTask(mock(DslImage.class)) })
        assertTrue(runExpectedFunctionError { runtimeAgentService.applyOnGetServiceMessage(mock(DslServiceMessage.class)) })
        assertTrue(runExpectedFunctionError { runtimeAgentService.applyOnGetLocalMessage(mock(DslLocalMessage.class)) })
    }

    /* Если в dsl не предоставлены все функции - выходит ошибка */
    @Test
    void "Dsl без одного обязательного блока выдаст ошибку"() {
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

    @Test
    void "Блок init должен корректно инициализировать данные"() {
        def runtimeAgentService = new TestRuntimeAgentServiceClass()
        def type = TypesObjects.testAgent1TypeCode()
        def name = StringObjects.randomString()
        def masId = StringObjects.randomString()
        def defaultBodyType = StringObjects.randomString()
        def localMessageTypes = TypesObjects.localMessageTypes();
        def taskTypes = TypesObjects.taskTypes();

        runtimeAgentService.testLoadExecuteRules(
                DslObjects.allBlocksDslWithInitParams(type, name, masId, defaultBodyType,
                        TypesObjects.typesAsStringArray(localMessageTypes.toList()),
                        TypesObjects.typesAsStringArray(taskTypes.toList())
                )
        )
        runtimeAgentService.applyInit()

        assertEquals(runtimeAgentService.agentName, name)
        assertEquals(runtimeAgentService.agentType, type)
        assertEquals(runtimeAgentService.agentMasId, masId)
        assertEquals(runtimeAgentService.defaultBodyType, defaultBodyType)
        assertEquals(runtimeAgentService.localMessageTypes, localMessageTypes)
        assertEquals(runtimeAgentService.taskTypes, taskTypes)
    }

    @Test
    void "Вызовы всех dsl функций проходят успешно"() {
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

    @Test
    void "Можно вызвать функцию execute в executeCondition без condition блока"() {
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

    /*  */
    @Test(expected = MissingPropertyException)
    void "Нельзя вызвать функцию execute вне executeCondition блока"() {
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

    @Test(expected = MissingMethodException)
    void "Нельзя вызвать функции библиотеки вне execute блока"() {
        def runtimeAgentService = new TestRuntimeAgentServiceClass()
        runtimeAgentService.testLoadExecuteRules(DslObjects.createDslWithOnGetServiceMessageBlock("testOnGetMessageFun()"))
        runtimeAgentService.applyInit()
        runtimeAgentService.applyOnGetServiceMessage(mock(DslServiceMessage.class))
    }

    @Test
    void "В одном блоке можно записать несколько executeCondition блоков"() {
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

    /*  */
    @Test
    void "Функция в блоке execute должна выполняться в зависимости от условий блоков allOf, anyOf, condition"() {
        DslObjects.testDslConditionBlocksArray("testOnGetServiceMessageFun()").forEach {
            def runtimeAgentService = new TestRuntimeAgentServiceClass()

            runtimeAgentService.testLoadExecuteRules(it.rules)
            runtimeAgentService.applyOnGetServiceMessage(mock(DslServiceMessage.class))

            assertEquals(it.expectedExecute, runtimeAgentService.isExecuteTestOnGetServiceMessages as Boolean)
        }
    }

    @Test
    void "Переменные из типов данных должны успешно создаваться"() {
        def ras = new TestRuntimeAgentServiceClass()
        ras.agentTypes = TypesObjects.agentTypes
        ras.messageBodyTypes = TypesObjects.messageBodyTypes
        ras.messageGoalTypes = TypesObjects.messageGoalTypes
        ras.serviceMessageTypes = TypesObjects.messageTypes
        ras.localMessageTypes = TypesObjects.localMessageTypes()
        ras.taskTypes = TypesObjects.taskTypes()

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
        ras.serviceMessageTypes.each {
            ras.testLoadExecuteRules(DslObjects.executeConditionDsl(
                    "${ras.getServiceMessageTypeVariableByCode(it.code)} == \"${it.code}\"",
                    "testOnGetServiceMessageFun()"
            ))
            testClosure()
        }
        ras.localMessageTypes.each {
            ras.testLoadExecuteRules(DslObjects.executeConditionDsl(
                    "${ras.getLocalMessageTypeVariableByCode(it)} == \"${it}\"",
                    "testOnGetServiceMessageFun()"
            ))
            testClosure()
        }
        ras.taskTypes.each {
            ras.testLoadExecuteRules(DslObjects.executeConditionDsl(
                    "${ras.getTaskTypeVariableByCode(it)} == \"${it}\"",
                    "testOnGetServiceMessageFun()"
            ))
            testClosure()
        }
    }

    @Test
    void "Тип агента можно задать как строкой, так и константным параметром"() {
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
        runtimeAgentService.setServiceMessageTypes(TypesObjects.messageTypes)

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

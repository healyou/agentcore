package objects

import com.mycompany.dsl.TestRuntimeAgentServiceClass
import com.mycompany.dsl.objects.DslAgentData
import com.mycompany.dsl.objects.DslTaskData

/**
 * @author Nikita Gorodilov
 */
class DslObjects {

    /**
     * Данные для тестирования a1, a2 dsl агентов в RuntimeAgentTest
     */
    static final def a1_testdslConditionEventName = "a1_lmt_event1"
    static final def a2_testdslConditionEventName = "a2_lmt_event1"
    static final def a1_testdslTaskType = "a1_tt1"
    static final def a2_testdslTaskType = "a2_tt1"
    static final def a1_testdslTaskData = new DslTaskData(a1_testdslTaskType)
    static final def a2_testdslTaskData = new DslTaskData(a2_testdslTaskType)

    static final def taskType = StringObjects.randomString()
    static final def agentData = new DslAgentData(1L)

    static final def notInitBlockDsl =
        """
            onGetServiceMessage = { serviceMessage -> }
            onGetLocalMessage = { localMessage -> }
            onEndTask = { taskData -> }
            onGetSystemEvent = { systemEvent -> }
        """

    static def allBlocksDslWithTypeParameterInInitBlock(typeParameter) {
        """
            init = {
                type = $typeParameter
                name = "${StringObjects.randomString()}"
                masId = "${StringObjects.randomString()}"
                defaultBodyType = "${StringObjects.randomString()}"
                defaultGoalType = "${StringObjects.randomString()}"
                localMessageTypes = ${TypesObjects.localMessageTypesAsStringArray()}
                taskTypes = ${TypesObjects.taskTypesAsStringArray()}
            }
            onGetServiceMessage = { serviceMessage -> }
            onGetLocalMessage = { localMessage -> }
            onEndTask = { taskData -> }
            onGetSystemEvent = { systemEvent -> }
        """
    }

    static def allBlocksDslWithInitParams(type, name, masId, bodyType, localMessageTypes, taskTypes) {
        """
            init = {
                type = "$type"
                name = "$name"
                masId = "$masId"
                defaultBodyType = "$bodyType"
                localMessageTypes = $localMessageTypes
                taskTypes = $taskTypes
            }
            onGetServiceMessage = { serviceMessage -> }
            onGetLocalMessage = { localMessage -> }
            onEndTask = { taskData -> }
            onGetSystemEvent = { systemEvent -> }
        """
    }

    static final def allBlocksDslArray = [
            "${randomInitBlock()}",
            "onGetServiceMessage = { message -> }",
            "onGetLocalMessage = { localMessage -> }",
            "onEndTask = { taskData -> }",
            "onGetSystemEvent = { systemEvent -> }"
    ]

    static final def allBlocksDsl = createAllBlocksDsl()
    private static final def createAllBlocksDsl() {
        def dsl = ""
        allBlocksDslArray.each {
            dsl += "$it\n "
        }
        dsl
    }

    static def createDslWithOnGetServiceMessageExecuteConditionBlock(executeConditionBlockBody) {
        """
            ${randomInitBlock()}
            onGetServiceMessage = {
                executeCondition ("BlockBody") {
        """ +
                executeConditionBlockBody +
                """
                }
            }
            onGetLocalMessage = { localMessage -> }
            onEndTask = { taskData -> }
            onGetSystemEvent = { systemEvent -> }
        """
    }

    static def createDslWithOnGetServiceMessageBlock(executeConditionBlockBody) {
        """
            ${randomInitBlock()}
            onGetServiceMessage = {
                """ +
                executeConditionBlockBody +
                """
            }
            onGetLocalMessage = { localMessage -> }
            onEndTask = { taskData -> }
            onGetSystemEvent = { systemEvent -> }
        """
    }

    static def createDslWithOnGetSystemEventBlock(executeConditionBlockBody) {
        """
            ${randomInitBlock()}
            onGetServiceMessage = { serviceMessage ->}
            onGetLocalMessage = { localMessage -> }
            onEndTask = { taskData -> }
            onGetSystemEvent = { systemEvent -> 
                """ +
                executeConditionBlockBody +
                """
            }
        """
    }

    static def createDslWithExecuteConditionBlocks(onGetServiceMessageBlock, onGetLocalMessageBlock, onEndTaskBlock, onGetSystemEventBlock) {
        """
            ${randomInitBlock()}
            onGetServiceMessage = {
                executeCondition ("BlockBody") {
                    """ +
                onGetServiceMessageBlock +
                """
                }
            }
            onGetLocalMessage = { localMessage -> 
                executeCondition ("BlockBody") {
                    """ +
                onGetLocalMessageBlock +
                """
                }
            }
            onEndTask = { taskData ->
                executeCondition ("BlockBody") {
                    """ +
                onEndTaskBlock +
                """
                }
            }
            onGetSystemEvent = { systemEvent -> 
                executeCondition ("BlockBody") {
                    """ +
                onGetSystemEventBlock +
                """
                }
            }
        """
    }

    static def executeConditionDsl(condition, execute) {
        """
                ${randomInitBlock()}
                onGetServiceMessage = {
                    executeCondition ("Успешное выполнение функции") {
                        condition() {
                            $condition
                        }
                        execute() {
                            $execute
                        }
                    }
                }
                onGetLocalMessage = { localMessage -> }
                onEndTask = { taskData -> }
                onGetSystemEvent = { systemEvent -> }
            """
    }

    static def randomInitBlock() {
        """
            init = {
                type = "${TypesObjects.testAgent1TypeCode()}"
                name = "${StringObjects.randomString()}"
                masId = "${StringObjects.randomString()}"
                defaultBodyType = "${StringObjects.randomString()}"
                localMessageTypes = ${TypesObjects.localMessageTypesAsStringArray()}
                taskTypes = ${TypesObjects.taskTypesAsStringArray()}
            }
        """
    }

    /**
     * Класс для тестирования имён параметров
     */
    static def class TestDslParameterName {

        TestRuntimeAgentServiceClass ras
        def getTypeFunctionName
        def typeArrayName
        def getTypeClosure = { code ->
            return ras."$getTypeFunctionName"(code)
        }

        TestDslParameterName(typeArrayName, getTypeFunctionName, ras) {
            this.getTypeFunctionName = getTypeFunctionName
            this.typeArrayName = typeArrayName
            this.ras = ras
        }
    }
    static TestDslParameterName[] testDslParameterNameArray(List<String> typeArrayNames, List<String> getTypeFunctionNames, TestRuntimeAgentServiceClass ras) {
        if (typeArrayNames.size() != getTypeFunctionNames.size()) {
            throw new RuntimeException("Размер параметров должен совпадать")
        }

        def ret = []
        typeArrayNames.indexed().each { index, value ->
            ret.add(new TestDslParameterName(value, getTypeFunctionNames[index], ras))
        }
        ret
    }

    /**
     * Блоки dsl, где функция execute может выполнять и не выполняться
     * @param execute выполняемая функция
     * @return информация о выполнение функции
     */
    static def testDslConditionBlocksArray(execute) {
        [
                new TestDslConditionBlocks( // все блоки вернут да
                        rules: """
                        ${randomInitBlock()}
                        onGetServiceMessage = { message ->
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
                                    $execute
                                }
                            }
                        }
                        onGetLocalMessage = { localMessage -> }
                        onEndTask = { taskData -> }
                        onGetSystemEvent = { systemEvent -> }
                    """,
                        expectedExecute: true
                ),
                new TestDslConditionBlocks( // блоки allOf вернёт нет
                        rules: """
                        ${randomInitBlock()}
                        onGetServiceMessage = { message ->
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
                                    $execute
                                }
                            }
                        }
                        onGetLocalMessage = { localMessage -> }
                        onEndTask = { taskData -> }
                        onGetSystemEvent = { systemEvent -> }
                    """,
                        expectedExecute: false
                ),
                new TestDslConditionBlocks( // блок вернёт нет
                        rules: """
                        ${randomInitBlock()}
                        onGetServiceMessage = { message ->
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
                                    $execute
                                }
                            }
                        }
                        onGetLocalMessage = { localMessage -> }
                        onEndTask = { taskData -> }
                        onGetSystemEvent = { systemEvent -> }
                    """,
                        expectedExecute: false
                ),
                new TestDslConditionBlocks(
                        rules: """
                        ${randomInitBlock()}
                        onGetServiceMessage = { message ->
                            executeCondition ("Успешное выполнение функции") {
                                execute {
                                    $execute
                                }
                            }
                        }
                        onGetLocalMessage = { localMessage -> }
                        onEndTask = { taskData -> }
                        onGetSystemEvent = { systemEvent -> }
                    """,
                        expectedExecute: true
                ),
                new TestDslConditionBlocks(
                        rules: """
                        ${randomInitBlock()}
                        onGetServiceMessage = { message ->
                            executeCondition ("Успешное выполнение функции") {
                                condition {
                                    true
                                }
                                execute {
                                    $execute
                                }
                            }
                        }
                        onGetLocalMessage = { localMessage -> }
                        onEndTask = { taskData -> }
                        onGetSystemEvent = { systemEvent -> }
                    """,
                        expectedExecute: true
                )
        ]
    }
    static class TestDslConditionBlocks {
        def rules
        def expectedExecute
    }
}

package objects
/**
 * @author Nikita Gorodilov
 */
class DslObjects {

    static final def a_testdslConditionEventName = "event"

    static final def notInitBlockDsl =
        """
            onGetServiceMessage = { serviceMessage -> }
            onGetLocalMessage = { localMessage -> }
            onLoadImage = { image -> }
            onEndImageTask = { updateImage -> }
        """

    static def allBlocksDslWithTypeParameterInInitBlock(typeParameter) {
        """
            init = {
                type = $typeParameter
                name = "${StringObjects.randomString()}"
                masId = "${StringObjects.randomString()}"
                defaultBodyType = "${StringObjects.randomString()}"
                defaultGoalType = "${StringObjects.randomString()}"
            }
            onGetServiceMessage = { serviceMessage -> }
            onGetLocalMessage = { localMessage -> }
            onLoadImage = { image -> }
            onEndImageTask = { updateImage -> }
        """
    }

    static def allBlocksDslWithInitParams(type, name, masId, bodyType) {
        """
            init = {
                type = "$type"
                name = "$name"
                masId = "$masId"
                defaultBodyType = "$bodyType"
            }
            onGetServiceMessage = { serviceMessage -> }
            onGetLocalMessage = { localMessage -> }
            onLoadImage = { image -> }
            onEndImageTask = { updateImage -> }
        """
    }

    static final def allBlocksDslArray = [
            "${randomInitBlock()}",
            "onGetServiceMessage = { message -> }",
            "onGetLocalMessage = { localMessage -> }",
            "onLoadImage = { image -> }",
            "onEndImageTask = { updateImage -> }"
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
            onLoadImage = {}
            onEndImageTask = {}
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
            onLoadImage = {}
            onEndImageTask = {}
        """
    }

    static def createDslWithExecuteConditionBlocks(onGetServiceMessageBlock, onGetLocalMessageBlock, onLoadImageBlock, onEndImageBlock) {
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

    static def createDslWithOnGetLoadImageBlock(executeConditionBlockBody) {
        """
            ${randomInitBlock()}
            onGetServiceMessage = {}
            onGetLocalMessage = {}
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
                onLoadImage = {}
                onEndImageTask = {}
            """
    }

    static def randomInitBlock() {
        """
            init = {
                type = "${TypesObjects.testAgent1TypeCode()}"
                name = "${StringObjects.randomString()}"
                masId = "${StringObjects.randomString()}"
                defaultBodyType = "${StringObjects.randomString()}"
            }
        """
    }

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
                        onLoadImage = {}
                        onEndImageTask = {}
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
                        onLoadImage = {}
                        onEndImageTask = {}
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
                        onLoadImage = {}
                        onEndImageTask = {}
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
                        onLoadImage = {}
                        onEndImageTask = {}
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
                        onLoadImage = {}
                        onEndImageTask = {}
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

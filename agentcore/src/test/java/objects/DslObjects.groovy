package objects

/**
 * @author Nikita Gorodilov
 */
class DslObjects {

    static final def notInitBlockDsl =
        """
            onGetMessage = { message -> }
            onLoadImage = { image -> }
            onEndImageTask = { updateImage -> }
        """

    static def allBlocksDslWithInitParams(type, name, masId) {
        """
            init = {
                type = "$type"
                name = "$name"
                masId = "$masId"
            }
            onGetMessage = { message -> }
            onLoadImage = { image -> }
            onEndImageTask = { updateImage -> }
        """
    }

    static final def allBlocksDsl =
        """
            ${randomInitBlock()}
            onGetMessage = { message -> }
            onLoadImage = { image -> }
            onEndImageTask = { updateImage -> }
        """

    static def createDslWithOnGetMessageExecuteConditionBlock(executeConditionBlockBody) {
        """
            ${randomInitBlock()}
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

    static def createDslWithOnGetMessageBlock(executeConditionBlockBody) {
        """
            ${randomInitBlock()}
            onGetMessage = {
                """ +
                executeConditionBlockBody +
                """
            }
            onLoadImage = {}
            onEndImageTask = {}
        """
    }

    static def createDslWithExecuteConditionBlocks(onGetMessageBlock, onLoadImageBlock, onEndImageBlock) {
        """
            ${randomInitBlock()}
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

    static def createDslWithOnGetLoadImageBlock(executeConditionBlockBody) {
        """
            ${randomInitBlock()}
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

    static def executeConditionDsl(condition, execute) {
        """
                ${randomInitBlock()}
                onGetMessage = {
                    executeCondition ("Успешное выполнение функции") {
                        condition() {
                            $condition
                        }
                        execute() {
                            $execute
                        }
                    }
                }
                onLoadImage = {}
                onEndImageTask = {}
            """
    }

    static def randomInitBlock() {
        """
            init = {
                type = "${TypesObjects.firstAgentTypeCodeStr()}"
                name = "${StringObjects.randomString()}"
                masId = "${StringObjects.randomString()}"
            }
        """
    }

    static def testDslConditionBlocksArray(execute) {
        [
                new TestDslConditionBlocks( // все блоки вернут да
                        rules: """
                        ${randomInitBlock()}
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
                                    $execute
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
                        ${randomInitBlock()}
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
                                    $execute
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
                        ${randomInitBlock()}
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
                                    $execute
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
                        ${randomInitBlock()}
                        onGetMessage = { message ->
                            executeCondition ("Успешное выполнение функции") {
                                execute {
                                    $execute
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
                        ${randomInitBlock()}
                        onGetMessage = { message ->
                            executeCondition ("Успешное выполнение функции") {
                                condition {
                                    true
                                }
                                execute {
                                    $execute
                                }
                            }
                        }
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

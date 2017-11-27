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

    static final def allBlocksDsl =
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

    static def createDslWithOnGetMessageExecuteConditionBlock(executeConditionBlockBody) {
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

    static def createDslWithOnGetMessageBlock(executeConditionBlockBody) {
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

    static def createDslWithExecuteConditionBlocks(onGetMessageBlock, onLoadImageBlock, onEndImageBlock) {
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

    static def createDslWithOnGetLoadImageBlock(executeConditionBlockBody) {
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

    static final def testTypeRule =
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

    static final def testDslConditionBlocksArray = [
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
    static class TestDslConditionBlocks {
        def rules
        def expectedExecute
    }
}

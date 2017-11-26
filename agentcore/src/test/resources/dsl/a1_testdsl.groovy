init = {
    type = "worker"
    name = "a1_testdsl"
    masId = "a1_testdsl"
}

onGetMessage = { message ->
    executeCondition ("Если пришло сообщение от серверного агента") {
        condition {
            message.senderCode.code == SERVER_AT
        }
        execute {
            a1_testOnGetMessageFun()
        }
    }
}

onLoadImage = { image ->
    executeCondition ("Наименование") {
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
            testOnLoadImage()
        }
    }
}

onEndImageTask = { updateImage ->
    executeCondition ("Наименование") {
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
            testOnEndImageTask()
        }
    }
}
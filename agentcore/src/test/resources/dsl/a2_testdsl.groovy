init = {
    type = "worker"
    name = "a2_testdsl"
    masId = "a2_testdsl"
}

onGetMessage = { message ->
    executeCondition ("Если пришло сообщение от рабочего агента") {
        condition {
            message.senderCode.code == WORKER_AT
        }
        execute {
            a2_testOnGetMessageFun()
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
init = {
    type = "worker"
    name = "name"
    masId = "masId"
}

onGetMessage = { message ->
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
            testOnGetMessageFun()
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
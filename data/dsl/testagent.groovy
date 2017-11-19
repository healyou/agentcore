init = {
    type = "worker"
    name = "name"
    masId = "masId"
}

onGetMessage = { message ->
    executeCondition ("Наименование") {
        allOf {
            condition {
                true
            }
            condition {
                true
            }
        }
        anyOf {
            condition {
                false
            }
            condition {
                true
            }
        }
        condition {
            true
        }
        execute {
            sendMessage()
            testImageFun1()
            println 'script onGetMessage onExecute'
        }
    }
    println 'message = ' + message
    println 'script onGetMessage'
}

onLoadImage = { image ->
    executeCondition ("Наименование") {
        allOf {
            condition {
                true
            }
            condition {
                true
            }
        }
        anyOf {
            condition {
                false
            }
            condition {
                true
            }
        }
        condition {
            true
        }
        execute {
            sendMessage()
            testImageFun2()
            println 'script onLoadImage onExecute'
        }
    }
    println 'image = ' + image
    println 'script onLoadImage'
}

onEndImageTask = { updateImage ->
    executeCondition ("Наименование") {
        allOf {
            condition {
                true
            }
            condition {
                true
            }
        }
        anyOf {
            condition {
                false
            }
            condition {
                true
            }
        }
        condition {
            true
        }
        execute {
            sendMessage()
            testImageFun3()
            println 'script onEndImageTask onExecute'
        }
    }
    println 'updateImage = ' + updateImage
    println 'script onEndImageTask'
}
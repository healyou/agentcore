init = {
    type = "worker"
    name = "name"
    masId = "masIdNew"
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
            testUpdateImageWithSleep image: image, sleep: 3000
            sendMessage messageType: SEARCH_SOLUTION_MT,
                    image: image,
                    agentTypes: ["worker", "server"]
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
            testImageFun3()
            println 'script onEndImageTask onExecute'
        }
    }
    println 'updateImage = ' + updateImage
    println 'script onEndImageTask'
}
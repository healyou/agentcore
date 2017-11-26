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
    executeCondition ("Выполним функцию над изображением") {
        condition {
            image != null
        }
        execute {
            a1_testOnLoadImageFun()
        }
    }
}

onEndImageTask = { updateImage ->
    executeCondition ("Выполним функцию над изображением") {
        condition {
            updateImage != null
        }
        execute {
            a1_testOnEndImageTaskFun()
        }
    }
}
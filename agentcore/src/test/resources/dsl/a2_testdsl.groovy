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
    executeCondition ("Выполним функцию над изображением") {
        condition {
            image != null
        }
        execute {
            a2_testOnLoadImageFun()
        }
    }
}

onEndImageTask = { updateImage ->
    executeCondition ("Выполним функцию над изображением") {
        condition {
            updateImage != null
        }
        execute {
            a2_testOnEndImageTaskFun()
        }
    }
}
init = {
    type = WORKER_AT
    name = "a1_testdsl"
    masId = "a1_testdsl"
}

onGetMessage = { message ->
    executeCondition ("Если пришло сообщение от серверного агента") {
        condition {
            message.senderType == SERVER_AT
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
            sendMessage messageType: "search_solution",
                    image: image,
                    agentTypes: ["worker", "server"]
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
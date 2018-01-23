/**
 * Работает с данными MockObjects - TypesObjects класса
 */

init = {
    type = TEST_AGENT_TYPE_2_AT
    name = "a2_testdsl"
    masId = "a2_testdsl"
    defaultBodyType = JSON_MBT
}

onGetServiceMessage = { message ->
    executeCondition ("Если пришло сообщение от 1 агента") {
        condition {
            message.senderType == TEST_AGENT_TYPE_1_AT
        }
        execute {
            a2_testOnGetServiceMessageFun()
        }
    }
}

onGetLocalMessage = { localMessage ->
    executeCondition ("Локальное сообщение агента") {
        condition {
            localMessage.event == "event"
        }
        execute {
            a2_testOnGetLocalMessageFun()
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
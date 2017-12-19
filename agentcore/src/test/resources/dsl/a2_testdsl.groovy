/**
 * Работает с данными MockObjects - TypesObjects класса
 */

init = {
    type = TEST_AGENT_TYPE_2_AT
    name = "a2_testdsl"
    masId = "a2_testdsl"
    defaultBodyType = JSON_MBT
}

onGetMessage = { message ->
    executeCondition ("Если пришло сообщение от 1 агента") {
        condition {
            message.senderType == TEST_AGENT_TYPE_1_AT
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
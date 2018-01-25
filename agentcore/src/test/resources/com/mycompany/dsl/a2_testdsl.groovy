/**
 * Работает с данными MockObjects - TypesObjects класса
 */

init = {
    type = TEST_AGENT_TYPE_2_AT
    name = "a2_testdsl"
    masId = "a2_testdsl"
    defaultBodyType = JSON_MBT
    localMessageTypes = ["a2_lmt_event1"]
    taskTypes = ["a2_tt1"]
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
            localMessage.event == A2_LMT_EVENT1_LMT && localMessage.event == "a2_lmt_event1"
        }
        execute {
            a2_testOnGetLocalMessageFun()
        }
    }
}

onEndTask = { taskData ->
    executeCondition ("Выполним функцию над изображением") {
        condition {
            taskData.type == A2_TT1_TT && taskData.type == "a2_tt1"
        }
        execute {
            a2_testOnEndTask()
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
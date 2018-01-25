/**
 * Работает с данными MockObjects - TypesObjects класса
 */

init = {
    type = TEST_AGENT_TYPE_1_AT
    name = "a1_testdsl"
    masId = "a1_testdsl"
    defaultBodyType = JSON_MBT
    localMessageTypes = ["a1_lmt_event1"]
    taskTypes = ["a1_tt1"]
}

onGetServiceMessage = { serviceMessage ->
    executeCondition ("Если пришло сообщение от 2 агента") {
        condition {
            serviceMessage.senderType == TEST_AGENT_TYPE_2_AT
        }
        execute {
            a1_testOnGetServiceMessageFun()
            sendServiceMessage messageType: "search_solution",
                    image: serviceMessage.image,
                    agentTypes: ["worker", "server"]
        }
    }
}

onGetLocalMessage = { localMessage ->
    executeCondition ("Локальное сообщение агента") {
        condition {
            localMessage.event == A1_LMT_EVENT1_LMT && localMessage.event == "a1_lmt_event1"
        }
        execute {
            a1_testOnGetLocalMessageFun()
            startTask (A1_TT1_TT) {
                testOnGetLocalMessageFun()
            }
        }
    }
}

onEndTask = { taskData ->
    executeCondition ("Выполним функцию над изображением") {
        condition {
            taskData.type == A1_TT1_TT && taskData.type == "a1_tt1"
        }
        execute {
            a1_testOnEndTask()
        }
    }
}
/**
 * Тестовый агент 2
 * При получении сообщения от Тестового агента 2
 * - обрабатывает изображение
 * - отправляет сообщение другому агенту(Тестовый агент 1)
 */

init = {
    type = INTEGRATION_TEST_AGENT_TYPE_2_AT
    name = "Тестовый агент 2"
    masId = "integration_test_agent_2_masId"
    defaultBodyType = JSON_MBT
    localMessageTypes = ["local_event_a2"]
    taskTypes = []
}

onGetServiceMessage = { serviceMessage ->
    println("7) Получение сообщения от первого агента")
    executeCondition ("Если пришло сообщение от 1 агента") {
        condition {
            serviceMessage.senderType == INTEGRATION_TEST_AGENT_TYPE_1_AT &&
                    serviceMessage.messageBody == "a1_message_body"
        }
        execute {
            println("8) Вызов функции из библиотеки функций агента testLibOnGetServiceMessageA2")
            testLibOnGetServiceMessageA2()
        }
    }
}

onGetLocalMessage = { localMessage ->
    println("9) Получение локального сообщения")
    executeCondition ("Локальное сообщение агента") {
        condition {
            localMessage.event == LOCAL_EVENT_A2_LMT
        }
        execute {
            println("10) Отправка сообщения первому агенту")
            sendServiceMessage messageType: INTEGRATION_TEST_MESSAGE_TYPE_2_TEST_GOAL_1_SMT,
                    messageBody: "a2_message_body",
                    agentTypes: [INTEGRATION_TEST_AGENT_TYPE_1_AT]
        }
    }
}

onGetSystemEvent = { systemEvent ->
}

onEndTask = { taskData ->
}
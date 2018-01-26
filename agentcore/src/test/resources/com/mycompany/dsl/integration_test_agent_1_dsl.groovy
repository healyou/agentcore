/**
 * Тестовый агент 1
 * При загрузке изображения
 * - обрабатывает изображение
 * - отправляет сообщение другому агенту(Тестовый агент 2)
 * При получении сообщения от Тестового агента 2
 * - проверка и конец
 */

init = {
    type = INTEGRATION_TEST_AGENT_TYPE_1_AT
    name = "Тестовый агент 1"
    masId = "integration_test_agent_1_masId"
    defaultBodyType = JSON_MBT
    localMessageTypes = ["local_event_a1"]
    taskTypes = ["task_type_a1"]
}

onGetSystemEvent = { systemEvent ->
    println("1) Начало работы агента")
    executeCondition ("Начало работы агента") {
        condition {
            systemEvent.code == AGENTSTART_SE
        }
        execute {
            println("2) Вызов функции из библиотеки функций агента testLibOnAgentStartA1")
            testLibOnAgentStartA1()
        }
    }
}

onGetLocalMessage = { localMessage ->
    println("3) Получение локального сообщения")
    executeCondition ("Локальное сообщение агента") {
        condition {
            localMessage.event == LOCAL_EVENT_A1_LMT
        }
        execute {
            startTask (TASK_TYPE_A1_TT) {
                println("4) Начало выполнения задачи - функция агента testLibOnStartTaskA1")
                // todo - тут надо передавать id агента
                testLibOnStartTaskA1()
            }
        }
    }
}

onEndTask = { taskData ->
    println("5) Завершение задачи агента")
    executeCondition ("Выполним функцию над изображением") {
        condition {
            taskData.type == TASK_TYPE_A1_TT
        }
        execute {
            println("6) Отправка сообщения второму агенту")
            sendServiceMessage messageType: INTEGRATION_TEST_MESSAGE_TYPE_1_TEST_GOAL_1_SMT,
                    messageBody: "a1_message_body",
                    agentTypes: [INTEGRATION_TEST_AGENT_TYPE_2_AT]
        }
    }
}

onGetServiceMessage = { serviceMessage ->
    println("11) Получение сообщения от второго агента")
    executeCondition ("Если пришло сообщение от 2 агента") {
        condition {
            serviceMessage.senderType == INTEGRATION_TEST_AGENT_TYPE_2_AT &&
                    serviceMessage.messageBody == "a2_message_body"
        }
        execute {
            println("12) Завершение теста")
        }
    }
}
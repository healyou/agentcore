/**
 * Тестовый агент 1(Очерёдность действий)
 * При системном события начала работы агента
 *      вызываем функцию из библиотеки функций агента
 * При получении локального сообщения(от вызова функции из библиотеки)
 *      начинаем новую задачу с вызовом функции из либы агента
 * По завершению задачи
 *      Отправляем сообщение второму агента
 * При получении сообщения от 2го агента
 *      Конец работы
 */

init = {
    type = MANUAL_TEST_AGENT_TYPE_1_AT
    name = "Тестовый агент 1(Ручное тестировние)"
    masId = "manual_test_agent_1_masId"
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
            testLibOnAgentStartA1 agent.id
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
                testLibOnStartTaskA1 agent.id
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
            sendServiceMessage messageType: MANUAL_TEST_MESSAGE_TYPE_1_TEST_GOAL_2_SMT,
                    messageBody: "a1_message_body",
                    agentTypes: [MANUAL_TEST_AGENT_TYPE_2_AT]
        }
    }
}

onGetServiceMessage = { serviceMessage ->
    println("11) Получение сообщения от второго агента")
    executeCondition ("Если пришло сообщение от 2 агента") {
        condition {
            serviceMessage.senderType == MANUAL_TEST_AGENT_TYPE_2_AT &&
                    serviceMessage.messageBody == "a2_message_body"
        }
        execute {
            println("12) Завершение теста")
        }
    }
}
/**
 * Тестовый агент 2
 * При получении сообщения от Тестового агента 2
 * - обрабатывает изображение
 * - отправляет сообщение другому агенту(Тестовый агент 1)
 */

init = {
    type = TEST_AGENT_TYPE_2_AT
    name = "Тестовый агент 2"
    masId = "test_agent_2_masId"
    defaultBodyType = JSON_MBT
    defaultGoalType = TEST_MESSAGE_GOAL_TYPE_1_MGT
}

onGetMessage = { message ->
    executeCondition ("Если пришло сообщение от первого серверного агента") {
        condition {
            message.senderType == TEST_AGENT_TYPE_1_AT
        }
        execute {
            println "3) Получение сообщения с сервиса от первого тестового агента вторым тестовым агентом. Работа над изображением"
            testUpdateImageWithSleep image: message.image, sleep: 3000
        }
    }
}

onLoadImage = { image ->
    executeCondition ("Обновим изображение") {
        execute {
            println "execute onLoadImage test_agent_type_2"
        }
    }
}

onEndImageTask = { updateImage ->
    executeCondition ("Отправим сообщение первому тестовому агенту") {
        execute {
            println "4) Работы над изображением закончена. Отправка сообщения первому тестовому агенту вторым тестовым агентов"
            sendMessage messageType: SEARCH_SOLUTION_MT,
                    image: updateImage,
                    agentTypes: [TEST_AGENT_TYPE_1_AT]
        }
    }
}
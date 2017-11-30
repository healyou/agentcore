/**
 * Тестовый агент 2
 * При получении сообщения от Тестового агента 2
 * - обрабатывает изображение
 * - отправляет сообщение другому агенту(Тестовый агент 1)
 */

init = {
    type = "test_agent_type_2"
    name = "Тестовый агент 2"
    masId = "test_agent_2_masId"
}

onGetMessage = { message ->
    executeCondition ("Если пришло сообщение от второго серверного агента") {
        condition {
            message.senderType == TEST_AGENT_TYPE_1_AT
        }
        execute {
            println "execute onGetMessage test_agent_type_2"
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
    executeCondition ("Отправим сообщение второму тестовому агенту") {
        execute {
            println "execute onEndImageTask test_agent_type_2"
            sendMessage messageType: SEARCH_SOLUTION_MT,
                    image: updateImage,
                    agentTypes: [TEST_AGENT_TYPE_1_AT]
        }
    }
}
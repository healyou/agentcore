/**
 * Тестовый агент 1
 * При загрузке изображения
 * - обрабатывает изображение
 * - отправляет сообщение другому агенту(Тестовый агент 2)
 * При получении сообщения от Тестового агента 2
 * - проверка и конец
 */

init = {
    type = "test_agent_type_1"
    name = "Тестовый агент 1"
    masId = "test_agent_1_masId"
}

onLoadImage = { image ->
    executeCondition ("Обновим изображение") {
        condition {
            image != null
        }
        execute {
            testUpdateImageWithSleep image: image, sleep: 3000
        }
    }
}

onEndImageTask = { updateImage ->
    executeCondition ("Отправим сообщение второму тестовому агенту") {
        condition {
            updateImage != null
        }
        execute {
            sendMessage messageType: SEARCH_SOLUTION_MT,
                    image: updateImage,
                    agentTypes: [TEST_AGENT_TYPE_2_AT]
        }
    }
}

onGetMessage = { message ->
    executeCondition ("Если пришло сообщение от второго серверного агента") {
        condition {
            message.senderCode.code == TEST_AGENT_TYPE_2_AT
        }
        execute {
            println("УСПЕХ - ПРИШЛО СООБЩЕНИЕ ОТ 2ГО ТЕСТОВОГО АГЕНТА")
        }
    }
}
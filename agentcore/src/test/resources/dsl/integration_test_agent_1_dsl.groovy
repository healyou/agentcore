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

onGetMessage = { message ->
    executeCondition ("Если пришло сообщение от серверного агента") {
        condition {
            message.senderCode.code == SERVER_AT
        }
        execute {
            a1_testOnGetMessageFun()
        }
    }
}

onLoadImage = { image ->
    executeCondition ("Обновить изображение") {
        execute {
            testUpdateImageWithSleep(image, 3000)
        }
    }
}

onEndImageTask = { updateImage ->
    executeCondition ("Выполним функцию над изображением") {
        condition {
            updateImage != null
        }
        execute {
            a1_testOnEndImageTaskFun()
        }
    }
}
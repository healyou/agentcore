init = {
    type = MANUAL_TEST_AGENT_TYPE_2_AT
    name = "Тестовый агент 2(Ручное тестировние)"
    masId = "manual_test_agent_2_masId"
    defaultBodyType = JSON_MBT
    defaultGoalType = MANUAL_TEST_MESSAGE_GOAL_TYPE_1_MGT
}

onGetMessage = { message ->
    executeCondition 'Вызов функции 1', {
        execute {
            testImageFun1()
        }
    }
}

onLoadImage = { image ->
    executeCondition 'Вызов функции 2', {
        execute {
            sendMessage messageType: MANUAL_TEST_MESSAGE_TYPE_2_TEST_GOAL_2_MT,
                    image: image,
                    agentTypes: [MANUAL_TEST_AGENT_TYPE_1_AT]
            testImageFun2()
        }
    }
}

onEndImageTask = { updateImage ->
    executeCondition 'Вызов функции 3', {
        execute {
            testImageFun3()
        }
    }
}
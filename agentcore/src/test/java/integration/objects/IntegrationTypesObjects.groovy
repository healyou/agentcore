package integration.objects

import com.mycompany.service.objects.AgentType
import com.mycompany.service.objects.MessageBodyType
import com.mycompany.service.objects.MessageGoalType
import com.mycompany.service.objects.MessageType

/**
 * Типы данных находящиеся на сервисе для интеграционного тестирования
 *
 * @author Nikita Gorodilov
 */
class IntegrationTypesObjects {

    static AgentType testAgentType1() {
        new AgentType(1L, "integration_test_agent_type_1", "Тип тестового агента 1(Интеграционные тесты)", false)
    }

    static AgentType testAgentType2() {
        new AgentType(2L, "integration_test_agent_type_2", "Тип тестового агента 2(Интеграционные тесты)", false)
    }

    static def testAgent1TypeCode() {
        testAgentType1().code
    }

    static def testAgent2TypeCode() {
        testAgentType2().code
    }

    static final def agentTypes = Arrays.asList(
            testAgentType1(),
            testAgentType2()
    )

    static final def messageBodyTypes = Arrays.asList(
            new MessageBodyType(1L, "json", "Тело сообщения формата Json", false)
    )

    static final def messageGoalTypes = Arrays.asList(
            new MessageGoalType(1L, "integration_test_message_goal_type_1", "Тестовая цель общения 1(Интеграционные тесты)", false)
    )

    static final def messageTypes = Arrays.asList(
            new MessageType(
                    1L, "integration_test_message_type_1_test_goal_1",
                    "Тестовый тип сообщения 1 для тестовой цели 1(Интеграционные тесты)",
                    1, messageGoalTypes.get(0),
                    false
            ),
            new MessageType(
                    2L, "integration_test_message_type_2_test_goal_1",
                    "Тестовый тип сообщения 2 для тестовой цели 1(Интеграционные тесты)",
                    2, messageGoalTypes.get(0),
                    false
            ),
            new MessageType(
                    3L, "test_message_type_3",
                    "Ответ на запрос решения задачи", 3,
                    messageGoalTypes.get(0),
                    false
            ),
            new MessageType(4L, "test_message_type_4", "Ответ на задачу", 4, messageGoalTypes.get(0), false)
    )
}

package objects

import com.mycompany.service.objects.AgentType
import com.mycompany.service.objects.MessageBodyType
import com.mycompany.service.objects.MessageGoalType
import com.mycompany.service.objects.MessageType

/**
 * @author Nikita Gorodilov
 */
class TypesObjects {

    static AgentType testAgentType1() {
        new AgentType(1L, "test_agent_type_1", "Первый тип агента", false)
    }

    static AgentType testAgentType2() {
        new AgentType(2L, "test_agent_type_2", "Второй тип агента", false)
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
            new MessageGoalType(1L, "task_decision", "Решение задачи", false)
    )

    static final def messageTypes = Arrays.asList(
            new MessageType(1L, "test_message_type_1", "Поиск решения задачи", 1, messageGoalTypes.get(0), false),
            new MessageType(2L, "test_message_type_2", "Поиск решения", 2, messageGoalTypes.get(0), false),
            new MessageType(3L, "test_message_type_3", "Ответ на запрос решения задачи", 3, messageGoalTypes.get(0), false),
            new MessageType(4L, "test_message_type_4", "Ответ на задачу", 4, messageGoalTypes.get(0), false)
    )
}

package objects

import service.objects.AgentType
import service.objects.MessageBodyType
import service.objects.MessageGoalType
import service.objects.MessageType

/**
 * @author Nikita Gorodilov
 */
class TypesObjects {

    static final def agentTypes = Arrays.asList(
            new AgentType(1L, AgentType.Code.SERVER, "Рабочий агент", false),
            new AgentType(2L, AgentType.Code.WORKER, "Серверный агент", false)
    )

    static final def messageBodyTypes = Arrays.asList(
            new MessageBodyType(1L, MessageBodyType.Code.JSON, "Тело сообщения формата Json", false)
    )

    static final def messageGoalTypes = Arrays.asList(
            new MessageGoalType(1L, MessageGoalType.Code.TASK_DECISION, "Решение задачи", false)
    )

    static final def messageTypes = Arrays.asList(
            new MessageType(1L, MessageType.Code.SEARCH_TASK_SOLUTION, "Поиск решения задачи", 1, messageGoalTypes.get(0), false),
            new MessageType(2L, MessageType.Code.SEARCH_SOLUTION, "Поиск решения", 2, messageGoalTypes.get(0), false),
            new MessageType(3L, MessageType.Code.SOLUTION_ANSWER, "Ответ на запрос решения задачи", 3, messageGoalTypes.get(0), false),
            new MessageType(4L, MessageType.Code.TASK_SOLUTION_ANSWER, "Ответ на задачу", 4, messageGoalTypes.get(0), false)
    )
}

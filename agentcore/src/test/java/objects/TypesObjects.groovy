package objects

import service.objects.AgentType
import service.objects.MessageBodyType
import service.objects.MessageGoalType
import service.objects.MessageType

/**
 * @author Nikita Gorodilov
 */
class TypesObjects {

    static def firstAgentType() {
        new AgentType(1L, AgentType.Code.values()[0], "первый тип", false)
    }

    static def firstAgentTypeCodeStr() {
        firstAgentType().code.code
    }

    static final def agentTypes = Arrays.asList(
            new AgentType(1L, AgentType.Code.server, "Рабочий агент", false),
            new AgentType(2L, AgentType.Code.worker, "Серверный агент", false)
    )

    static final def messageBodyTypes = Arrays.asList(
            new MessageBodyType(1L, MessageBodyType.Code.json, "Тело сообщения формата Json", false)
    )

    static final def messageGoalTypes = Arrays.asList(
            new MessageGoalType(1L, MessageGoalType.Code.task_decision, "Решение задачи", false)
    )

    static final def messageTypes = Arrays.asList(
            new MessageType(1L, MessageType.Code.search_task_solution, "Поиск решения задачи", 1, messageGoalTypes.get(0), false),
            new MessageType(2L, MessageType.Code.search_solution, "Поиск решения", 2, messageGoalTypes.get(0), false),
            new MessageType(3L, MessageType.Code.solution_answer, "Ответ на запрос решения задачи", 3, messageGoalTypes.get(0), false),
            new MessageType(4L, MessageType.Code.task_solution_answer, "Ответ на задачу", 4, messageGoalTypes.get(0), false)
    )
}

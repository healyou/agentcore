package service.objects

import agentcore.utils.Codable
import com.fasterxml.jackson.annotation.JsonCreator
import com.fasterxml.jackson.annotation.JsonProperty

/**
 * Тип сообщения - зависит от цели сообщения
 *
 * @author Nikita Gorodilov
 */
class MessageType @JsonCreator constructor (
        @JsonProperty("id")
        override var id: Long?,
        @JsonProperty("code")
        var code: Code,
        @JsonProperty("name")
        var name: String,
        @JsonProperty("messageOrder")
        var messageOrder: Long,
        @JsonProperty("messageGoalType")
        var messageGoalType: MessageGoalType,
        @JsonProperty("deleted")
        var isDeleted: Boolean
): Entity {

    /* Типы сообщения - для каждого MessageGoalType.Code тут сделать свой codable */
    enum class Code(override val code: String): Codable<String> {
        /* Связанные с MessageGoalType.Code.TASK_DECISION */
        SEARCH_TASK_SOLUTION("search_task_solution"),
        SEARCH_SOLUTION("search_solution"),
        SOLUTION_ANSWER("solution_answer"),
        TASK_SOLUTION_ANSWER("task_solution_answer");

        /* Связанные с ... */
    }
}
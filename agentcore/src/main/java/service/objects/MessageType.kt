package service.objects

import db.base.Codable
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
        @JsonProperty("order")
        var order: Long,
        @JsonProperty("goalType")
        var goalType: MessageGoalType,
        @JsonProperty("deleted")
        var isDeleted: Boolean
): Entity {

    /* Типы сообщения - для каждого MessageGoalType.Code тут сделать свой codable */
    enum class Code(override val code: String): Codable<String> {
        /* Связанные с MessageGoalType.Code.task_decision */
        search_task_solution("search_task_solution"),
        search_solution("search_solution"),
        solution_answer("solution_answer"),
        task_solution_answer("task_solution_answer");

        /* Связанные с ... */
    }
}
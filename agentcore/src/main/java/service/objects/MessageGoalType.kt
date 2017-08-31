package service.objects

import agentcore.utils.Codable
import com.fasterxml.jackson.annotation.JsonCreator
import com.fasterxml.jackson.annotation.JsonProperty

/**
 * Тип цели сообщения
 *
 * @author Nikita Gorodilov
 */
class MessageGoalType @JsonCreator constructor (
        @JsonProperty("id")
        override var id: Long?,
        @JsonProperty("code")
        var code: Code,
        @JsonProperty("name")
        var name: String,
        @JsonProperty("deleted")
        var isDeleted: Boolean
): Entity {

    /* Типы тела сообщения */
    enum class Code(override val code: String): Codable<String> {
        TASK_DECISION("task_decision");
    }
}
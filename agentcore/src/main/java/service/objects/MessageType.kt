package service.objects

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
        var code: String,
        @JsonProperty("name")
        var name: String,
        @JsonProperty("order")
        var order: Long,
        @JsonProperty("goalType")
        var goalType: MessageGoalType,
        @JsonProperty("deleted")
        var isDeleted: Boolean
): Entity
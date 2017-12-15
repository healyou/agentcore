package service.objects

import db.base.Codable
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

    // TODO - любое добавление типа в сервисе необходимо добавлять тип в ENUM - надо как то от этого избавиться

    /* Типы тела сообщения */
    enum class Code(override val code: String): Codable<String> {
        task_decision("task_decision"),
        test_message_goal_type_1("test_message_goal_type_1");
    }
}
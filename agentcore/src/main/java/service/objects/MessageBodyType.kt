package service.objects

import agentcore.utils.Codable
import com.fasterxml.jackson.annotation.JsonCreator
import com.fasterxml.jackson.annotation.JsonProperty

/**
 * Тип тела сообщения
 *
 * @author Nikita Gorodilov
 */
class MessageBodyType @JsonCreator constructor (
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
        JSON("json");
    }
}
package service.objects

import com.fasterxml.jackson.annotation.JsonCreator
import com.fasterxml.jackson.annotation.JsonProperty
import java.util.*

/**
 * @author Nikita Gorodilov
 */
class Message @JsonCreator constructor (
        /* Идентификатор сообщения */
        @JsonProperty("id")
        override var id: Long? = null,
        /* Отправитель сообщения */
        @JsonProperty("sender")
        var sender: Agent? = null,
        /* Получатели сообщения */
        @JsonProperty("recipients")
        var recipients: List<MessageRecipient>? = null,
        /* Цель сообщения */
        @JsonProperty("goalType")
        var goalType: MessageGoalType? = null,
        /* Цель сообщения */
        @JsonProperty("type")
        var type: MessageType? = null,
        /* Дата создания */
        @JsonProperty("createDate")
        var createDate: Date? = null,
        /* Тип тела сообщения */
        @JsonProperty("bodyType")
        var bodyType: MessageBodyType? = null,
        /* Тело сообщения */
        @JsonProperty("body")
        var body: String
): Entity
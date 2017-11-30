package service.objects

import com.fasterxml.jackson.annotation.JsonCreator
import com.fasterxml.jackson.annotation.JsonIgnore
import com.fasterxml.jackson.annotation.JsonProperty
import java.util.*

/**
 * Получатель сообщения - привязан к сущности Message
 *
 * @author Nikita Gorodilov
 */
class MessageRecipient @JsonCreator constructor (
        /* Идентификатор записи */
        @JsonProperty("id")
        override var id: Long?,
        /* Получатель сообщения */
        @JsonProperty("recipient")
        var recipient: Agent,
        /* Дата просмотра пользователем */
        @JsonProperty("viewedDate")
        var viewedDate: Date?
): Entity {

    /* Просмотрено ли сообщение */
    val isViewed
        @JsonIgnore
        get() = viewedDate != null
}
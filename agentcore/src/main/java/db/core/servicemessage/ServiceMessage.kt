package db.core.servicemessage

import service.objects.Entity
import java.util.*

/**
 * Сообщение, получаемое или отправляемое агентом на сервис
 *
 * @author Nikita Gorodilov
 */
open class ServiceMessage(
        /* Объект получаемый или передаваемый в сообщении */
        var jsonObject: String,
        /* Тип сообщения */
        var serviceMessageType: ServiceMessageType,
        /* Агент, которому принадлежит сообщение */
        var systemAgentId: Long
) : Entity {
    /* Идентификатор */
    override var id: Long? = null
    /* Дата создания сообщения */
    var createDate: Date? = null
    /* Дата использования(отправки или чтения агентом) */
    var useDate: Date? = null
    /* Тип отправителя сообщения(получение сообщения) */
    var getMessageSenderCode: String? = null
    /* Типы агентов, которым отправляется сообщение */
    var sendAgentTypeCodes: List<String>? = null
    /* Тип сообщения на Rest сервисе(отправка сообщения) */
    var sendMessageType: String? = null
    /* Тип тела сообщения(отправка сообщения) */
    var sendMessageBodyType: String? = null

    /**
     * Использовалось ли уже данные сообщение
     */
    val isUse
        get() = useDate != null
}
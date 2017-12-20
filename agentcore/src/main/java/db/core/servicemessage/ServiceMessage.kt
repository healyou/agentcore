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
        /* Типы агентов, которым отправляется сообщение */
        var sendAgentTypeCodes: List<String>,
        /* Агент, которому принадлежит сообщение */
        var systemAgentId: Long
) : Entity {
    /* Идентификатор */
    override var id: Long? = null
    /* Дата создания сообщения */
    var createDate: Date? = null
    /* Дата использования(отправки или чтения агентом) */
    var useDate: Date? = null
    /* Тип отправителя сообщения */
    var senderCode: String? = null
    /* Тип сообщения на Rest сервисе */
    var messageType: String? = null
    /* Тип тела сообщения */
    var messageBodyType: String? = null

    /**
     * Использовалось ли уже данные сообщение
     */
    val isUse
        get() = useDate != null
}
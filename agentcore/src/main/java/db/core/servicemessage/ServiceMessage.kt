package db.core.servicemessage

import service.objects.Entity
import java.util.*

/**
 * Сообщение, получаемое или отправляемое агентом на сервис
 *
 * @author Nikita Gorodilov
 */
class ServiceMessage(
        /* Объект получаемый или передаваемый в сообщении */
        var jsonObject: String,
        /* Тип json объекта */
        var objectType: ServiceMessageObjectType,
        /* Тип сообщения */
        var messageType: ServiceMessageType
) : Entity {
    /* Идентификатор */
    override var id: Long? = null
    /* Дата создания сообщения */
    var createDate: Date? = null
    /* Дата использования(отправки или чтения агентом) */
    var useDate: Date? = null
}
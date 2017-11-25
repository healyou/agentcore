package db.core.servicemessage

import service.objects.AgentType
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
        /* Тип json объекта */
        var objectType: ServiceMessageObjectType,
        /* Тип сообщения */
        var messageType: ServiceMessageType,
        /* Типы агентов, которым отправляется сообщение */
        var sendAgentTypeCodes: List<AgentType.Code>,
        /* Агент, которому принадлежит сообщение */
        var systemAgentId: Long // todo - дописать работу с системными агентами
) : Entity {
    /* Идентификатор */
    override var id: Long? = null
    /* Дата создания сообщения */
    var createDate: Date? = null
    /* Дата использования(отправки или чтения агентом) */
    var useDate: Date? = null

    /**
     * Использовалось ли уже данные сообщение
     */
    val isUse
        get() = useDate != null
}
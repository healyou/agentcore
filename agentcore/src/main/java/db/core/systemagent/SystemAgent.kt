package db.core.systemagent

import service.objects.AgentType
import service.objects.Entity
import java.util.*

/**
 * Локально работающий агент
 *
 * @author Nikita Gorodilov
 */
class SystemAgent(
        /* Логин от сервиса агентов */
        var serviceLogin: String,
        /* Пароль от сервиса агентов */
        var servicePassword: String,
        /* Типы агентов, которым отправляется сообщение */
        var sendAgentTypeCodes: List<AgentType.Code>,
        /* Нужно ли получать сообщения от сервиса агентов */
        var isSendAndGetMessages: Boolean
) : Entity {

    /* Идентификатор */
    override var id: Long? = null
    /* Дата создания сообщения */
    var createDate: Date? = null
    /* Дата изменения */
    var updateDate: Date? = null
    /* Удалено ли значение */
    var isDeleted: Boolean? = null

    override fun toString(): String {
        return "Агент{$id} $serviceLogin"
    }
}
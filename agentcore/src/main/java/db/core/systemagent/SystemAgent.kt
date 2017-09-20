package db.core.systemagent

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
        var sendAgentTypeCodes: String,
        /* Нужно ли получать сообщения от сервиса агентов */
        var isSendAndGetMessages: Boolean? = null
) : Entity {
    /* Идентификатор */
    override var id: Long? = null
    /* Дата создания сообщения */
    var createDate: Date? = null
    /* Дата изменения */
    var updateDate: Date? = null
    /* Удалено ли значение */
    var isDeleted: Boolean? = null

    /**
     * Использовалось ли уже данные сообщение
     */
    val useInServiceTask
        get() = isSendAndGetMessages == true
}
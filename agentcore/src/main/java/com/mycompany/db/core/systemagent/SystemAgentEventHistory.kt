package com.mycompany.db.core.systemagent

import com.mycompany.service.objects.Entity
import java.util.*

/**
 * История поведения агента
 *
 * @author Nikita Gorodilov
 */
class SystemAgentEventHistory(
        /* Логин от сервиса агентов */
        var systemAgentId: Long,
        /* Информационное сообщение */
        var message: String
) : Entity {

    /* Идентификатор */
    override var id: Long? = null
    /* Дата создания */
    var createDate: Date? = null
}
package com.mycompany.db.core.systemagent

import com.mycompany.db.core.file.dslfile.DslFileAttachment
import com.mycompany.service.objects.Entity
import com.mycompany.user.User
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
        /* Нужно ли получать сообщения от сервиса агентов */
        var isSendAndGetMessages: Boolean,
        /* Владелец агента */
        var owner: User,
        /* Создатель агента */
        var createUser: User
) : Entity {

    /* Идентификатор */
    override var id: Long? = null
    /* Дата создания сообщения */
    var createDate: Date? = null
    /* Дата изменения */
    var updateDate: Date? = null
    /* Файл для работы агента */
    var dslFile: DslFileAttachment? = null
    /* Удалено ли значение */
    var isDeleted: Boolean? = null
}
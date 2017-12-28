package user

import service.objects.Entity
import java.util.*

/**
 * @author Nikita Gorodilov
 */
class User(
        /* Логин в системе */
        var login: String,
        /* Пароль в системе */
        var password: String
) : Entity {

    /* Идентификатор */
    override var id: Long? = null
    /* Дата создания пользователя */
    var createDate: Date? = null
    /* Дата завершения работы пользователя в системе */
    var endDate: Date? = null
    /* Удалён ли пользователь */
    val isDeleted: Boolean
        get() = endDate != null
}
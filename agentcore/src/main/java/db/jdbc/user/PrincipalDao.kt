package db.jdbc.user

import user.Principal
import java.util.*

/**
 * @author Nikita Gorodilov
 */
interface PrincipalDao {

    /**
     * Получить пользователя по имени
     *
     * @param login логин для входа пользователя
     * @return пользователь
     */
    fun getPrincipal(login: String): Principal
}
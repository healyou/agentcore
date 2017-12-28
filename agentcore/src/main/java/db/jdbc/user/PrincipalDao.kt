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
     * @param username имя пользователя
     * @return пользователь
     */
    fun getPrincipal(username: String): Principal
}
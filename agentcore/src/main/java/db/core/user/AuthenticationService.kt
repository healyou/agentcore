package db.core.user

import dsl.exceptions.AuthenticationException
import user.Principal

/**
 * @author Nikita Gorodilov
 */
interface AuthenticationService {

    /**
     * Авторизация пользователя
     */
    @Throws(AuthenticationException::class)
    fun authenticate(username: String, password: String): Principal
}
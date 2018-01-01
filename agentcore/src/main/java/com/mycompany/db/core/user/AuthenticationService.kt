package com.mycompany.db.core.user

import com.mycompany.dsl.exceptions.AuthenticationException
import com.mycompany.user.Principal

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
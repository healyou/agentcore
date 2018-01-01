package com.mycompany.db.jdbc.user

import com.mycompany.user.Principal

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